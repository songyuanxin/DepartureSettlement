package com.syx.controller;

import com.syx.domain.Approve;
import com.syx.domain.ApproveLog;
import com.syx.domains.AjaxResult;
import com.syx.domains.dto.LogisticsDto;
import com.syx.service.ILsjsService;
import com.syx.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author 宋远欣
 * @date 2022/5/16
 **/
@RestController
@RequestMapping(path = "/logistics")
@Slf4j
public class LogisticsController {

    @Autowired
    private ILsjsService lsjsService;

    /**
     * 工牌、工装审核点击【通过】按钮
     * @return
     */
    @PostMapping(path = "/tooling/adopt")
    public AjaxResult toolingAdopt(LogisticsDto logisticsDto){
        Approve approve = setApprove(logisticsDto);
        approve.setApproveResult(2);
        approve.setApproveResultDesc("通过");
        approve.setApproveContent(4);

        StringBuilder auditMind = new StringBuilder();
        if (StringUtils.isNotBlank(logisticsDto.getCardMoney())){
            if (Double.valueOf(logisticsDto.getCardMoney()).intValue() > 0){
                auditMind.append("存在工牌扣款：" + logisticsDto.getCardMoney() + "元；");
            }
        }
        if (StringUtils.isNotBlank(logisticsDto.getClothesMoney())){
            if (Double.valueOf(logisticsDto.getClothesMoney()).intValue() > 0){
                auditMind.append("存在工装扣款：" + logisticsDto.getClothesMoney() + "元；");
            }
        }
        approve.setApproveOpinion(auditMind.toString() + logisticsDto.getAuditMind());

        int i = lsjsService.updateApprove(approve);

        //将审核记录插入审核记录表
        ApproveLog approveLog = new ApproveLog();
        BeanUtils.copyProperties(approve, approveLog);
        approveLog.setApproveContentDesc("工牌、工装审核");
        int i1 = lsjsService.insertApproveLog(approveLog);

        if (i < 1 || i1 < 1) {
            return AjaxResult.error("审核结果提交失败，请重试或联系管理员！");
        }
        return AjaxResult.success("审核结果提交成功");
    }

    /**
     * 工牌、工装审核点击【待办】按钮
     * @return
     */
    @PostMapping(path = "/tooling/noAdopt")
    public AjaxResult toolingNoAdopt(LogisticsDto logisticsDto){
        Approve approve = setApprove(logisticsDto);
        approve.setApproveResult(3);
        approve.setApproveResultDesc("不通过");
        approve.setApproveContent(4);

        StringBuilder auditMind = new StringBuilder();
        if (StringUtils.isNotBlank(logisticsDto.getCardMoney())){
            if (Double.valueOf(logisticsDto.getCardMoney()).intValue() > 0){
                auditMind.append("存在工牌扣款：" + logisticsDto.getCardMoney() + "元；");
            }
        }
        if (StringUtils.isNotBlank(logisticsDto.getClothesMoney())){
            if (Double.valueOf(logisticsDto.getClothesMoney()).intValue() > 0){
                auditMind.append("存在工牌扣款：" + logisticsDto.getClothesMoney() + "元；");
            }
        }
        approve.setApproveOpinion(auditMind.toString() + logisticsDto.getAuditMind());

        int i = lsjsService.updateApprove(approve);

        //将审核记录插入审核记录表
        ApproveLog approveLog = new ApproveLog();
        BeanUtils.copyProperties(approve, approveLog);
        approveLog.setApproveContentDesc("工牌、工装审核");
        int i1 = lsjsService.insertApproveLog(approveLog);

        if (i < 1 || i1 < 1) {
            return AjaxResult.error("审核结果提交失败，请重试或联系管理员！");
        }
        return AjaxResult.success("审核结果提交成功");
    }

    private Approve setApprove(LogisticsDto logisticsDto) {
        Approve approve = new Approve();
        //获取当前系统时间
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        approve.setQuitPernr(logisticsDto.getQuitPernr());
        approve.setReviewerPernr(logisticsDto.getReviewerPernr());

        if (StringUtils.isNotBlank(logisticsDto.getCardMoney()) && !logisticsDto.getCardMoney().equals("null")){
            approve.setCardMoney(new BigDecimal(logisticsDto.getCardMoney()).setScale(2,BigDecimal.ROUND_HALF_UP));
        }
        if (StringUtils.isNotBlank(logisticsDto.getClothesMoney()) && !logisticsDto.getClothesMoney().equals("null")){
            approve.setClothesMoney(new BigDecimal(logisticsDto.getClothesMoney()).setScale(2,BigDecimal.ROUND_HALF_UP));
        }

        approve.setApproveTime(timestamp);

        return approve;
    }
}
