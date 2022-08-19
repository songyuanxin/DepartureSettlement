package com.syx.service.logistics.impl;

import com.syx.domain.Approve;
import com.syx.domain.ImportData;
import com.syx.domain.Reviewer;
import com.syx.domain.SAPStoreHead;
import com.syx.domains.vo.SendMsgRes;
import com.syx.service.impl.LsjsServiceImpl;
import com.syx.service.impl.SAPStoreHeadServiceImpl;
import com.syx.service.impl.WeChatServiceImpl;
import com.syx.service.logistics.IToolProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author 宋远欣
 * @date 2022/5/20
 **/
@Service
public class ToolProcessServiceImpl implements IToolProcessService {

    @Autowired
    private LsjsServiceImpl lsjsServiceImpl;

    @Autowired
    private SAPStoreHeadServiceImpl sapStoreHeadServiceImpl;

    @Autowired
    private WeChatServiceImpl weChatServiceImpl;

    /**
     * 发送至工牌工装审核人企业微信中审核
     * @param quitPernr
     * @return
     */
    @Override
    public SendMsgRes sendtoolMsg(Integer launchId, String quitPernr, String userName) {
        SendMsgRes sendMsgRes = new SendMsgRes();
        //一、根据离职员工工号到人事导入数据表中查询该员工所属分部以及人员范围
        ImportData imprtData = lsjsServiceImpl.getLastImoprtDataByPernr(quitPernr);
        //二、根据离职员工工号所属分部/地区到审核人配置表中查询各部分审核人
        Reviewer reviewer = new Reviewer();
        //1、根据离职员工人员范围判断是否需要从门店主数据中查询所属地区
        if (imprtData.getPersonScope().equals("门店")){
            //从人员主数据中查出所属门店店编
            String storeIdByPernr = lsjsServiceImpl.getDepartmentByPernr(quitPernr);
            //根据门店店编查询所属地区
            SAPStoreHead SAPStoreHead = sapStoreHeadServiceImpl.getSAPStoreHeadByStoreId(storeIdByPernr);
            String divisionByStoreId = SAPStoreHead.getMarketTitle();
            String regionByStoreId = SAPStoreHead.getManageArea();
            //根据人员范围、所属分部、所属地区查询审核人信息
            reviewer = lsjsServiceImpl.getReviewer(imprtData.getPersonScope(), divisionByStoreId, regionByStoreId);
        }else {
            reviewer = lsjsServiceImpl.getReviewer(imprtData.getPersonScope(), imprtData.getDivision(), "");
        }
        if (reviewer == null){
            sendMsgRes.setErrcode(1);
            return sendMsgRes;
        }
        int insertApproveResult = 0;
        int approveContent = 4;
        String approveContentDesc = "工牌工装审核";
        insertApproveResult = insertApprove(launchId, quitPernr,reviewer.getToolPernr(), approveContent, approveContentDesc);
        if (insertApproveResult == 0){
            sendMsgRes.setErrcode(1);
            sendMsgRes.setErrmsg("写入数据库失败");
            return sendMsgRes;
        }
//        String splicing = "离司结算审核提醒:\n您收到了"+ quitPernr + userName + "的离司结算申请" + "<a href=\"http://hrfico.jzj.cn:19003/approve/#/pages/index/logistics/toolingApproval?pernr=" + reviewer.getToolPernr() + "\">"+"【审批入口】</a>";
        String splicing = "离司结算审核提醒:\n您收到了"+ quitPernr + userName + "的离司结算申请" + "<a href=\"http://hrfico.jzj.cn:19004/approve/#/pages/index/logistics/toolingApproval?pernr=" + reviewer.getToolPernr() + "\">"+"【审批入口】</a>";
        sendMsgRes = weChatServiceImpl.sendMsgToReviewer(reviewer.getToolPernr(), splicing);
        //判断提醒消息是否发送成功，若发送成功则写入审核表以及审核记录表
        if (sendMsgRes.getErrcode() != 0){
            sendMsgRes.setErrcode(1);
            sendMsgRes.setErrmsg("发送至工牌工装审核时失败");
            return sendMsgRes;
        }
        return sendMsgRes;
    }

    /**
     * 将发送成功的离司结算申请写入审核表
     * @param
     * @param
     * @return
     */
    public int insertApprove(Integer launchId, String quitPernr, String reviewerPernr,int approveContent, String approveContentDesc) {
        //根据员工工号查询员工基本信息
        Approve approve = new Approve();
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        approve.setLaunchId(launchId);
        approve.setQuitPernr(quitPernr);
        approve.setReviewerPernr(reviewerPernr);
        approve.setSendTime(timestamp);
        approve.setApproveContent(approveContent);
        approve.setApproveContentDesc(approveContentDesc);
        approve.setApproveResult(1);
        approve.setApproveResultDesc("待审核");
        return lsjsServiceImpl.insertApprove(approve);
    }
}
