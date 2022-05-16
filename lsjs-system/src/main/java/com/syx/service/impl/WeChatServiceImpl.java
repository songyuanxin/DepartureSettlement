package com.syx.service.impl;

import com.syx.domains.SendMsgData;
import com.syx.domains.vo.SendMsgRes;
import com.syx.service.IWeChatService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
@Service
public class WeChatServiceImpl implements IWeChatService {

    @Autowired
    private WxServiceImpl wxService;

    @Override
    public SendMsgRes sendMsg(SendMsgData sendMsgData, String accessToken) {
        try {
            SendMsgRes sendMsgRes = wxService.sendMsg(sendMsgData, accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
