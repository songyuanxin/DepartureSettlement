package com.syx.controller;

import com.syx.domain.Approve;
import com.syx.domain.ApproveLog;
import com.syx.domains.AjaxResult;
import com.syx.domains.dto.DirectApproveFunctionDto;
import com.syx.domains.dto.DirectApproveStoreDto;
import com.syx.domains.dto.RegionalOrAreaApproveDto;
import com.syx.domains.vo.SendMsgRes;
import com.syx.service.ILsjsService;
import com.syx.service.finance.IFinanceProcessService;
import com.syx.service.logistics.IToolProcessService;
import com.syx.service.manager.IAreaProcessService;
import com.syx.service.manager.IRegionalProcessService;
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
@RequestMapping(path = "/directly")
@Slf4j
public class DirectlyController {

    @Autowired
    private ILsjsService lsjsService;

    @Autowired
    private IRegionalProcessService regionalProcessService;

    @Autowired
    private IAreaProcessService areaProcessService;

    @Autowired
    private IFinanceProcessService financeProcessService;

    @Autowired
    private IToolProcessService toolProcessService;

    /**
     * 直接上级审核职能部门员工时点击【通过】按钮
     *
     * @return
     */
    @PostMapping(path = "/function/adopt")
    public AjaxResult functionAdopt(DirectApproveFunctionDto functionDto) {
        SendMsgRes sendMsgRes = new SendMsgRes();

        //一、将审核结果写入数据库，更新审核结果
        Approve approve = new Approve();
        //获取当前系统时间
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        approve.setApproveId(functionDto.getApproveId());
        approve.setLaunchId(functionDto.getLaunchId());
        approve.setApproveTime(timestamp);
        approve.setQuitPernr(functionDto.getQuitPernr());
        approve.setReviewerPernr(functionDto.getReviewerPernr());
        approve.setWork(functionDto.getWork());
        approve.setFixedAsset(functionDto.getFixedAsset());
        approve.setIsa(functionDto.getIsa());
        approve.setDormitory(functionDto.getDormitory());

        approve.setDocumentNum(functionDto.getDocumentNum());
        approve.setApproveOpinion(functionDto.getAuditMind());
        approve.setApproveContent(1);
        approve.setApproveResult(2);
        approve.setApproveResultDesc("通过");

        int i = lsjsService.updateApprove(approve);

        //将审核记录插入审核记录表
        ApproveLog approveLog = new ApproveLog();
        BeanUtils.copyProperties(approve, approveLog);
        approveLog.setApproveContentDesc("直接上级审核");
        int i1 = lsjsService.insertApproveLog(approveLog);

        if (i < 1 || i1 < 1) {
            return AjaxResult.error("审核结果提交失败，可能原因：审核结果记录失败！");
        }

        //二、发送至财务和其他审核人审核
        String quitPernr = functionDto.getQuitPernr();
        String userName = lsjsService.getUserNameByPernr(quitPernr);
        sendMsgRes = financeProcessService.sendCareMsg(functionDto.getLaunchId(), quitPernr, userName);
        sendMsgRes = financeProcessService.sendLoanAndShortMsg(functionDto.getLaunchId(), quitPernr, userName);
        sendMsgRes = financeProcessService.sendQualityMsg(functionDto.getLaunchId(), quitPernr, userName);
        sendMsgRes = toolProcessService.sendtoolMsg(functionDto.getLaunchId(), quitPernr, userName);
        if (sendMsgRes.getErrcode() != 0) {
            return AjaxResult.error("审核结果提交失败，可能原因：发送至财务部/后勤部审核人时失败，请重试！");
        }
        return AjaxResult.success("审核结果提交成功");
    }

    /**
     * 直接上级审核职能部门员工时点击【待办】按钮
     *
     * @return
     */
    @PostMapping(path = "/function/noAdopt")
    public AjaxResult functionNoAdopt(DirectApproveFunctionDto functionDto) {
        SendMsgRes sendMsgRes = new SendMsgRes();
        //一、将审核结果写入数据库，更新审核结果
        Approve approve = new Approve();
        //获取当前系统时间
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        approve.setApproveId(functionDto.getApproveId());
        approve.setLaunchId(functionDto.getLaunchId());
        approve.setApproveTime(timestamp);
        approve.setQuitPernr(functionDto.getQuitPernr());
        approve.setReviewerPernr(functionDto.getReviewerPernr());

        StringBuilder auditMind = new StringBuilder();
        //工作交接
        if (functionDto.getWork().equals("已完成")) {
            approve.setWork(functionDto.getWork());
        } else if(functionDto.getWork().equals("未完成")){
            approve.setWork(functionDto.getWork());
            auditMind.append("工作交接：" + functionDto.getWork() + "；");
        }
        //固定资产交接
        if (functionDto.getFixedAsset().equals("已完成")) {
            approve.setFixedAsset(functionDto.getFixedAsset());
        } else if(functionDto.getFixedAsset().equals("未完成")){
            approve.setFixedAsset(functionDto.getFixedAsset());
            auditMind.append("固定资产交接：" + functionDto.getFixedAsset() + "；");
        }
        //信息系统权限交接
        if (functionDto.getIsa().equals("已关闭")) {
            approve.setIsa(functionDto.getIsa());
        } else if(functionDto.getIsa().equals("未关闭")){
            approve.setIsa(functionDto.getIsa());
            auditMind.append("信息系统权限交接：" + functionDto.getIsa() + "；");
        }
        //宿舍交接
        if (functionDto.getDormitory().equals("已完成全部事项")) {
            approve.setDormitory(functionDto.getDormitory());
        } else if (functionDto.getDormitory().equals("存在未完成事项")) {
            approve.setDormitory(functionDto.getDormitory());
            auditMind.append("宿舍交接：" + functionDto.getDormitory() + "；");
        } else if(functionDto.getDormitory().equals("未住宿舍")){
            approve.setDormitory(functionDto.getDormitory());
        }
        approve.setDocumentNum(functionDto.getDocumentNum());
        approve.setApproveOpinion(auditMind.toString() + functionDto.getAuditMind());

        approve.setApproveContent(1);
        approve.setApproveResult(3);
        approve.setApproveResultDesc("不通过");

        int i = lsjsService.updateApprove(approve);

        //将审核记录插入审核记录表
        ApproveLog approveLog = new ApproveLog();
        BeanUtils.copyProperties(approve, approveLog);
        approveLog.setApproveContentDesc("直接上级审核");
        int i1 = lsjsService.insertApproveLog(approveLog);

        if (i < 1 || i1 < 1) {
            return AjaxResult.error("审核结果提交失败，请重试或联系管理员！");
        }
        return AjaxResult.success("审核结果提交成功");
    }

    /**
     * 直接上级审核门店员工时点击【通过】按钮
     *
     * @return
     */
    @PostMapping(path = "/store/adopt")
    public AjaxResult storeAdopt(DirectApproveStoreDto storeDto) {
        //一、将审核结果写入数据库
        Approve approve = new Approve();
        //获取当前系统时间
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        approve.setLaunchId(storeDto.getLaunchId());
        approve.setApproveId(storeDto.getApproveId());
        approve.setQuitPernr(storeDto.getQuitPernr());
        approve.setReviewerPernr(storeDto.getReviewerPernr());
        approve.setApproveTime(timestamp);
        approve.setApproveContent(1);

        BigDecimal goodsRefund = new BigDecimal(0);
        if (StringUtils.isNotBlank(storeDto.getGoodsRefund())) {
            goodsRefund = new BigDecimal(storeDto.getGoodsRefund());
            goodsRefund.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        approve.setGoodsRefund(goodsRefund);

        String approveOpinion = "";
        if (StringUtils.isNotEmpty(storeDto.getGoodsRefund())){
            if (Double.valueOf(storeDto.getGoodsRefund()).intValue() > 0) {
                approveOpinion = "存在商品赔偿需扣除！";
            }
        }

        approve.setApproveOpinion(approveOpinion + storeDto.getAuditMind());
        approve.setApproveResult(2);
        approve.setApproveResultDesc("通过");
        approve.setDocumentNum(storeDto.getDocumentNum());

        approve.setWork(storeDto.getWork());
        approve.setDormitory(storeDto.getDormitory());
        approve.setAreaPernr(storeDto.getAreaPernr());
        approve.setRegionPernr(storeDto.getRegionalPernr());

        int i = lsjsService.updateApprove(approve);

        //将审核记录插入审核记录表
        ApproveLog approveLog = new ApproveLog();
        BeanUtils.copyProperties(approve, approveLog);
        approveLog.setApproveContentDesc("直接上级审核");
        int i1 = lsjsService.insertApproveLog(approveLog);

        SendMsgRes sendMsgRes = new SendMsgRes();
        if (i > 0) {
            //判断该离职员工是否是店长，若不是店长则需要发送至店长审核时所选的区域经理
            if (storeDto.getIsShopowner().equals("2")) {
                //二、发送至区域经理企业微信审核
                sendMsgRes = regionalProcessService.sendRegionalMsg(storeDto);
                if (sendMsgRes.getErrcode() != 0) {
                    return AjaxResult.error("发送至区域经理审核失败，请重试或联系管理员！");
                }
                return AjaxResult.success("审核结果提交成功");
            } else {
                RegionalOrAreaApproveDto regionalOrAreaApproveDto = new RegionalOrAreaApproveDto();
                regionalOrAreaApproveDto.setLaunchId(storeDto.getLaunchId());
                regionalOrAreaApproveDto.setQuitPernr(storeDto.getQuitPernr());
                regionalOrAreaApproveDto.setAreaPernr(storeDto.getAreaPernr());
                sendMsgRes = areaProcessService.sendAreaMsg(regionalOrAreaApproveDto, storeDto.getIsShopowner());
                if (sendMsgRes.getErrcode() != 0) {
                    return AjaxResult.error("发送至地区经理审核失败，请重试或联系管理员！");
                }
                return AjaxResult.success("审核结果提交成功");
            }
        }
        if (sendMsgRes.getErrcode() != 0) {
            return AjaxResult.error("发送至区域经理或地区经理审核失败，请重试或联系管理员！");
        }
        return AjaxResult.success("审核结果提交成功");
    }

    /**
     * 直接上级审核门店员工时点击【待办】按钮
     *
     * @return
     */
    @PostMapping(path = "/store/noAdopt")
    public AjaxResult storeNoAdopt(DirectApproveStoreDto storeDto) {
        //一、将审核结果写入数据库
        Approve approve = new Approve();
        //获取当前系统时间
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        BigDecimal goodsRefund = new BigDecimal(0);
        if (StringUtils.isNotBlank(storeDto.getGoodsRefund())) {
            String str = storeDto.getGoodsRefund();
            goodsRefund = new BigDecimal(str);
            goodsRefund.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        approve.setApproveId(storeDto.getApproveId());
        approve.setLaunchId(storeDto.getLaunchId());
        approve.setQuitPernr(storeDto.getQuitPernr());
        approve.setReviewerPernr(storeDto.getReviewerPernr());
        approve.setApproveTime(timestamp);
        approve.setApproveContent(1);
        approve.setApproveResult(3);
        approve.setApproveResultDesc("不通过");
        approve.setDocumentNum(storeDto.getDocumentNum());
        approve.setGoodsRefund(goodsRefund);

        StringBuilder auditMind = new StringBuilder();
        //工作交接
        if (storeDto.getWork().equals("已完成")) {
            approve.setWork(storeDto.getWork());
        } else if(storeDto.getWork().equals("未完成")){
            approve.setWork(storeDto.getWork());
            auditMind.append("工作交接：" + storeDto.getWork() + "；");
        }
        //宿舍交接
        if (storeDto.getDormitory().equals("已完成全部事项")) {
            approve.setDormitory(storeDto.getDormitory());
        } else if (storeDto.getDormitory().equals("存在未完成事项")) {
            approve.setDormitory(storeDto.getDormitory());
            auditMind.append("宿舍交接：" + storeDto.getDormitory() + "");
        } else if(storeDto.getDormitory().equals("未住宿舍")){
            approve.setDormitory(storeDto.getDormitory());
        }
        //商品赔偿
        if (StringUtils.isNotEmpty(storeDto.getGoodsRefund())){
            if (Double.valueOf(storeDto.getGoodsRefund()).intValue() > 0) {
                auditMind.append("存在商品赔偿需扣除！");
            }
        }

        approve.setAreaPernr(storeDto.getAreaPernr());
        approve.setRegionPernr(storeDto.getRegionalPernr());
        approve.setApproveOpinion(auditMind.toString() + storeDto.getAuditMind());

        int i = lsjsService.updateApprove(approve);

        //将审核记录插入审核记录表
        ApproveLog approveLog = new ApproveLog();
        BeanUtils.copyProperties(approve, approveLog);
        approveLog.setApproveContentDesc("直接上级审核");
        int i1 = lsjsService.insertApproveLog(approveLog);

        if (i < 1 || i1 < 1) {
            return AjaxResult.error("审核结果提交失败，请重试或联系管理员！");
        }
        return AjaxResult.success("审核结果提交成功");
    }
}
