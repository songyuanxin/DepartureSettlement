package com.syx.service.manager;

import com.syx.domain.Approve;
import com.syx.domains.dto.DirectApproveStoreDto;
import com.syx.domains.vo.SendMsgRes;

/**
 * @author 宋远欣
 * @date 2022/5/20
 **/
public interface IRegionalProcessService {

    SendMsgRes sendRegionalMsg(DirectApproveStoreDto approveStoreDto);
}
