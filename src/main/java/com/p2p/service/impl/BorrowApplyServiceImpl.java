package com.p2p.service.impl;

import com.p2p.bean.BorrowApply;
import com.p2p.bean.BorrowDetail;
import com.p2p.common.Pager;
import com.p2p.common.ServerResponse;
import com.p2p.common.ValidationResult;
import com.p2p.common.ValidationUtils;
import com.p2p.dao.BorrowApplyMapper;
import com.p2p.dao.BorrowDetailMapper;
import com.p2p.service.BorrowApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by 7025 on 2017/12/21.
 */
@Service
public class BorrowApplyServiceImpl extends AbstractServiceImpl implements BorrowApplyService {

    private BorrowApplyMapper borrowApplyMapper;
    private BorrowDetailMapper borrowDetailMapper;

    @Autowired
    public void setBorrowApplyMapper(BorrowApplyMapper borrowApplyMapper) {
        super.setBaseDAO(borrowApplyMapper);
        this.borrowApplyMapper = borrowApplyMapper;
    }

    @Autowired
    public void setBorrowDetailMapper(BorrowDetailMapper borrowDetailMapper) {
        this.borrowDetailMapper = borrowDetailMapper;
    }

    @Override
    public Pager listPagerCriteria(int pageNo, int pageSize, Object obj) {
        Pager pager = new Pager(pageNo, pageSize);
        pager.setRows(borrowApplyMapper.listPagerCriteria(pager, obj));
        pager.setTotal(borrowApplyMapper.countCriteria(obj));
        return pager;
    }

        @Override
        public ServerResponse updateChecked(Integer baid, Integer status, Date cktime) {
            if(borrowApplyMapper.updateChecked(baid, status, cktime) == 1) {
                return ServerResponse.createBySuccess("修改成功");
            }
            return ServerResponse.createByError("修改失败");
        }

    @Override
    public ServerResponse saveBorrow(BorrowApply borrowApply, BorrowDetail borrowDetail) {
        ValidationResult validationResult = ValidationUtils.validateEntity(borrowApply);
        if(validationResult.isHasErrors()) {
            return ServerResponse.createByError("保存失败");
        }
        validationResult = ValidationUtils.validateEntity(borrowDetail);
        if(validationResult.isHasErrors()) {
            return ServerResponse.createByError("保存失败");
        }
        if(borrowApplyMapper.save(borrowApply) == 0) {
            return ServerResponse.createByError("保存失败");
        }
        borrowDetail.setBaid(borrowApply.getBaid());
        if(borrowDetailMapper.save(borrowDetail) == 0){
            return ServerResponse.createByError("保存失败");
        }
        return ServerResponse.createBySuccess("保存成功");
    }
}
