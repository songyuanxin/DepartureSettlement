package com.syx.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.syx.client.WxClient;
import com.syx.domains.SendMsgData;
import com.syx.domains.vo.AccessTokenRes;
import com.syx.domains.vo.SendMsgRes;
import com.syx.utils.JsonUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 宋远欣
 * @date 2022/3/1
 **/
@Service
public class WxServiceImpl {
    private static final String CORP_ID = "wx69ef55d633835331";
//    private static final String CORP_SECRET = "n9445-9rfHcCgwbg8KvWZgd94Htom_SgLqr0YvtUkMA";
    private static final String CORP_SECRET = "1xXEJ1gmOmmoVCC04trkmnXi245udU1nNtjd_uaCr0U";

    @SuppressWarnings("unchecked")
    public AccessTokenRes getAccessToken() throws IOException {
        WxClient client = new WxClient();
        Map<String, Object> params = new HashMap<>(2);
        params.put("corpid",CORP_ID);
        params.put("corpsecret",CORP_SECRET);
        String s = client.executeGet(WxClient.Api.ACCESS_TOKEN_GET, params);
        AccessTokenRes tokenBaseRes = (AccessTokenRes) JsonUtils.deserializeObject(s, new TypeReference<AccessTokenRes>() {});
        return tokenBaseRes;
    }

    /**
     * 通过离司结算应用发送离司结算审批提醒
     * @param sendMsgData
     * @param accessToken
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public SendMsgRes sendMsg(SendMsgData sendMsgData, String accessToken) throws IOException{
        WxClient client = new WxClient();
        Map<String, Object> params = new HashMap<>(1);
        params.put("access_token",accessToken);
        String s = client.executePost(WxClient.Api.SEND_MSG, sendMsgData, params);
        SendMsgRes sendMsgRes = (SendMsgRes) JsonUtils.deserializeObject(s, new TypeReference<SendMsgRes>() {});
        return sendMsgRes;
    }

}
