package com.syx.task;

import com.syx.mapper.lsjs.ApproveMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author 宋远欣
 * @date 2022/8/17
 **/
@Configuration
@EnableScheduling
public class LsjsTask {

    @Autowired
    private ApproveMapper approveMapper;

    /**
     * 设置定时任务，每天上午9点向超过5天未审批的审核人再次发送消息提醒
     */
    @Scheduled(cron = "0 30 * * * ?")
    public void sendLateApproveMsg(){

    }
}
