package com.syx.service;

import com.syx.domain.*;
import com.syx.domain.vo.AuditUserRes;
import com.syx.domains.dto.ApproveGetDto;
import com.syx.domains.dto.ImportDataDto;
import com.syx.domains.dto.ImportDataGetDto;
import com.syx.domains.vo.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author 宋远欣
 * @date 2022/5/18
 **/
public interface ILsjsService {

    String getUserNameByPernr(String pernr);

    SAPUserInfo getUserInfoByPernr(String pernr);

    String getLeaveDateByPernr(String pernr);

    int insertApprove(Approve approve);

    int insertImportData(ImportData importData);

    int updateApprove(Approve approve);

    ImportData getLastImoprtDataByPernr(String quitPernr);

    Reviewer getReviewer(String personScope, String parcelDesc, String region);

    String getDepartmentByPernr(String quitPernr);

    List<String> getAuditQuitPernr(String reviewerPernr);

    List<AuditUserRes> getUserInfoByPernrList(List<String> quitPernrList, String reviewerPernr);

    List<QueryApproveRes> queryApproveByPernr(String pernr);

    int deleteApproveByPernr(String quitPernr, String approveContent);

    int insertApproveLog(ApproveLog approveLog);

    List<SearchUserInfoRes> getUserPernrOrUserName(String pernrOrName);

    String getDutySystemByPernr(String quitPernr);

    int getPDKKandRZLL(List<String> quitPernrList) throws Exception;

    int insertDeduction(Map<String, List<ItPDKK>> itPDKKMap);

    int insertResume(Map<String, List<ItRZLI>> itRZLIMap) throws ParseException;

    List<Approve> getApproveByPernr(String pernr);

    int deleteImportData(List<ImportDataDto> dataList);

    int deletePDKKandRZLL(List<ImportDataDto> dataList);

    List<ResumeRes> getResume(String pernr);

    List<Deduction> getDeduction(String pernr);

    List<String> getPernrImoprtDataByTime(ApproveGetDto approveGetDto);

    List<ApproveGetRes> getApproveDataByPernr(List<ImportData> imoprtPernrByTime);

    List<ApproveGetRes> getLsjsList(ApproveGetDto approveGetDto);

    String getLeaveResonByQuitPernr(String pernr);

    List<ImportDataRes> getImportDataList(String quitPernr);

    int deleteDataByPernr(String quitPernr, Integer launchId);

    List<ApproveDataRes> getApproveDataRes(ImportDataGetDto dataResByTime);

    int insertDeleteLog(DeleteImportDataLog deleteImportDataLog);

    /**
     * 删除借款余额数据表
     * @param dataList
     * @return
     */
    int deleteJKYE (List<ImportDataDto> dataList);
    /**
     * 调用SAP接口获得借款余额
     * @param quitPernrList
     * @return
     * @throws Exception
     */
    int getJKYE(List<String> quitPernrList) throws Exception;

    /**
     * 借款余额插入数据库
     * @return
     * @throws Exception
     */
    int insertLoanBalance(Map<String,List<ItJKYE>> itJKYEMap);
}
