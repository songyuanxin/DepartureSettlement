package com.syx.controller;

import com.syx.domain.Approve;
import com.syx.domain.ApproveLog;
import com.syx.domain.ImportData;
import com.syx.domains.AjaxResult;
import com.syx.domains.ImportDataInfo;
import com.syx.domains.dto.ImportDataDto;
import com.syx.domains.dto.RegionalOrAreaApproveDto;
import com.syx.domains.vo.SendMsgRes;
import com.syx.service.IImportDataService;
import com.syx.service.ILsjsService;
import com.syx.service.finance.IFinanceProcessService;
import com.syx.service.logistics.IToolProcessService;
import com.syx.service.manager.IAreaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/5/16
 **/
@RestController
@RequestMapping(path = "/manager")
@Slf4j
public class ManagerController {

    @Autowired
    private ILsjsService lsjsService;

    @Autowired
    private IAreaProcessService areaProcessService;

    @Autowired
    private IFinanceProcessService financeProcessService;

    @Autowired
    private IToolProcessService toolProcessService;

    @Autowired
    private IImportDataService importDataService;

    /**
     * 区域经理审核时点击【通过】按钮
     * @return
     */
    @PostMapping(path = "/regional/adopt")
    public AjaxResult regionalAdopt(RegionalOrAreaApproveDto regionalOrAreaApproveDto){
        //一、将审核结果写入数据库
        Approve approve = new Approve();
        //获取当前系统时间
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        approve.setLaunchId(regionalOrAreaApproveDto.getLaunchId());
        approve.setQuitPernr(regionalOrAreaApproveDto.getQuitPernr());
        approve.setReviewerPernr(regionalOrAreaApproveDto.getReviewerPernr());
        approve.setApproveOpinion(regionalOrAreaApproveDto.getAuditMind());
        approve.setApproveTime(timestamp);
        approve.setApproveResult(2);
        approve.setApproveResultDesc("通过");
        approve.setApproveContent(6);

        int i = lsjsService.updateApprove(approve);

        //将审核记录插入审核记录表
        ApproveLog approveLog = new ApproveLog();
        BeanUtils.copyProperties(approve, approveLog);
        approveLog.setApproveContentDesc("区域经理审核");
        int i1 = lsjsService.insertApproveLog(approveLog);

        if ( i < 1 || i1 < 1){
            return AjaxResult.success("审核结果提交失败");
        }

        SendMsgRes sendMsgRes = new SendMsgRes();
        sendMsgRes = areaProcessService.sendAreaMsg(regionalOrAreaApproveDto, "2");

        if (sendMsgRes.getErrcode() != 0){
            return AjaxResult.error("审核结果提交失败，可能原因：发送至地区经理审核时出错，请重试或联系管理员！");
        }
        return AjaxResult.success("审核结果提交成功");
    }

    /**
     * 区域经理审核时点击【退回】按钮
     * @return
     */
    @PostMapping(path = "/regional/noAdopt")
    public AjaxResult regionalNoAdopt(RegionalOrAreaApproveDto regionalOrAreaApproveDto){
        //将审核记录插入审核记录表
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        ApproveLog approveLog = new ApproveLog();
        approveLog.setApproveContent(6);
        approveLog.setApproveContentDesc("区域经理审核");
        approveLog.setApproveResult(4);
        approveLog.setApproveResultDesc("退回");
        approveLog.setApproveOpinion(regionalOrAreaApproveDto.getAuditMind());
        approveLog.setApproveTime(timestamp);
        approveLog.setQuitPernr(regionalOrAreaApproveDto.getQuitPernr());
        approveLog.setReviewerPernr(regionalOrAreaApproveDto.getReviewerPernr());
        int i1 = lsjsService.insertApproveLog(approveLog);

        //二、重新发起离司结算申请并发送至直接上级企业微信中
        //1、根据离职员工工号到人事导入数据表中查询直接上级工号
        ImportData importData = importDataService.getLastImportDataByPernr(regionalOrAreaApproveDto.getQuitPernr());
        ImportDataInfo importDataInfo = new ImportDataInfo();
        //根据工号查询员工姓名，向直接上级推送消息时使用
        String userName = lsjsService.getUserNameByPernr(importData.getQuitPernr());
        //为ImportDataDto实体赋值
        BeanUtils.copyProperties(importData, importDataInfo);
        importDataInfo.setQuitPernr(importData.getQuitPernr());
        importDataInfo.setQuitName(userName);

        List<ImportDataInfo> importDataInfos = new ArrayList<>();
        importDataInfos.add(importDataInfo);

        if (importDataInfos.size() == 0){
            return AjaxResult.error("找不到该员工直接上级等信息，请联系管理员！");
        }
        //2、调用发送至直接上级企业微信接口向直接上级重新推送该员工的离司结算申请
        String isReturn = "1";
        SendMsgRes sendMsgRes = importDataService.launchProcess(importDataInfos, isReturn);
        if (sendMsgRes.getErrcode() != 0){
            return AjaxResult.error("重新发送至直接上级失败，请联系管理员处理！");
        }
        return AjaxResult.success("退回成功");
    }

    /**
     * 地区经理审核时点击【通过】按钮
     * @return
     */
    @PostMapping(path = "/area/adopt")
    public AjaxResult areaAdopt(RegionalOrAreaApproveDto regionalOrAreaApproveDto){
        Approve approve = new Approve();
        //获取当前系统时间
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        approve.setLaunchId(regionalOrAreaApproveDto.getLaunchId());
        approve.setQuitPernr(regionalOrAreaApproveDto.getQuitPernr());
        approve.setReviewerPernr(regionalOrAreaApproveDto.getReviewerPernr());
        approve.setApproveOpinion(regionalOrAreaApproveDto.getAuditMind());
        approve.setApproveTime(timestamp);
        approve.setApproveResult(2);
        approve.setApproveResultDesc("通过");
        approve.setApproveContent(7);

        //一、将审核结果写入数据库
        int i = lsjsService.updateApprove(approve);

        //将审核记录插入审核记录表
        ApproveLog approveLog = new ApproveLog();
        BeanUtils.copyProperties(approve, approveLog);
        approveLog.setApproveContentDesc("地区经理审核");
        int i1 = lsjsService.insertApproveLog(approveLog);
        if (i < 1 || i1 < 1){
            return AjaxResult.success("审核结果提交失败");
        }
        //二、发送至财务和其他审核人审核
        SendMsgRes sendMsgRes = new SendMsgRes();
        String quitPernr = regionalOrAreaApproveDto.getQuitPernr();
        String userName = lsjsService.getUserNameByPernr(quitPernr);
        sendMsgRes = financeProcessService.sendCareMsg(regionalOrAreaApproveDto.getLaunchId(), quitPernr, userName);
        sendMsgRes = financeProcessService.sendLoanAndShortMsg(regionalOrAreaApproveDto.getLaunchId(), quitPernr,userName);
        sendMsgRes = financeProcessService.sendQualityMsg(regionalOrAreaApproveDto.getLaunchId(), quitPernr, userName);
        sendMsgRes = toolProcessService.sendtoolMsg(regionalOrAreaApproveDto.getLaunchId(), quitPernr, userName);

        if (sendMsgRes.getErrcode() != 0){
            return AjaxResult.error("审核结果提交失败，原因可能是发送至财务部或后勤部审核失败，请重试或联系管理员！");
        }
        return AjaxResult.success("审核结果提交成功");
    }

    /**
     * 地区经理审核时点击【退回】按钮
     * @return
     */
    @PostMapping(path = "/area/noAdopt")
    public AjaxResult areaNoAdopt(RegionalOrAreaApproveDto regionalOrAreaApproveDto){

        //将审核记录插入审核记录表
        //获取当前系统时间
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        ApproveLog approveLog = new ApproveLog();
        approveLog.setApproveContent(6);
        approveLog.setApproveContentDesc("地区经理审核");
        approveLog.setApproveResult(4);
        approveLog.setApproveResultDesc("退回");
        approveLog.setApproveOpinion(regionalOrAreaApproveDto.getAuditMind());
        approveLog.setApproveTime(timestamp);
        approveLog.setQuitPernr(regionalOrAreaApproveDto.getQuitPernr());
        approveLog.setReviewerPernr(regionalOrAreaApproveDto.getReviewerPernr());
        int i1 = lsjsService.insertApproveLog(approveLog);

        //二、重新发起离司结算申请并发送至直接上级企业微信中
        //1、根据离职员工工号到人事导入数据表中查询直接上级工号
        ImportData importData = importDataService.getLastImportDataByPernr(regionalOrAreaApproveDto.getQuitPernr());
        ImportDataInfo importDataInfo = new ImportDataInfo();
        //根据工号查询员工姓名，向直接上级推送消息时使用
        String userName = lsjsService.getUserNameByPernr(importData.getQuitPernr());
        //为ImportDataDto实体赋值
        BeanUtils.copyProperties(importData, importDataInfo);
        importDataInfo.setQuitPernr(importData.getQuitPernr());
        importDataInfo.setQuitName(userName);

        List<ImportDataInfo> importDataInfoList = new ArrayList<>();
        importDataInfoList.add(importDataInfo);
        if (importDataInfoList.size() == 0){
            return AjaxResult.error("找不到该员工直接上级等信息，请联系管理员！");
        }
        //2、调用发送至直接上级企业微信接口向直接上级重新推送该员工的离司结算申请
        String isReturn = "2";
        SendMsgRes sendMsgRes = importDataService.launchProcess(importDataInfoList, isReturn);
        if (sendMsgRes.getErrcode() != 0){
            return AjaxResult.error("重新发送至直接上级失败，请联系管理员处理！");
        }
        return AjaxResult.success("退回成功");
    }
}
