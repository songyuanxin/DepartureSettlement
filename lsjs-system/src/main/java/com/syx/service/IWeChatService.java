package com.syx.service;

import com.syx.domains.SendMsgData;
import com.syx.domains.vo.SendMsgRes;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
public interface IWeChatService {

     public SendMsgRes sendMsg(SendMsgData sendMsgData, String accessToken);

}
