package com.syx.service.finance;

import com.syx.domains.dto.RegionalOrAreaApproveDto;
import com.syx.domains.vo.SendMsgRes;

/**
 * @author 宋远欣
 * @date 2022/5/20
 **/
public interface IFinanceProcessService {

    SendMsgRes sendLoanAndShortMsg(String quitPernr, String userName);

    SendMsgRes sendQualityMsg(String quitPernr, String userName);

    SendMsgRes sendCareMsg(String quitPernr, String userName);
}
