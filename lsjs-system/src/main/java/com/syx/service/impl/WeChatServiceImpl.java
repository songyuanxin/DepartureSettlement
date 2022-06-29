package com.syx.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.syx.domain.WeChatUserInfo;
import com.syx.domains.Content;
import com.syx.domains.SendMsgData;
import com.syx.domains.vo.AccessTokenRes;
import com.syx.domains.vo.SendMsgRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
@Service
public class WeChatServiceImpl {

    private static final String MSGTYPE = "text";
    //    private static final String AGENTID = "1000043";
    private static final String AGENTID = "1000046";
    private static final int SAFE = 0;
    private static final int ENABLE_DUPLICATE_CHECK = 0;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WxServiceImpl wxServiceImpl;

    public SendMsgRes sendMsg(String reviewer, String splicing) {
        SendMsgRes sendMsgRes = new SendMsgRes();
        //二、组装发送信息实体
        SendMsgData sendMsgData = new SendMsgData();
        Content content = new Content();
        sendMsgData.setAgentid(AGENTID);
        sendMsgData.setMsgtype(MSGTYPE);
        sendMsgData.setSafe(SAFE);
        sendMsgData.setEnable_duplicate_check(ENABLE_DUPLICATE_CHECK);
//        sendMsgData.setTouser("00" + reviewer);
        sendMsgData.setTouser("00072403");
        content.setContent(splicing);
        sendMsgData.setText(content);
        //三、调用企业微信接口发送提醒消息
        //判断redis中的token是否有效
        Boolean access_token1 = redisTemplate.hasKey("access_token");
        //若token无效则调用企业微信接口获取access_token并再次写入redis
        if (!access_token1) {
            getAccessToken();
            //从redis中获取token
            String access_token = redisTemplate.opsForValue().get("access_token").toString();
            //调用企业微信接口发送消息
            sendMsgRes = sendMsg(sendMsgData, access_token);
        } else {
            //若token有效，则直接调用企业微信接口发送消息
            String access_token = redisTemplate.opsForValue().get("access_token").toString();
            //调用企业微信接口发送消息
            sendMsgRes = sendMsg(sendMsgData, access_token);
        }
        return sendMsgRes;
    }

    /**
     * 获取企业微信应用token
     *
     * @return
     */
    public void getAccessToken() {
        AccessTokenRes accessTokenRes = new AccessTokenRes();
        try {
            accessTokenRes = wxServiceImpl.getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将access_token存入redis中,并设置失效时间为两小时
        redisTemplate.opsForValue().set("access_token", accessTokenRes.getAccess_token(), 2, TimeUnit.HOURS);
    }

    /**
     * 调用企业微信应用发送消息
     *
     * @param sendMsgData
     * @param accessToken
     * @return
     */
    public SendMsgRes sendMsg(SendMsgData sendMsgData, String accessToken) {
        SendMsgRes sendMsgRes = new SendMsgRes();
        try {
            sendMsgRes = wxServiceImpl.sendMsg(sendMsgData, accessToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sendMsgRes;
    }

    public String getUserId(String code) {
        WeChatUserInfo userInfo = null;
        String userId = "";
        String UserInfoUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE";
        AccessTokenRes accessTokenRes = new AccessTokenRes();
        try {
            accessTokenRes = wxServiceImpl.getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将access_token存入redis中,并设置失效时间为两小时
        redisTemplate.opsForValue().set("access_token", accessTokenRes.getAccess_token(), 2, TimeUnit.HOURS);
        //判断redis中的token是否有效
        Boolean access_token1 = redisTemplate.hasKey("access_token");
        if (!access_token1) {
            getAccessToken();
            //从redis中获取token
            String access_token = redisTemplate.opsForValue().get("access_token").toString();
            //调用企业微信接口获取用户ID
            //替换url，访问企业微信接口
            UserInfoUrl = UserInfoUrl.replace("SUITE_TOKEN", access_token).replace("CODE", code);
            //调用http方法
            String body = HttpRequest.get(UserInfoUrl).execute().body();
            try {
                // 取部门列表信息
                userInfo = JSONUtil.toBean(body, WeChatUserInfo.class);
                if (userInfo.getErrcode() == null || userInfo.getErrcode() == 0) {
                    // 用户id
                    userId = userInfo.getUserId();
                } else {
                    throw new RuntimeException(userInfo.getErrmsg());
                }
            } catch (Exception e) {
                throw new RuntimeException(userInfo.getErrmsg());
            }
        } else {
            //若token有效，则直接调用企业微信接口发送消息
            String access_token = redisTemplate.opsForValue().get("access_token").toString();
            //调用企业微信接口获取用户ID
            //替换url，访问企业微信接口
            UserInfoUrl = UserInfoUrl.replace("SUITE_TOKEN", access_token).replace("CODE", code);
            //调用http方法
            String body = HttpRequest.get(UserInfoUrl).execute().body();
            try {
                // 取部门列表信息
                userInfo = JSONUtil.toBean(body, WeChatUserInfo.class);
                if (userInfo.getErrcode() == null || userInfo.getErrcode() == 0) {
                    // 用户id
                    userId = userInfo.getUserId();
                } else {
                    throw new RuntimeException(userInfo.getErrmsg());
                }
            } catch (Exception e) {
                throw new RuntimeException(userInfo.getErrmsg());
            }
        }
        return userId;
    }
}


