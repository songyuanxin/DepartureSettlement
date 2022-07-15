package com.syx.service.finance;

import com.syx.domains.dto.RegionalOrAreaApproveDto;
import com.syx.domains.vo.SendMsgRes;

/**
 * @author 宋远欣
 * @date 2022/5/20
 **/
public interface IFinanceProcessService {

    SendMsgRes sendLoanAndShortMsg(Integer launchId, String quitPernr, String userName);

    SendMsgRes sendQualityMsg(Integer launchId, String quitPernr, String userName);

    SendMsgRes sendCareMsg(Integer launchId, String quitPernr, String userName);
}
