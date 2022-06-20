package com.syx.controller;

import com.sun.org.apache.xpath.internal.operations.String;
import com.syx.domain.Approve;
import com.syx.domain.ApproveLog;
import com.syx.domains.AjaxResult;
import com.syx.domains.dto.FinanceApproveDto;
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
@RequestMapping(path = "/finance")
@Slf4j
public class FinanceController {

    @Autowired
    private ILsjsService lsjsService;

    /**
     * 短款、借款审核时点击【通过】按钮
     * @return
     */
    @PostMapping(path = "/loan/adopt")
    public AjaxResult loanAdopt(FinanceApproveDto financeApproveDto){
        Approve approve = setApprove(financeApproveDto);
        approve.setApproveResult(2);
        approve.setApproveResultDesc("通过");
        approve.setApproveContent(2);

        StringBuilder auditMind = new StringBuilder();
        //存在短款
        if (StringUtils.isNotBlank(financeApproveDto.getShortMoney())){
            if (Double.valueOf(financeApproveDto.getShortMoney()).intValue() > 0){
                auditMind.append("涉及" + financeApproveDto.getStoreId() + financeApproveDto.getStoreName() + "营业短款" + financeApproveDto.getShortMoney() + "元未处理。");
            }
        }
        approve.setApproveOpinion(auditMind.toString() + financeApproveDto.getAuditMind());

        int i = lsjsService.updateApprove(approve);

        //将审核记录插入审核记录表
        ApproveLog approveLog = new ApproveLog();
        BeanUtils.copyProperties(approve, approveLog);
        approveLog.setApproveContentDesc("借款、短款审核");
        int i1 = lsjsService.insertApproveLog(approveLog);

        if (i < 1 || i1 < 1) {
            return AjaxResult.error("审核结果提交失败，请重试或联系管理员！");
        }
        return AjaxResult.success("审核结果提交成功");
    }

    /**
     * 短款、借款审核时点击【不通过】按钮
     * @return
     */
    @PostMapping(path = "/loan/noAdopt")
    public AjaxResult loanNoAdopt(FinanceApproveDto financeApproveDto){
        Approve approve = setApprove(financeApproveDto);

        StringBuilder auditMind = new StringBuilder();
        //存在短款
        if (StringUtils.isNotBlank(financeApproveDto.getShortMoney())){
            if (Double.valueOf(financeApproveDto.getShortMoney()).intValue() > 0){
                auditMind.append("涉及" + financeApproveDto.getStoreId() + financeApproveDto.getStoreName() + "营业短款" + financeApproveDto.getShortMoney() + "元未处理。");
            }
        }
        //存在借款
        if (StringUtils.isNotBlank(financeApproveDto.getLoanMoney())){
            if (Double.valueOf(financeApproveDto.getLoanMoney()).intValue() > 0) {
                auditMind.append("存在员工个人借款" + financeApproveDto.getLoanMoney() + "元未履行完成冲销程序。若未在离司结算流程审批前完成处理将从离司结算工资中予以扣除。");
            }
        }

        approve.setApproveOpinion(auditMind.toString() + financeApproveDto.getAuditMind());

        approve.setApproveResult(3);
        approve.setApproveResultDesc("不通过");
        approve.setApproveContent(2);

        int i = lsjsService.updateApprove(approve);

        //将审核记录插入审核记录表
        ApproveLog approveLog = new ApproveLog();
        BeanUtils.copyProperties(approve, approveLog);
        approveLog.setApproveContentDesc("借款、短款审核");
        int i1 = lsjsService.insertApproveLog(approveLog);

        if (i < 1 || i1 < 1) {
            return AjaxResult.error("审核结果提交失败，请重试或联系管理员！");
        }
        return AjaxResult.success("审核结果提交成功");
    }

    /**
     * 质量简报扣款审核时点击【提交】按钮
     * @return
     */
    @PostMapping(path = "/quality/adopt")
    public AjaxResult qualityAdopt(FinanceApproveDto financeApproveDto){
        Approve approve = setApprove(financeApproveDto);
        approve.setApproveResult(2);
        approve.setApproveResultDesc("通过");
        approve.setApproveContent(3);

        StringBuilder auditMind = new StringBuilder();
        //存在质量简报扣款
        if (StringUtils.isNotBlank(financeApproveDto.getQualityMoney())){
            if (Double.valueOf(financeApproveDto.getQualityMoney()).intValue() > 0) {
                auditMind.append("存在质量简报扣款：" + financeApproveDto.getQualityMoney() + "元;");
            }
        }
        approve.setApproveOpinion(auditMind.toString() + financeApproveDto.getAuditMind());

        int i = lsjsService.updateApprove(approve);

        //将审核记录插入审核记录表
        ApproveLog approveLog = new ApproveLog();
        BeanUtils.copyProperties(approve, approveLog);
        approveLog.setApproveContentDesc("质量简报扣款审核");
        int i1 = lsjsService.insertApproveLog(approveLog);

        if (i < 1 || i1 < 1) {
            return AjaxResult.error("审核结果提交失败，请重试或联系管理员！");
        }
        return AjaxResult.success("审核结果提交成功");
    }

    /**
     * 质量简报扣款审核时点击【待办】按钮
     * @return
     */
//    @PostMapping(path = "/quality/noAdopt")
//    public AjaxResult qualityNoAdopt(FinanceApproveDto financeApproveDto){
//        Approve approve = setApprove(financeApproveDto);
//        approve.setApproveResult(3);
//        approve.setApproveResultDesc("不通过");
//        approve.setApproveContent(3);
//
//        StringBuilder auditMind = new StringBuilder();
//        if (StringUtils.isNotBlank(financeApproveDto.getQualityMoney())){
//            if (Double.valueOf(financeApproveDto.getQualityMoney()).intValue() > 0){
//                auditMind.append("存在质量简报扣款：" + financeApproveDto.getQualityMoney() + "元；");
//            }
//        }
//        approve.setApproveOpinion(auditMind.toString() + financeApproveDto.getAuditMind());
//
//        int i = lsjsService.updateApprove(approve);
//
//        //将审核记录插入审核记录表
//        ApproveLog approveLog = new ApproveLog();
//        BeanUtils.copyProperties(approve, approveLog);
//        approveLog.setApproveContentDesc("质量简报扣款审核");
//        int i1 = lsjsService.insertApproveLog(approveLog);
//
//        if (i < 1 || i1 < 1) {
//            return AjaxResult.error("审核结果提交失败，请重试或联系管理员！");
//        }
//        return AjaxResult.success("审核结果提交成功");
//    }

    /**
     * 管理责任盘点扣款审核时点击【提交】按钮
     * @return
     */
    @PostMapping(path = "/care/adopt")
    public AjaxResult careAdopt(FinanceApproveDto financeApproveDto){
        Approve approve = setApprove(financeApproveDto);
        approve.setApproveResult(2);
        approve.setApproveResultDesc("通过");
        approve.setApproveContent(5);

        StringBuilder auditMind = new StringBuilder();
        //存在管理责任盘点扣款
        if (StringUtils.isNotBlank(financeApproveDto.getCareMoney())){
            if (Double.valueOf(financeApproveDto.getCareMoney()).intValue() > 0) {
                auditMind.append("存在管理责任盘点扣款：" + financeApproveDto.getCareMoney() + "元;");
            }
        }
        approve.setApproveOpinion(auditMind.toString() + financeApproveDto.getAuditMind());

        int i = lsjsService.updateApprove(approve);

        //将审核记录插入审核记录表
        ApproveLog approveLog = new ApproveLog();
        BeanUtils.copyProperties(approve, approveLog);
        approveLog.setApproveContentDesc("管理责任盘点扣款审核");
        int i1 = lsjsService.insertApproveLog(approveLog);

        if (i < 1 || i1 < 1) {
            return AjaxResult.error("审核结果提交失败，请重试或联系管理员！");
        }
        return AjaxResult.success("审核结果提交成功");
    }

    /**
     * 管理责任盘点扣款审核时点击【待办】按钮
     * @return
     */
//    @PostMapping(path = "/care/noAdopt")
//    public AjaxResult careNoAdopt(FinanceApproveDto financeApproveDto){
//        Approve approve = setApprove(financeApproveDto);
//        approve.setApproveResult(3);
//        approve.setApproveResultDesc("不通过");
//        approve.setApproveContent(5);
//
//        StringBuilder auditMind = new StringBuilder();
//        if (StringUtils.isNotBlank(financeApproveDto.getCareMoney())){
//            if (Double.valueOf(financeApproveDto.getCareMoney()).intValue() > 0){
//                auditMind.append("存在管理责任盘点扣款：" + financeApproveDto.getCareMoney() + "元；");
//            }
//        }
//        approve.setApproveOpinion(auditMind.toString() + financeApproveDto.getAuditMind());
//
//        int i = lsjsService.updateApprove(approve);
//
//        //将审核记录插入审核记录表
//        ApproveLog approveLog = new ApproveLog();
//        BeanUtils.copyProperties(approve, approveLog);
//        approveLog.setApproveContentDesc("管理责任盘点扣款审核");
//        int i1 = lsjsService.insertApproveLog(approveLog);
//
//        if (i < 1 || i1 < 1) {
//            return AjaxResult.error("审核结果提交失败，请重试或联系管理员！");
//        }
//        return AjaxResult.success("审核结果提交成功");
//    }

    /**
     * 更新审核表中审核数据时给Approve实体赋值
     * @param financeApproveDto
     * @return
     */
    private Approve setApprove(FinanceApproveDto financeApproveDto) {
        Approve approve = new Approve();
        //获取当前系统时间
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        approve.setQuitPernr(financeApproveDto.getQuitPernr());
        approve.setReviewerPernr(financeApproveDto.getReviewerPernr());
        approve.setApproveTime(timestamp);
        approve.setShortStoreId(financeApproveDto.getStoreId());
        approve.setCareDocumentNum(financeApproveDto.getCareDocumentNum());
        if (StringUtils.isNotBlank(financeApproveDto.getLoanMoney()) && !financeApproveDto.getLoanMoney().equals("null")){
            approve.setLoanMoney(new BigDecimal(financeApproveDto.getLoanMoney()).setScale(2,BigDecimal.ROUND_HALF_UP));
        }
        if (StringUtils.isNotBlank(financeApproveDto.getShortMoney()) && !financeApproveDto.getShortMoney().equals("null")){
            approve.setShortMoney(new BigDecimal(financeApproveDto.getShortMoney()).setScale(2,BigDecimal.ROUND_HALF_UP));
        }
        if (StringUtils.isNotBlank(financeApproveDto.getQualityMoney()) && !financeApproveDto.getQualityMoney().equals("null")){
            approve.setQualityMoney(new BigDecimal(financeApproveDto.getQualityMoney()).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        if (StringUtils.isNotBlank(financeApproveDto.getCareMoney()) && !financeApproveDto.getCareMoney().equals("null")){
            approve.setCareMoney(new BigDecimal(financeApproveDto.getCareMoney()).setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        return approve;
    }
}
