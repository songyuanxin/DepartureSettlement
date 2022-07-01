package com.syx.controller;

import com.syx.domain.Approve;
import com.syx.domain.Deduction;
import com.syx.domain.ImportData;
import com.syx.domain.SAPUserInfo;
import com.syx.domains.AjaxResult;
import com.syx.domains.dto.ImportDataDto;
import com.syx.domains.dto.UploadDto;
import com.syx.domains.vo.ResumeRes;
import com.syx.domains.vo.SendMsgRes;
import com.syx.mapper.lsjs.DeductionMapper;
import com.syx.mapper.lsjs.ResumeMapper;
import com.syx.service.IImportDataService;
import com.syx.service.ILsjsService;
import com.syx.service.impl.WeChatServiceImpl;
import com.syx.utils.ExcelUtil;
import com.syx.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 宋远欣
 * @date 2022/5/11
 **/
@RestController
@RequestMapping(path = "/importData")
@Slf4j
public class ImportDataController {

    @Autowired
    private IImportDataService importDataService;

    @Autowired
    private ILsjsService lsjsService;

    @Autowired
    private WeChatServiceImpl weChatService;

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private DeductionMapper deductionMapper;

    /**
     * 导入离司结算
     *
     * @param uploadDto
     * @return
     * @throws Exception
     */
    @PostMapping(path = "/import")
    public AjaxResult importData(UploadDto uploadDto) throws Exception {
        ExcelUtil<ImportDataDto> util = new ExcelUtil<ImportDataDto>(ImportDataDto.class);
        List<ImportDataDto> dataList = util.importExcel(uploadDto.getFile().getInputStream());

        List<String> repeatList = new ArrayList<>();//存放导入数据中重复数据
        List<String> pErrorList = new ArrayList<>();//存放人员范围选择错误的工号加姓名
        List<String> invalidList = new ArrayList<>();//存放无效的员工工号和姓名
        List<String> noMatchingList = new ArrayList<>();//存放导入数据有错误的员工工号和姓名
        List<String> isRepeatList = new ArrayList<>();//存放导入数据有已经发起过离司结算的员工工号和姓名
        List<String> isErrorPernrList = new ArrayList<>();//存放导入数据中未离职员工工号和姓名
        List<String> idErrorDirectPernrList = new ArrayList<>();//存放导入数据中未离职的直接上级工号和姓名
        List<String> isReEntryList = new ArrayList<>();//存放属于再入职离职员工的工号

        //一、校验导入数据是否有重复，重复元素为p
        List<String> uniqueList = dataList.stream().collect(Collectors.groupingBy(ImportDataDto::getPernr, Collectors.counting()))
                .entrySet().stream().filter(e -> e.getValue() > 1)
                .map(Map.Entry::getKey).collect(Collectors.toList());
        if (uniqueList.size() > 0) {
            for (String pernr : uniqueList) {
                repeatList.add(pernr);
            }
            return AjaxResult.error("导入数据中" + repeatList + "的离司结算重复，请检查修改后重新导入数据！！");
        }

        List<String> quitPernrList = new ArrayList<>();

        for (ImportDataDto importDataDto : dataList) {
            //二、校验是否有人员范围超出“门店”、“职能”的
            if (!importDataDto.getPersonScope().equals("门店") && !importDataDto.getPersonScope().equals("职能")) {
                pErrorList.add(importDataDto.getPernr() + importDataDto.getName());
                return AjaxResult.error("导入数据中" + pErrorList + "所属人员范围选择错误，人员范围只能选择门店或职能，请检查修改后重新导入数据");
            }
            //获取人事导入所有人的工号
            quitPernrList.add(importDataDto.getPernr());
            //三、校验是否缺少直接上级等关键数据
            if (importDataDto.getDirectPernr().equals("") || importDataDto.getPersonScope().equals("") || importDataDto.getPernr().equals("") || importDataDto.getDirectName().equals("") || importDataDto.getName().equals("")) {
                return AjaxResult.error("<div>导入数据中缺少关键数据，请检查修改后重新导入数据！<div style=\"color:#81b6dd;\">导入数据模板要求：</br>1、必须填写离职员工工号、离职员工姓名、直接上级工号、直接上级姓名、人员范围；</br>2、若离职员工属于职能部门则必须填写所属分部</div></div>");
            }
            //四、校验离职原因为旷工的是否已填写旷工发文号
            //1、查询离职员工离职原因
            String leaveResonByQuitPernr = lsjsService.getLeaveResonByQuitPernr(importDataDto.getPernr());
            if (StringUtils.isNotBlank(leaveResonByQuitPernr)) {
                //2、判断该离职原因离职原因是否是旷工，若为旷工但没填写旷工发文号时不允许导入
                if (leaveResonByQuitPernr.equals("旷工") && importDataDto.getAbsenteeismDoc().equals("")) {
                    return AjaxResult.error("导入数据中" + importDataDto.getPernr() + importDataDto.getName() + "缺少旷工发文号，请检查修改后重新导入数据!(离职原因为旷工的离职员工必须填写旷工发文号)");
                }
            }
            String pernr = importDataDto.getPernr();
            String directPernr = importDataDto.getDirectPernr();
            //五、校验是否有无效的工号
            //1、校验是否有离职员工工号无效的
            String quitNameByPernr = lsjsService.getUserNameByPernr(pernr);
            if (StringUtils.isNotBlank(quitNameByPernr)) {
                if (quitNameByPernr.isEmpty()) {
                    invalidList.add(importDataDto.getPernr() + importDataDto.getName());
                }
            } else {
                invalidList.add(importDataDto.getPernr() + importDataDto.getName());
            }
            if (invalidList.size() > 0) {
                return AjaxResult.error("导入数据中" + invalidList + "离职员工不存在企业中或工号错误，请检查修改后重新导入数据！");
            }
            //2、校验是否有直接上级工号无效的
            String dirtlyNameByPernr = lsjsService.getUserNameByPernr(directPernr);
            if (StringUtils.isNotBlank(dirtlyNameByPernr)) {
                if (dirtlyNameByPernr.isEmpty()) {
                    invalidList.add(importDataDto.getDirectPernr() + importDataDto.getDirectName());
                }
            } else {
                invalidList.add(importDataDto.getPernr() + importDataDto.getName());
            }
            if (invalidList.size() > 0) {
                return AjaxResult.error("导入数据中" + invalidList + "直接上级不存在企业中或工号错误，请检查修改后重新导入数据！");
            }
            //六、检验导入数据中是否有未离职员工
            //1、检验是否有未离职的员工确存在导入数据中
            String leaveDateByPernr = lsjsService.getLeaveDateByPernr(pernr);
            if (StringUtils.isBlank(leaveDateByPernr)) {
                if (leaveDateByPernr == null || leaveDateByPernr.equals("")) {
                    isErrorPernrList.add(importDataDto.getPernr() + importDataDto.getName());
                }
            }
            if (isErrorPernrList.size() > 0) {
                return AjaxResult.error("导入数据中" + isErrorPernrList + "暂未离职，请检查修改后重新导入数据！");
            }
            //2、校验是否有已经离职的直接上级却存在导入数据中
            String leaveDateByDirectPernr = lsjsService.getLeaveDateByPernr(directPernr);
            if (StringUtils.isNotBlank(leaveDateByDirectPernr)) {
                if (leaveDateByDirectPernr != null || leaveDateByDirectPernr.length() > 0) {
                    idErrorDirectPernrList.add(importDataDto.getDirectPernr() + importDataDto.getDirectName());
                }
            }
            if (idErrorDirectPernrList.size() > 0) {
                return AjaxResult.error("导入数据中" + idErrorDirectPernrList + "直接上级已经离职，请检查修改后重新导入数据！");
            }
            //七、校验工号和姓名是否匹配
            //根据工号查询离职员工姓名
            String name = "";
            String directName = "";
            try {
                name = lsjsService.getUserNameByPernr(pernr);
                directName = lsjsService.getUserNameByPernr(directPernr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //判断导入数据中的姓名与系统中的是否一致
            if (!name.equals(importDataDto.getName())) {
                noMatchingList.add(pernr + name);
            } else if (!directName.equals(importDataDto.getDirectName())) {
                noMatchingList.add(directPernr + directName);
            }
            if (noMatchingList.size() > 0) {
                return AjaxResult.error("导入数据中" + noMatchingList + "的工号与姓名不匹配，请检查修改后重新导入数据！！");
            }
            //八、校验是否存在已经发起过离司结算的员工
            List<Approve> approve = lsjsService.getApproveByPernr(pernr);
            List<ResumeRes> resume = lsjsService.getResume(pernr);
            List<Deduction> deduction = lsjsService.getDeduction(pernr);
            ImportData importData = lsjsService.getLastImoprtDataByPernr(pernr);
            //若查到发起记录
            if (importData != null) {
                //查询该员工最近一次入职日期
                SAPUserInfo userInfoByPernr = lsjsService.getUserInfoByPernr(pernr);
                //若最近一次发起时间在最近一次入职日期之后，则表示是错误数据
                if (importData.getImportTime().getTime() > userInfoByPernr.getJoinedDate().getTime()) {
                    isRepeatList.add(pernr + name);
                    return AjaxResult.error("导入数据中" + isRepeatList + "已经发起过离司结算申请，请检查修改后重新导入！");
                } else {
                    //若最近一次发起时间在最近一次入职日期之前表示是再入职员工,再入职员工需要先判断上一次离司结算是否已经全部审核完毕
                    Map<String, String> approveResult = new HashMap<>();
                    for (Approve approve1 : approve) {
                        if (approve1.getApproveResult() == 3 || approve1.getApproveResult() == 1) {
                            approveResult.put(approve1.getApproveContentDesc(), approve1.getApproveResultDesc());
                        }
                    }
                    if (approveResult.size() > 0) {
                        isRepeatList.add(pernr + name);
                        return AjaxResult.error("导入数据中" + isRepeatList + "可能属于再入职员工，已经发起过离司结算申请并且还存在未审核完成的内容，请检查修改后重新导入！");
                    }
                    //若属于再入职员工，再次发起离司结算时还需将之前获取到的任职履历和盘点扣款删除重新获取
                    List<ResumeRes> resumeByPernr = resumeMapper.getResumeByPernr(pernr);
                    List<Deduction> deductionByPernr = deductionMapper.getDeductionByPernr(pernr);
                    if (resumeByPernr.size() > 0) {
                        resumeMapper.deleteResume(pernr);
                    }
                    if (deductionByPernr.size() > 0) {
                        deductionMapper.deleteDeduction(pernr);
                    }
                    isReEntryList.add(pernr);
                }
            } else {
                if (approve.size() > 0) {
                    return AjaxResult.error("该模板中存在异常的数据，请联系管理员处理！");
                }
                if (resume.size() > 0) {
                    return AjaxResult.error("该模板中存在异常的数据，请联系管理员处理！");
                }
                if (deduction.size() > 0) {
                    return AjaxResult.error("该模板中存在异常的数据，请联系管理员处理！");
                }
            }
        }

        int i = 0;
        for (ImportDataDto data : dataList) {
            ImportData importData = new ImportData();
            if (isReEntryList.contains(data.getPernr())) {
                importData.setQuitPernr(data.getPernr());
                importData.setPersonScope(data.getPersonScope());
                importData.setDirectPernr(data.getDirectPernr());
                importData.setDivision(data.getDivision());
                importData.setAbsenteeismDoc(data.getAbsenteeismDoc());
                importData.setOriginatorPernr(uploadDto.getUserId());
                LocalDateTime now = LocalDateTime.now();
                Timestamp timestamp = Timestamp.valueOf(now);
                importData.setImportTime(timestamp);
                importData.setReEntry(1);
                //将导入数据写入数据库中备份
                i = lsjsService.insertImportData(importData);
                if (i == 0) {
                    return AjaxResult.error("流程发起失败，可能原因：导入数据备份时出现错误，请联系管理员处理！");
                }
            }else {
                importData.setQuitPernr(data.getPernr());
                importData.setPersonScope(data.getPersonScope());
                importData.setDirectPernr(data.getDirectPernr());
                importData.setDivision(data.getDivision());
                importData.setAbsenteeismDoc(data.getAbsenteeismDoc());
                importData.setOriginatorPernr(uploadDto.getUserId());
                LocalDateTime now = LocalDateTime.now();
                Timestamp timestamp = Timestamp.valueOf(now);
                importData.setImportTime(timestamp);
                importData.setReEntry(0);
                //将导入数据写入数据库中备份
                i = lsjsService.insertImportData(importData);
                if (i == 0) {
                    return AjaxResult.error("流程发起失败，可能原因：导入数据备份时出现错误，请联系管理员处理！");
                }
            }
        }

        //1、获取离职员工的盘点扣款以及任职履历并写入数据库
        int insertPdkkAndRzll = lsjsService.getPDKKandRZLL(quitPernrList);
        if (insertPdkkAndRzll == -1) {
            lsjsService.deleteImportData(dataList);
            return AjaxResult.error("流程发起失败，可能原因：保存离职员工盘点扣款或任职履历时出错，请联系管理员处理！");
        }
        String isReturn = "0";
        //导入数据时若以上校验通过则开始发起流程
        SendMsgRes sendMsgRes = importDataService.launchProcess(dataList, isReturn);
        if (sendMsgRes.getErrcode() != 0) {
            lsjsService.deleteImportData(dataList);
            lsjsService.deletePDKKandRZLL(dataList);
            return AjaxResult.error("流程发起失败，可能原因：发送至直接上级审核时出错，请联系管理员处理！");
        }
        return AjaxResult.success("流程发起成功", dataList);
    }

    /**
     * 获取操作人工号
     *
     * @param code
     * @return
     */
    @PostMapping("/getUserId/{code}")
    public AjaxResult getUserId(@PathVariable String code) {
        String userId = weChatService.getUserId(code);
        if (userId.equals("")) {
            return AjaxResult.error("获取用户ID失败");
        } else {
            return AjaxResult.success("获取用户ID成功", userId);
        }
    }

}
