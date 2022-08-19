package com.syx.task;

import com.syx.domain.LsjsAuditSendLog;
import com.syx.domains.vo.AuditApproveVo;
import com.syx.domains.vo.SendMsgRes;
import com.syx.mapper.lsjs.ApproveMapper;
import com.syx.mapper.lsjs.LsjsAuditSendLogMapper;
import com.syx.service.impl.WeChatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/8/17
 **/
@Configuration
@EnableScheduling
public class LsjsTask {

    @Autowired
    private ApproveMapper approveMapper;

    @Autowired
    private WeChatServiceImpl weChatService;

    @Autowired
    private LsjsAuditSendLogMapper lsjsAuditSendLogMapper;

    /**
     * 设置定时任务，每天上午9点向超过5天未审批的审核人再次发送消息提醒
     */
    @Scheduled(cron = "00 00 09 * * ?")
    public void sendLateApproveMsg() throws ParseException {
        //获取当前系统时间
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowTime = now.format(fmt);

        //查询当前待审核的数据
        List<AuditApproveVo> auditApproveList = approveMapper.getAuditData();
        for (AuditApproveVo auditApprove:auditApproveList){
            //将接收时间转换为String格式
            SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sendTime = tempDate.format(auditApprove.getSendTime());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date sendDate = format.parse(sendTime);
            Date nowDate = format.parse(nowTime);
            //时间差
            long diff = nowDate.getTime() - sendDate.getTime();
            int diffDays = (int)(diff/(24 * 60 * 60 * 1000));
            if (diffDays >= 5){
                String aType = "";
                if (auditApprove.getApproveContent() == 1 && auditApprove.getPersonScope().equals("门店")){
                    aType = "<a href=\"http://hrfico.jzj.cn:19004/approve/#/pages/index/directly/storeApproval?pernr=";
//                    aType = "<a href=\"http://10.9.8.81:8080/approve/#/pages/index/directly/storeApproval?pernr=";
                }else if (auditApprove.getApproveContent() == 1 && auditApprove.getPersonScope().equals("职能")){
                    aType = "<a href=\"http://hrfico.jzj.cn:19004/approve/#/pages/index/directly/functionApproval?pernr=";
//                    aType = "<a href=\"http://10.9.8.81:8080/approve/#/pages/index/directly/functionApproval?pernr=";
                }else if (auditApprove.getApproveContent() == 2){
                    aType = "<a href=\"http://hrfico.jzj.cn:19004/approve/#/pages/index/finance/loanApproval?pernr=";
//                    aType = "<a href=\"http://10.9.8.81:8080/approve/#/pages/index/finance/loanApproval?pernr=";
                }else if (auditApprove.getApproveContent() == 3){
                    aType = "<a href=\"http://hrfico.jzj.cn:19004/approve/#/pages/index/finance/qualityApproval?pernr=";
//                    aType = "<a href=\"http://10.9.8.81:8080/approve/#/pages/index/finance/qualityApproval?pernr=";
                }else if (auditApprove.getApproveContent() == 4){
                    aType = "<a href=\"http://hrfico.jzj.cn:19004/approve/#/pages/index/logistics/toolingApproval?pernr=";
//                    aType = "<a href=\"http://10.9.8.81:8080/approve/#/pages/index/logistics/toolingApproval?pernr=";
                }else if (auditApprove.getApproveContent() == 5){
                    aType = "<a href=\"http://hrfico.jzj.cn:19004/approve/#/pages/index/finance/careApproval?pernr=";
//                    aType = "<a href=\"http://10.9.8.81:8080/approve/#/pages/index/finance/careApproval?pernr=";
                }else if (auditApprove.getApproveContent() == 6){
                    aType = "<a href=\"http://hrfico.jzj.cn:19004/approve/#/pages/index/manager/regionApproval?pernr=";
//                    aType = "<a href=\"http://10.9.8.81:8080/approve/#/pages/index/manager/regionApproval?pernr=";
                }else if (auditApprove.getApproveContent() == 7){
                    aType = "<a href=\"http://hrfico.jzj.cn:19004/approve/#/pages/index/manager/areaApproval?pernr=";
//                    aType = "<a href=\"http://10.9.8.81:8080/approve/#/pages/index/manager/areaApproval?pernr=";
                }
                String splicing = "离司结算审核提醒:\n您收到了" + auditApprove.getQuitPernr() + auditApprove.getName() + "的离司结算申请" + aType + auditApprove.getReviewerPernr() + "\">"+"【审批入口】</a>";
                SendMsgRes sendMsgRes = weChatService.sendMsgToReviewer(auditApprove.getReviewerPernr(), splicing);
                //将发送结果记录写入数据库
                insertAuditSendLog(sendMsgRes,auditApprove);
            }
        }
    }

    private void insertAuditSendLog(SendMsgRes sendMsgRes, AuditApproveVo auditApprove) {
        //获取当前系统时间
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        if (sendMsgRes.getErrcode() != 0){
            LsjsAuditSendLog lsjsAuditSendLog = new LsjsAuditSendLog();
            lsjsAuditSendLog.setQuitPernr(auditApprove.getQuitPernr());
            lsjsAuditSendLog.setReceivingTime(auditApprove.getSendTime());
            lsjsAuditSendLog.setReviewerPernr(auditApprove.getReviewerPernr());
            lsjsAuditSendLog.setApproveContent(auditApprove.getApproveContent());
            lsjsAuditSendLog.setSendIdent(0);
            lsjsAuditSendLog.setSendTime(timestamp);
            lsjsAuditSendLogMapper.insertAuditSendLog(lsjsAuditSendLog);
        }else {
            LsjsAuditSendLog lsjsAuditSendLog = new LsjsAuditSendLog();
            lsjsAuditSendLog.setQuitPernr(auditApprove.getQuitPernr());
            lsjsAuditSendLog.setReceivingTime(auditApprove.getSendTime());
            lsjsAuditSendLog.setReviewerPernr(auditApprove.getReviewerPernr());
            lsjsAuditSendLog.setApproveContent(auditApprove.getApproveContent());
            lsjsAuditSendLog.setSendIdent(1);
            lsjsAuditSendLog.setSendTime(timestamp);
            lsjsAuditSendLogMapper.insertAuditSendLog(lsjsAuditSendLog);
        }

    }
}
