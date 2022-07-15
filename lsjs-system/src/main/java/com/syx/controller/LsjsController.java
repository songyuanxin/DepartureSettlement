package com.syx.controller;

import com.syx.domain.Approve;
import com.syx.domain.DeleteImportDataLog;
import com.syx.domain.SAPUserInfo;
import com.syx.domain.vo.AuditUserRes;
import com.syx.domains.AjaxResult;
import com.syx.domains.dto.ApproveGetDto;
import com.syx.domains.dto.DeleteImportDataDto;
import com.syx.domains.dto.ImportDataGetDto;
import com.syx.domains.vo.*;
import com.syx.service.ILsjsService;
import com.syx.service.ISAPStoreHeadService;
import com.syx.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author 宋远欣
 * @date 2022/5/21
 **/
@RestController
@RequestMapping(path = "/lsjs")
@Slf4j
public class LsjsController {

    @Autowired
    private ILsjsService lsjsService;

    @Autowired
    private ISAPStoreHeadService isapStoreHeadService;

    /**
     * 审核人点击审批入口后调用此接口获取待审核列表
     *
     * @param reviewerPernr
     * @return
     */
    @PostMapping(value = "/getAuditQuitUser")
    public AjaxResult getAuditQuitUser(String reviewerPernr) {
        List<String> auditQuitPernr = lsjsService.getAuditQuitPernr(reviewerPernr);
        List<AuditUserRes> auditUserRes = lsjsService.getUserInfoByPernrList(auditQuitPernr, reviewerPernr);
        if (auditUserRes == null) {
            return AjaxResult.success("暂无待审核的离司结算申请");
        }
        return AjaxResult.success("查询成功", auditUserRes);
    }

    /**
     * 验证工号与身份证后六位是否正确
     *
     * @param pernr
     * @param password
     * @return
     */
    @PostMapping(value = "/queryCheck")
    public AjaxResult queryCheck(String pernr, String password) {
        //一、校验该工号是否存在
        SAPUserInfo userInfoByPernr = lsjsService.getUserInfoByPernr(pernr);
        if (userInfoByPernr == null) {
            return AjaxResult.error("该工号不存在或无效！");
        }
        String idNumber = userInfoByPernr.getIdNumber().substring(userInfoByPernr.getIdNumber().length() - 6, userInfoByPernr.getIdNumber().length());
        //二、若工号存在校验身份证后六位是否正确
        if (!password.equals(idNumber)) {
            return AjaxResult.error("身份证后六位不正确，请重新输入！");
        }
        return AjaxResult.success("校验通过");
    }

    /**
     * 查询离司结算流程审核情况
     *
     * @param pernr
     * @return
     */
    @PostMapping(value = "/query")
    public AjaxResult query(String pernr) throws ParseException {
        //三、若工号与身份证后六位均正确查询该员工离司结算审核情况
        List<QueryApproveRes> approveByPernrList = lsjsService.queryApproveByPernr(pernr);
        String userNameByPernr = lsjsService.getUserNameByPernr(pernr);

        QueryApproveStatusRes approveStatusResList = new QueryApproveStatusRes();
        if (approveByPernrList.size() == 0) {
            approveStatusResList.setStatus(1);
            approveStatusResList.setName(userNameByPernr);
            return AjaxResult.error("离司结算流程待人事发起，如有疑问请联系分部处理！", approveStatusResList);
        }
        /**
         * 1、无论是门店员工还是职能员工，若要走完所有流程最低会有4条审核记录，若没有4条表示离司结算任处于审批中。
         * 2、无论是门店员工还是职能员工，借款短款审核、质量简报扣款审核、工牌工装扣款审核是同步进行，且属于流程的最后一个审批环节。
         */
        if (approveByPernrList.size() < 4){
            approveStatusResList.setStatus(2);
            approveStatusResList.setName(userNameByPernr);
            approveStatusResList.setQueryApproveRes(approveByPernrList);
            return AjaxResult.success("查询成功",approveStatusResList);
        }
        //遍历审核记录，若判断审核节点是否都已全部通过
        List<String> resultList = new ArrayList<>();
        for (QueryApproveRes queryApproveRes:approveByPernrList){
            if (queryApproveRes.getApproveResult().equals("不通过") || queryApproveRes.getApproveResult().equals("待审核")){
                resultList.add(queryApproveRes.getApproveResult());
            }
            if (queryApproveRes.getApproveResult().equals("通过")){
                queryApproveRes.setApproveResult("已审核");
            }
        }
        if (resultList.size() > 0){
            approveStatusResList.setStatus(2);
            approveStatusResList.setName(userNameByPernr);
            approveStatusResList.setQueryApproveRes(approveByPernrList);
            return AjaxResult.success("查询成功",approveStatusResList);
        }
        approveStatusResList.setStatus(3);
        approveStatusResList.setName(userNameByPernr);
        approveStatusResList.setQueryApproveRes(approveByPernrList);
        //3、如果该员工的离司结算已全部审核完成，则需要判断审核完成时间是否在当月5日之前，若在当月5日以及5日之前则只能在当月15日之前查询审核进度
        //审核完成时间
        if (approveStatusResList.getStatus() == 3){
            //获取当前系统时间
            LocalDate now = LocalDate.now();
            //审核完成时间
            QueryApproveRes queryApproveRes = approveByPernrList.stream().max(Comparator.comparing(QueryApproveRes::getApproveTime)).get();
            String approveTime = queryApproveRes.getApproveTime();
            //指定审核完成时间转换格式
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
            //将审核时间转换为Date类型
            Date parse = format.parse(approveTime);
            Instant instant = parse.toInstant();
            //将审核时间转换为LocalDateTime类型
            LocalDateTime allApproveTime = LocalDateTime.ofInstant(instant, ZoneId.ofOffset("GMT", ZoneOffset.ofHours(8)));
            //跨年查询的直接返回查不到
            if (now.getYear() != allApproveTime.getYear()){
                approveStatusResList.setStatus(4);
                return AjaxResult.error("您的离职工资结算流程已完结！", approveStatusResList);
            }
            //先判断查询时间与审核完成时间是否在同一个月
            if (now.getMonth() == allApproveTime.getMonth()){
                if (allApproveTime.getDayOfMonth() <= 5 && now.getDayOfMonth() >= 15){
                    approveStatusResList.setStatus(4);
                    return AjaxResult.error("您的离职工资结算流程已完结！", approveStatusResList);
                }
            }else {
                if (now.getMonthValue() > allApproveTime.getMonthValue()){
                    if (allApproveTime.getDayOfMonth() <= 5){
                        approveStatusResList.setStatus(4);
                        return AjaxResult.error("您的离职工资结算流程已完结！", approveStatusResList);
                    }else if (allApproveTime.getDayOfMonth() > 5 && now.getDayOfMonth() > 15){
                        approveStatusResList.setStatus(4);
                        return AjaxResult.error("您的离职工资结算流程已完结！", approveStatusResList);
                    }
                }
            }
        }
        return AjaxResult.success("查询成功", approveStatusResList);
    }

    /**
     * 直接上级审核门店员工时选择区域经理和地区经理使用远程搜索
     *
     * @param pernr
     * @return
     */
    @PostMapping(value = "/searchUserByPernr")
    public AjaxResult searchUserByPernr(String pernr) {
        List<SearchUserInfoRes> infoResList = new ArrayList<>();
        if (pernr.length() > 1){
            infoResList = lsjsService.getUserPernrOrUserName(pernr);
        }
        return AjaxResult.success("查询成功", infoResList);
    }

    /**
     * 根据门店编码获取门店名称
     *
     * @param storeId
     * @return
     */
    @PostMapping(value = "/getStoreNameByStoreId")
    public AjaxResult getStoreNameByStoreId(String storeId) {
        String storeName = isapStoreHeadService.getStoreNameByStoreId(storeId);
        if (StringUtils.isBlank(storeName)) {
            return AjaxResult.error("门店名称查询失败");
        }
        return AjaxResult.success("查询成功", storeName);
    }

    /**
     * 调用web service接口获取盘点扣款和任职履历
     *
     * @return
     * @throws DocumentException
     */
    @PostMapping(value = "/getPDKKandRZLL")
    public AjaxResult getPDKKandRZLL(List<String> quitPernrList) throws Exception {
        int insertResult = lsjsService.getPDKKandRZLL(quitPernrList);
        return AjaxResult.success("请求成功",insertResult);
    }

    /**
     * 人力资源中心获取审核数据(用于导出审核数据报表中)
     *
     * @param approveGetDto
     * @return
     */
    @GetMapping(value = "/getLsjsList")
    public AjaxResult getLsjsList(ApproveGetDto approveGetDto){
        if (StringUtils.isBlank(approveGetDto.getEndTime())){
            approveGetDto.setEndTime("");
        }
        if (approveGetDto.getApproveStatus() == null){
            approveGetDto.setApproveStatus(0);
        }
        //判断结束日期是否为空，若没有选择结束日期则直接默认查询到当天
        if (approveGetDto.getEndTime().length() == 0){
            LocalDate date = LocalDate.now();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            approveGetDto.setEndTime(date.format(fmt));
        }
        List<ApproveGetRes> lsjsList = lsjsService.getLsjsList(approveGetDto);
        return AjaxResult.success(lsjsList);
    }

    /**
     * 人力资源中心删除导入数据前查询导入数据(用于删除功能中)
     * @param quitPernr
     * @return
     */
    @GetMapping(value = "/getImportDataList")
    public AjaxResult getImportDataList(String quitPernr){
        List<ImportDataRes> importDataList = lsjsService.getImportDataList(quitPernr);
        return AjaxResult.success(importDataList);
    }

    /**
     * 人力资源中心删除人事导入时的错误数据(删除功能)
     * @param deleteImportDataDto
     * @return
     */
    @PostMapping(value = "/deleteDataByPernr/{quitPernr}")
    public AjaxResult deleteDataByPernr(@PathVariable DeleteImportDataDto deleteImportDataDto){
        int i = lsjsService.deleteDataByPernr(deleteImportDataDto.getQuitPernr(), deleteImportDataDto.getLaunchId());
        if (i == 0){
            return AjaxResult.error("删除该离职员工数据失败，请重试或联系管理员");
        }
        //记录删除操作
        DeleteImportDataLog deleteImportDataLog = new DeleteImportDataLog();
        deleteImportDataLog.setQuitPernr(deleteImportDataDto.getQuitPernr());
        deleteImportDataLog.setLaunchId(deleteImportDataDto.getLaunchId());
        deleteImportDataLog.setOriginatorPernr(deleteImportDataDto.getUserId());
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        deleteImportDataLog.setDeleteTime(timestamp);
        int i1 = lsjsService.insertDeleteLog(deleteImportDataLog);
        if (i1 == 0){
            return AjaxResult.error("记录删除操作时出错，请重试或联系管理员");
        }
        return AjaxResult.success("删除成功",i);
    }

    /**
     * 离司结算审核监控报表查询
     * @param importDataGetDto
     * @return
     */
    @GetMapping("/getApproveDataRes")
    public AjaxResult getApproveDataRes(ImportDataGetDto importDataGetDto){
        List<ApproveDataRes> approveDataRes = lsjsService.getApproveDataRes(importDataGetDto);
        return AjaxResult.success("返回成功", approveDataRes);
    }
}
