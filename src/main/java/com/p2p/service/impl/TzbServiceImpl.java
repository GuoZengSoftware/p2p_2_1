package com.p2p.service.impl;

import com.p2p.bean.*;
import com.p2p.calc.*;
import com.p2p.common.BeanCopyUtils;
import com.p2p.common.Pager;
import com.p2p.common.ServerResponse;
import com.p2p.dao.*;
import com.p2p.enums.BorrowStatusEnum;
import com.p2p.enums.HKStatusEnum;
import com.p2p.enums.WayEnum;
import com.p2p.service.TzbService;
import com.p2p.vo.BorrowApplyDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 7025 on 2017/12/25.
 */
@Service
public class TzbServiceImpl extends AbstractServiceImpl implements TzbService {

    private TzbMapper tzbMapper;

    private BorrowApplyMapper borrowApplyMapper;

    private BorrowDetailMapper borrowDetailMapper;

    private UserMoneyMapper userMoneyMapper;

    private RewardMapper rewardMapper;

    private RewardSettingMapper rewardSettingMapper;

    private HkbMapper hkbMapper;

    private ShborrowMapper shborrowMapper;

    @Override
    public Pager listPagerCriteria(int pageNo, int pageSize, Object obj) {
        Pager pager = new Pager(pageNo, pageSize);
        pager.setRows(tzbMapper.listPagerCriteria(pager, obj));
        pager.setTotal(tzbMapper.countCriteria(obj));
        return pager;
    }

    @Override
    public Pager getUserInvest(Integer page, Integer limit, Object obj) {
        Pager pager = new Pager(page, limit);
        pager.setRows(tzbMapper.getUserInvest(pager, obj));
        pager.setTotal(tzbMapper.countUserInvest(obj));
        return pager;
    }

    @Override
    public Pager getAdminInvest(Integer page, Integer limit, Object obj) {
        Pager pager = new Pager(page, limit);
        pager.setRows(tzbMapper.getAdminInvest(pager, obj));
        pager.setTotal(tzbMapper.countAdminInvest(obj));
        return pager;
    }

    @Override
    @Transactional
    public ServerResponse<Integer> save(Object obj) {
        Tzb tzb = (Tzb)obj;
        BorrowApplyDetail bAD = (BorrowApplyDetail)borrowApplyMapper.getById(tzb.getBaid());
        if(bAD.getUid().equals(tzb.getUid())) {
            return ServerResponse.createByError("投资失败！不能给自己投资");
        }
        tzb.setJuid(bAD.getUid());
        tzb.setNprofit(bAD.getNprofit());
        tzb.setCpname(bAD.getCpname());
        tzb.setTztime(Calendar.getInstance().getTime());
        tzbMapper.save(tzb);
        // tzb.getResint1()为还款期数
        Integer month = tzb.getResint1();
        //修改投资人的资产
        UserMoney userMoney = userMoneyMapper.getUserMoney(tzb.getUid());
        //可用余额小于投资金额
        if(userMoney.getKymoney().compareTo(tzb.getMoney()) == -1) {
            return ServerResponse.createByError("余额不足！请充值");
        }
        userMoney.setTzmoney(userMoney.getTzmoney().add(tzb.getMoney()));
        //获取投资总额所对应的投资奖励百分比
        Double percent = rewardSettingMapper.getPercent(userMoney.getTzmoney());
        BigDecimal rewardMoney = userMoney.getTzmoney().multiply(BigDecimal.valueOf(percent / 100)).setScale(2, BigDecimal.ROUND_HALF_UP);
        //添加投资奖励记录
        Reward reward = new Reward();
        reward.setMoney(rewardMoney);
        reward.setUid(tzb.getUid());
        reward.setTmoney(userMoney.getTzmoney());
        rewardMapper.save(reward);
        // 更新用户资产
        userMoney.setKymoney(userMoney.getKymoney().subtract(tzb.getMoney()));
        //初始化收益
        Float yearNpro = bAD.getNprofit();
        //月利率
        Float monthNpro = yearNpro / 12;
        BigDecimal syMoney = BigDecimal.valueOf(0);
        //一次还清和先息后本的用户收益计息方式
        if(bAD.getWay().equals(WayEnum.PAYOFF_ONCE.getCode())|| bAD.getWay().equals(WayEnum.XIAN_XI.getCode())) {
            //投资金额乘以年利率
            syMoney = tzb.getMoney().multiply(BigDecimal.valueOf(yearNpro/100))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        // 等额本金的用户收益计息
        if(bAD.getWay().equals(WayEnum.EQUAL_BJ.getCode())) {
            syMoney = new ACMLoanCalculator().calLoan(tzb.getMoney(), month, yearNpro, LoanUtil.RATE_TYPE_YEAR).getTotalInterest();
        }
        //等额本息的用户收益计息
        if(bAD.getWay().equals(WayEnum.EQUAL_BX.getCode())) {
            syMoney = new ACPIMLoanCalculator().calLoan(tzb.getMoney(), month,yearNpro,LoanUtil.RATE_TYPE_YEAR).getTotalInterest();
        }
        //用户待收总额等于原待收加（投资加用户利息收益）   .setScale(2, BigDecimal.ROUND_HALF_UP)设置精度为两位小数点
        userMoney.setDsmoney(userMoney.getDsmoney().add(tzb.getMoney().add(syMoney)));
        //用户收益总额等于原收益总额加（用户利息收益）
        userMoney.setSymoney(userMoney.getSymoney().add(syMoney));
        // 用户总资产等于原先总资产加投资收益
        userMoney.setZmoney(userMoney.getZmoney().add(syMoney));
        userMoneyMapper.update(userMoney);
        // 如果已筹金额等于目标金额,则更新借款表中的状态为还款中、更新借款人冻结金额为可用余额
        BorrowDetail borrowDetail = new BorrowDetail();
        //修改借款人的资产
        UserMoney juserMoney = userMoneyMapper.getUserMoney(tzb.getJuid());
        juserMoney.setZmoney(juserMoney.getZmoney().add(tzb.getMoney()));
        //如果已经满标
        if((bAD.getMoneyCount().add(tzb.getMoney())).compareTo(bAD.getMoney()) == 0) {
            BorrowApply borrowApply = new BorrowApply();
            borrowApply.setBaid(tzb.getBaid());
            borrowApply.setCkstatus(BorrowStatusEnum.REPAYMENT.getCode());
            borrowApplyMapper.update(borrowApply);
            //满标时将借款人的冻结金额变成可用余额
            juserMoney.setDjmoney(juserMoney.getDjmoney().add(tzb.getMoney()).subtract(bAD.getMoney()));
            juserMoney.setKymoney(juserMoney.getKymoney().add(bAD.getMoney()));
            // 生成还款清单
            //查找贷后负责人id
            Integer huid = shborrowMapper.getIdByBaid(tzb.getBaid());
            //一次还清的还款清单
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Calendar.getInstance().getTime());
            if(bAD.getWay().equals(WayEnum.PAYOFF_ONCE.getCode())) {
                Hkb hkb = new Hkb();
                try {
                    BeanCopyUtils.copy(hkb, bAD);
                    hkb.setYbj(bAD.getMoney());
                    hkb.setYlx(syMoney);
                    hkb.setYbx(bAD.getMoney().add(syMoney));
                    hkb.setTnum(1);
                    calendar.add(Calendar.MONTH, month);
                    hkb.setYtime(calendar.getTime());
                    hkb.setHuid(huid);
                    hkb.setBaid(tzb.getBaid());
                    hkbMapper.save(hkb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            List<Hkb> hkbList = new ArrayList<>();
            for(int i = 1; i <= month; i++) {
                Hkb hkb = new Hkb();
                try {
                    //拷贝uid,cpname,rname,bzname字段的值
                    BeanCopyUtils.copy(hkb, bAD);
                    hkb.setStatus(HKStatusEnum.UNREPAY.getCode());
                    hkb.setHuid(huid);
                    hkb.setTnum(month);
                    calendar.add(Calendar.MONTH, 1);
                    hkb.setYtime(calendar.getTime());
                    //每月利息金额
                    BigDecimal bigMonthNpro = BigDecimal.valueOf(monthNpro);
                    //先息后本还款表
                    if(bAD.getWay().equals(WayEnum.XIAN_XI.getCode())) {
                        //每月利息等于总借款乘以月利率
                        hkb.setYlx(bAD.getMoney().multiply(bigMonthNpro));
                        hkb.setYbj(BigDecimal.valueOf(0));
                        //最后一个月还本金加利息
                        if(i == month) {
                            //应还本金
                            hkb.setYbj(bAD.getMoney());
                        }
                    }
                    // 等额本金还款算法
                    else if(bAD.getWay().equals(WayEnum.EQUAL_BJ.getCode())) {
                        LoanByMonth loanByMonth = new ACMLoanCalculator().calLoan(bAD.getMoney(), month, yearNpro, LoanUtil.RATE_TYPE_YEAR)
                                .getAllLoans().get(i-1);
                        // 月还本金
                        hkb.setYbj(loanByMonth.getPayPrincipal());
                        // 月利息
                        hkb.setYlx(loanByMonth.getInterest());

                    }
                    // 等额本息还款算法
                    else if(bAD.getWay().equals(WayEnum.EQUAL_BX.getCode())) {
                        LoanByMonth loanByMonth = new ACPIMLoanCalculator().calLoan(bAD.getMoney(), month, yearNpro, LoanUtil.RATE_TYPE_YEAR)
                                .getAllLoans().get(i-1);
                        //每月利息
                        hkb.setYlx(loanByMonth.getInterest());
                        //每月还款本金
                        hkb.setYbj(loanByMonth.getPayPrincipal());
                    }
                    hkb.setYbx(hkb.getYlx().add(hkb.getYbj()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hkb.setBaid(tzb.getBaid());
                hkbList.add(hkb);
            }
            //批量保存
            hkbMapper.saveList(hkbList);
        } else {
            juserMoney.setDjmoney(juserMoney.getDjmoney().add(tzb.getMoney()));
        }
        userMoneyMapper.update(juserMoney);
        //修改借款详情里的已筹金额
        borrowDetail.setBaid(tzb.getBaid());
        borrowDetail.setMoney(bAD.getMoneyCount().add(tzb.getMoney()));
        borrowDetailMapper.update(borrowDetail);
        return ServerResponse.createBySuccess("投资成功");
    }

    @Autowired
    public void setTzbMapper(TzbMapper tzbMapper) {
        super.setBaseDAO(tzbMapper);
        this.tzbMapper = tzbMapper;
    }

    @Autowired
    public void setBorrowApplyMapper(BorrowApplyMapper borrowApplyMapper) {
        this.borrowApplyMapper = borrowApplyMapper;
    }

    @Autowired
    public void setBorrowDetailMapper(BorrowDetailMapper borrowDetailMapper) {
        this.borrowDetailMapper = borrowDetailMapper;
    }

    @Autowired
    public void setUserMoneyMapper(UserMoneyMapper userMoneyMapper) {
        this.userMoneyMapper = userMoneyMapper;
    }

    @Autowired
    public void setRewardMapper(RewardMapper rewardMapper) {
        this.rewardMapper = rewardMapper;
    }

    @Autowired
    public void setRewardSettingMapper(RewardSettingMapper rewardSettingMapper) {
        this.rewardSettingMapper = rewardSettingMapper;
    }

    @Autowired
    public void setHkbMapper(HkbMapper hkbMapper) {
        this.hkbMapper = hkbMapper;
    }

    @Autowired
    public void setShborrowMapper(ShborrowMapper shborrowMapper) {
        this.shborrowMapper = shborrowMapper;
    }
}
