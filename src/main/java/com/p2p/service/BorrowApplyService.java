package com.p2p.service;

import com.p2p.bean.BorrowApply;
import com.p2p.bean.BorrowDetail;
import com.p2p.common.Pager;
import com.p2p.common.ServerResponse;
import com.p2p.vo.IndexBorrowVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface BorrowApplyService extends BaseService {

    ServerResponse updateChecked(Integer baid, Integer status, Date cktime);

    ServerResponse saveBorrow(BorrowApply borrowApply, BorrowDetail borrowDetail);

    Pager listPagerByBzid(int pageNo, int pageSize, Object obj);
}