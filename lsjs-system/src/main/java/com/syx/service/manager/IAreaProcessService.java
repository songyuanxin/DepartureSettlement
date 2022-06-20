package com.syx.service.manager;

import com.syx.domains.dto.RegionalOrAreaApproveDto;
import com.syx.domains.vo.SendMsgRes;

/**
 * @author 宋远欣
 * @date 2022/5/20
 **/
public interface IAreaProcessService {
    SendMsgRes sendAreaMsg(RegionalOrAreaApproveDto regionalOrAreaApproveDto, String isShopowner);
}
