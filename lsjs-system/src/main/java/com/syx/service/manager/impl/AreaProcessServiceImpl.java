package com.syx.service.manager.impl;

import com.syx.domain.Approve;
import com.syx.domains.dto.RegionalOrAreaApproveDto;
import com.syx.domains.vo.SendMsgRes;
import com.syx.service.impl.LsjsServiceImpl;
import com.syx.service.impl.WeChatServiceImpl;
import com.syx.service.manager.IAreaProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author 宋远欣
 * @date 2022/5/20
 **/
@Service
public class AreaProcessServiceImpl implements IAreaProcessService {

    @Autowired
    private WeChatServiceImpl weChatServiceImpl;

    @Autowired
    private LsjsServiceImpl lsjsServiceImpl;

    /**
     * 发送至地区经理审核
     * @param regionalOrAreaApproveDto
     * @param isShopowner
     * @return
     */
    @Override
    public SendMsgRes sendAreaMsg(RegionalOrAreaApproveDto regionalOrAreaApproveDto, String isShopowner) {
        SendMsgRes sendMsgRes = new SendMsgRes();
        String quitPernr = regionalOrAreaApproveDto.getQuitPernr();
        String userName = lsjsServiceImpl.getUserNameByPernr(quitPernr);
        //判断离职员工是否属于店长
        if (isShopowner.equals("2")){
            //到审核表中查询直接上级审核该离职员工时所选的地区经理工号
            String areaPernr = lsjsServiceImpl.getAreaPernrByQuitPernr(quitPernr);

            int insertApproveResult = 0;
            insertApproveResult = insertApprove(regionalOrAreaApproveDto, areaPernr);
            if (insertApproveResult == 0){
                sendMsgRes.setErrcode(1);
                sendMsgRes.setErrmsg("发送至地区经理审核时写入数据库失败");
                return sendMsgRes;
            }
            String splicing = "离司结算审核提醒:\n您收到了"+ quitPernr + userName + "的离司结算申请" + "<a href=\"http://10.9.16.2:8080/approve/#/pages/index/manager/areaApproval?pernr=" + areaPernr + "\">"+"【审批入口】</a>";
//            String splicing = "离司结算审核提醒:\n您收到了"+ quitPernr + userName + "的离司结算申请" + "<a href=\"http://hrfico.jzj.cn:19004/approve/#/pages/index/manager/areaApproval?pernr=" + areaPernr + "\">"+"【审批入口】</a>";
            sendMsgRes = weChatServiceImpl.sendMsg(areaPernr, splicing);
            if (sendMsgRes.getErrcode() != 0){
                sendMsgRes.setErrcode(1);
                sendMsgRes.setErrmsg("发送至地区经理审核时失败");
                return sendMsgRes;
            }
        }else {
            int insertApproveResult = 0;
            insertApproveResult = insertApprove(regionalOrAreaApproveDto, regionalOrAreaApproveDto.getAreaPernr());
            if (insertApproveResult == 0){
                sendMsgRes.setErrcode(1);
                sendMsgRes.setErrmsg("发送至地区经理审核时写入数据库失败");
                return sendMsgRes;
            }
            String splicing = "离司结算审核提醒:\n您收到了"+ quitPernr + userName + "的离司结算申请" + "<a href=\"http://10.9.16.2:8080/approve/#/pages/index/manager/areaApproval?pernr=" + regionalOrAreaApproveDto.getAreaPernr() + "\">"+"【审批入口】</a>";
//            String splicing = "离司结算审核提醒:\n您收到了"+ quitPernr + userName + "的离司结算申请" + "<a href=\"http://hrfico.jzj.cn:19004/approve/#/pages/index/manager/areaApproval?pernr=" + regionalOrAreaApproveDto.getAreaPernr() + "\">"+"【审批入口】</a>";
            sendMsgRes = weChatServiceImpl.sendMsg(regionalOrAreaApproveDto.getAreaPernr(), splicing);
            //判断提醒消息是否发送成功，若发送成功则写入审核表以及审核记录表
            if (sendMsgRes.getErrcode() != 0){
                sendMsgRes.setErrcode(1);
                sendMsgRes.setErrmsg("发送至地区经理审核时失败");
                return sendMsgRes;
            }
        }
        return sendMsgRes;
    }

    /**
     * 将发送成功的离司结算申请写入审核表
     * @param
     * @param
     * @return
     */
    public int insertApprove(RegionalOrAreaApproveDto regionalOrAreaApproveDto, String areaPernr) {
        //根据员工工号查询员工基本信息
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        Approve approve = new Approve();
        approve.setQuitPernr(regionalOrAreaApproveDto.getQuitPernr());
        approve.setApproveContent(7);
        approve.setApproveContentDesc("地区经理审核");
        approve.setApproveResult(1);
        approve.setApproveResultDesc("待审核");
        approve.setReviewerPernr(areaPernr);
        approve.setSendTime(timestamp);

        return lsjsServiceImpl.insertApprove(approve);
    }
}
