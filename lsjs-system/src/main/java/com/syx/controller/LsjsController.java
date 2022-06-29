package com.syx.controller;

import com.syx.domain.SAPUserInfo;
import com.syx.domain.vo.AuditUserRes;
import com.syx.domains.AjaxResult;
import com.syx.domains.dto.ApproveGetDto;
import com.syx.domains.vo.*;
import com.syx.service.ILsjsService;
import com.syx.service.ISAPStoreHeadService;
import com.syx.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
        List<AuditUserRes> auditUserRes = lsjsService.getUserInfoByPernrList(auditQuitPernr);

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
    public AjaxResult query(String pernr) {
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
        if (approveByPernrList.size() < 5){
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
     * 人力资源中心获取审核数据
     *
     * @param approveGetDto
     * @return
     */
    @GetMapping(value = "/getLsjsList")
    public AjaxResult getLsjsList(ApproveGetDto approveGetDto){
        List<ApproveGetRes> lsjsList = lsjsService.getLsjsList(approveGetDto);
        return AjaxResult.success(lsjsList);
    }

}
