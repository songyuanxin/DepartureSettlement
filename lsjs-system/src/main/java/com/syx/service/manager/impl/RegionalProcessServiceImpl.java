package com.syx.service.manager.impl;

import com.syx.domain.Approve;
import com.syx.domains.dto.DirectApproveStoreDto;
import com.syx.domains.vo.SendMsgRes;
import com.syx.service.impl.LsjsServiceImpl;
import com.syx.service.impl.WeChatServiceImpl;
import com.syx.service.manager.IRegionalProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author 宋远欣
 * @date 2022/5/20
 **/
@Service
public class RegionalProcessServiceImpl implements IRegionalProcessService {

    @Autowired
    private WeChatServiceImpl weChatServiceImpl;

    @Autowired
    private LsjsServiceImpl lsjsServiceImpl;

    /**
     * 发送至企业微信
     * @param approveStoreDto
     * @return
     */
    @Override
    public SendMsgRes sendRegionalMsg(DirectApproveStoreDto approveStoreDto) {
        SendMsgRes sendMsgRes = new SendMsgRes();
        String quitPernr = approveStoreDto.getQuitPernr();
        String regionalPernr = approveStoreDto.getRegionalPernr();
        String userName = lsjsServiceImpl.getUserNameByPernr(quitPernr);

//        String splicing = "离司结算审核提醒:\n您收到了"+ quitPernr + userName + "的离司结算申请" + "<a href=\"http://10.9.16.2:8080/approve/#/pages/index/manager/regionApproval?pernr=" + regionalPernr + "\">"+"【审批入口】</a>";
        String splicing = "离司结算审核提醒:\n您收到了"+ quitPernr + userName + "的离司结算申请" + "<a href=\"http://hrfico.jzj.cn:19004/approve/#/pages/index/manager/regionApproval?pernr=" + regionalPernr + "\">"+"【审批入口】</a>";
        sendMsgRes = weChatServiceImpl.sendMsgToReviewer(regionalPernr, splicing);
        //判断提醒消息是否发送成功，若发送成功则写入审核表以及审核记录表
        if (sendMsgRes.getErrcode() != 0){
            sendMsgRes.setErrcode(1);
            sendMsgRes.setErrmsg("发送至区域经理审核时失败");
            return sendMsgRes;
        }

        int insertApproveResult = 0;
        insertApproveResult = insertApprove(approveStoreDto);
        if (insertApproveResult == 0){
            sendMsgRes.setErrcode(1);
            sendMsgRes.setErrmsg("发送至区域经理审核时写入数据库失败");
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
    public int insertApprove(DirectApproveStoreDto approveStoreDto) {
        //根据员工工号查询员工基本信息
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        Approve approve = new Approve();
        approve.setLaunchId(approveStoreDto.getLaunchId());
        approve.setQuitPernr(approveStoreDto.getQuitPernr());
        approve.setApproveContent(6);
        approve.setApproveContentDesc("区域经理审核");
        approve.setApproveResult(1);
        approve.setApproveResultDesc("待审核");
        approve.setReviewerPernr(approveStoreDto.getRegionalPernr());
        approve.setSendTime(timestamp);

        return lsjsServiceImpl.insertApprove(approve);
    }

}
