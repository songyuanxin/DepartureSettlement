package com.syx.service.impl;

import com.syx.client.HttpClientUtils;
import com.syx.domain.*;
import com.syx.domain.vo.AuditUserRes;
import com.syx.domains.dto.ImportDataDto;
import com.syx.domains.vo.*;
import com.syx.mapper.SAPStoreHead.SAPStoreHeadMapper;
import com.syx.mapper.lsjs.*;
import com.syx.service.ILsjsService;
import com.syx.utils.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 宋远欣
 * @date 2022/5/18
 **/
@Service
public class LsjsServiceImpl implements ILsjsService {

    @Autowired
    private SAPUserInfoMapper sapUserInfoMapper;

    @Autowired
    private ApproveMapper approveMapper;

    @Autowired
    private ImportDataMapper importDataMapper;

    @Autowired
    private ReviewerMapper reviewerMapper;

    @Autowired
    private ApproveLogMapper approveLogMapper;

    @Autowired
    private SAPStoreHeadMapper sapStoreHeadMapper;

    @Autowired
    private DeductionMapper deductionMapper;

    @Autowired
    private ResumeMapper resumeMapper;

    /**
     * 根据工号查询员工姓名
     * @param pernr
     * @return
     */
    @Override
    public String getUserNameByPernr(String pernr) {
        return sapUserInfoMapper.getUserNameByPernr(pernr);
    }

    /**
     * 根据工号查询最近一次发起的离司结算申请
     * @param pernr
     * @return
     */
    @Override
    public Approve getApproveLastByPernr(String pernr) {
        return approveMapper.getApproveLastByPernr(pernr);
    }

    /**
     * 根据工号查询员工基本信息
     * @param pernr
     * @return
     */
    @Override
    public SAPUserInfo getUserInfoByPernr(String pernr) {
        return sapUserInfoMapper.getUserInfoByPernr(pernr);
    }

    @Override
    public String getLeaveDateByPernr(String pernr) {
        return sapUserInfoMapper.getLeaveDateByPernr(pernr);
    }

    /**
     * 将发送成功的离司结算申请写入数据库
     * @param approve
     * @return
     */
    @Override
    public int insertApprove(Approve approve) {
        return approveMapper.insertApprove(approve);
    }

    /**
     * 将导入数据写入数据库备份
     * @param importData
     * @return
     */
    @Override
    public int insertImportData(ImportData importData) {
        return importDataMapper.insertImportData(importData);
    }

    /**
     * 更新审核表中的审核结果
     * @param approve
     * @return
     */
    @Override
    public int updateApprove(Approve approve) {
        return approveMapper.updateApprove(approve);
    }

    /**
     * 根据离职员工工号查询人事导入数据
     * @param quitPernr
     * @return
     */
    @Override
    public ImportData getImoprtDataByPernr(String quitPernr) {
        return importDataMapper.getImoprtDataByPernr(quitPernr);
    }

    /**
     * 根据人员范围、所属分部、所属地区查询各部分审核人工号
     * @param personScope
     * @param parcelDesc
     * @param region
     * @return
     */
    @Override
    public Reviewer getReviewer(String personScope, String parcelDesc, String region) {
        return reviewerMapper.getReviewer(personScope, parcelDesc, region);
    }

    /**
     * 根据员工工号查询所在门店编码
     * @param quitPernr
     * @return
     */
    @Override
    public String getDepartmentByPernr(String quitPernr) {
        return sapUserInfoMapper.getDepartmentByPernr(quitPernr);
    }

    /**
     * 根据审核人工号查询待自己审核的离职员工工号
     * @param reviewerPernr
     * @return
     */
    @Override
    public List<String> getAuditQuitPernr(String reviewerPernr) {
        return approveMapper.getAuditQuitPernr(reviewerPernr);
    }

    /**
     * 根据离职员工工号批量查询员工信息
     * @param quitPernrList
     * @return
     */
    @Override
    public List<AuditUserRes> getUserInfoByPernrList(List<String> quitPernrList) {
        if (quitPernrList.size() == 0){
            return null;
        }
        List<AuditUserRes> auditUserResList = new ArrayList<>();
        for (String quitPernr:quitPernrList){
            AuditUserRes auditUserRes = sapUserInfoMapper.getUserInfoByPernrList(quitPernr);
            List<Deduction> deductionByPernr = deductionMapper.getDeductionByPernr(quitPernr);
            List<ResumeRes> resumeByPernr = resumeMapper.getResumeByPernr(quitPernr);

            auditUserRes.setDeductions(deductionByPernr);
            auditUserRes.setResumes(resumeByPernr);

            String department = "";
            department = sapStoreHeadMapper.getSAPStoreNameByStoreId(auditUserRes.getDepartment());
            if (StringUtils.isNotBlank(department)){
                auditUserRes.setDepartment(department);
            }
            auditUserResList.add(auditUserRes);
        }
        return auditUserResList;
    }

    /**
     * 根据员工工号查询审核表中审核记录
     * @param pernr
     * @return
     */
    @Override
    public List<QueryApproveRes> queryApproveByPernr(String pernr) {
        List<QueryApproveRes> approve = approveMapper.queryApproveByPernr(pernr);
        List<QueryApproveRes> queryApproveResList = new ArrayList<>();
        //获取当前系统时间
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //遍历数据库查询到的所有审核结果
        for (QueryApproveRes approveRes : approve){
            QueryApproveRes queryApproveRes = new QueryApproveRes();
            LocalDateTime now = LocalDateTime.now();
            String nowTime = df.format(now);
            //如果审核已经通过则显示审核人联系电话
            if (approveRes.getApproveResult().equals("通过") || approveRes.getApproveResult().equals("同意")){
                BeanUtils.copyProperties(approveRes, queryApproveRes);
            }else {
                //格式化时间格式
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    //当前时间转为Date类型
                    java.util.Date nowDate = format.parse(nowTime);
                    //审核人接收时间转为Date类型
                    java.util.Date sendTime = format.parse(approveRes.getSendTime());
                    //当前时间减审核人接收时间
                    long diff = nowDate.getTime() - sendTime.getTime();
                    //判断当前时间是否已经大于审核人接收时间3天
                    if (diff / (24 * 60 * 60 * 1000) > 3){
                        //若已经接收超过3天则显示审核人联系电话
                        BeanUtils.copyProperties(approveRes, queryApproveRes);
                    }else if (diff / (24 * 60 * 60 * 1000) <= 3){
                        //若接收时间不超过3天则不显示审核人联系电话
                        BeanUtils.copyProperties(approveRes, queryApproveRes);
                        queryApproveRes.setApproverPhone("");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            queryApproveResList.add(queryApproveRes);
        }
        return queryApproveResList;
    }

    /**
     * 根据离职员工工号删除审核表中该员工的所有记录
     * @param quitPernr
     * @return
     */
    @Override
    public int deleteApproveByPernr(String quitPernr, String approveContent) {
        return approveMapper.deleteApproveByPernr(quitPernr, approveContent);
    }

    /**
     * 将审核记录插入审核记录表
     * @param approveLog
     * @return
     */
    @Override
    public int insertApproveLog(ApproveLog approveLog) {
        return approveLogMapper.insertApproveLog(approveLog);
    }

    /**
     * 根据离职员工工号查询所属地区经理工号
     * @param quitPernr
     * @return
     */
    public String getAreaPernrByQuitPernr(String quitPernr) {
        return approveMapper.getAreaPernrByQuitPernr(quitPernr);
    }

    /**
     * 根据工号查询职能体系
     * @param quitPernr
     * @return
     */
    public String getDutySystemByPernr(String quitPernr) {
        return sapUserInfoMapper.getDuyuSystemByPernr(quitPernr);
    }

    /**
     * 获取离职员工的盘点扣款以及任职履历
     * @param quitPernrList
     * @return
     */
    @Override
    public int getPDKKandRZLL(List<String> quitPernrList) throws Exception {
        String postUrl = "http://s4hanadb01.jzj.cn:8002/sap/bc/srt/rfc/sap/zhr_interface_rfc008/800/zhr_interface_rfc008/zhr_interface_rfc008";
        String Username = "S4CONN";
        String Password = "SAP_po1234**";
        String soapXml = getXML(quitPernrList);
        String result = HttpClientUtils.postSoapOne(soapXml, postUrl, Username, Password);
        List<ItPDKK> itPDKKList = new ArrayList<>();
        List<ItRZLI> itRZLIList = new ArrayList<>();

        List<Map<String, String>> maps = parseSoap(result);

        for (Map<String, String> map : maps){
            ItPDKK itPDKK = new ItPDKK();
            ItRZLI itRZLI = new ItRZLI();
            if (map.size() == 3){
                itPDKK.setPernr(map.get("PERNR"));
                itPDKK.setZyyyynn(map.get("ZYYYYNN"));
                itPDKK.setBetrg(map.get("BETRG"));
                itPDKKList.add(itPDKK);
            }else if(map.size() == 7){
                itRZLI.setPernr(map.get("PERNR"));
                itRZLI.setBegda(map.get("BEGDA"));
                itRZLI.setEndda(map.get("ENDDA"));
                itRZLI.setStoreName(map.get("STEXT1"));
                itRZLI.setStoreId(map.get("SHORT"));
                itRZLI.setPosition(map.get("STEXT2"));
                itRZLI.setPost(map.get("STEXT3"));
                itRZLIList.add(itRZLI);
            }
        }
        Map<String, List<ItPDKK>> itPDKKMap = itPDKKList.stream().collect(Collectors.groupingBy(item -> item.getPernr()));
        Map<String, List<ItRZLI>> itRZLIMap = itRZLIList.stream().collect(Collectors.groupingBy(item -> item.getPernr()));
        int i = 0;
        if (itPDKKMap.size() > 0 || itRZLIMap.size() > 0){
            int insertDeduction = insertDeduction(itPDKKMap);
            if (itPDKKMap.size() > 0 && insertDeduction <= 0){
                return -1;
            }
            int insertResume = insertResume(itRZLIMap);
            if (itRZLIMap.size() > 0 && insertResume <= 0){
                return -1;
            }
            i = i + insertDeduction + insertResume;
        }
        return i;
    }

    private String getXML(List<String> pernrList) {
        StringBuilder pernr = new StringBuilder();
        for (String quitPernr : pernrList) {
            pernr.append("<item>" + "<PERNR>" + quitPernr + "</PERNR>" + "</item>");
        }
        String soapXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<urn:ZHR_INTERFACE_RFC008>"
                + "<INPUT>"
                + pernr
                + "</INPUT>"
                + "</urn:ZHR_INTERFACE_RFC008>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";
        return soapXml;
    }

    public static List<Map<String, String>> parseSoap(String soap) throws DocumentException, DocumentException {
        org.dom4j.Document doc = DocumentHelper.parseText(soap);//报文转成doc对象
        Element root = doc.getRootElement();//获取根元素，准备递归解析这个XML树
        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, String>> mapList = new ArrayList<>();
        List<Map<String, String>> code = getCode(root, mapList);
        return code;
    }

    public static List<Map<String, String>> getCode(Element root, List<Map<String, String>> mapList) {

        Map<String, String> map = new HashMap<String, String>();

        if (root.elements() != null) {
            List<Element> list = root.elements();//如果当前根节点有子节点，找到子节点
            for (Element e : list) {//遍历每个节点
                if (e.elements().size() > 0) {
                    getCode(e, mapList);//当前节点不为空的话，递归遍历子节点；
                }
                if (e.elements().size() == 0) {
                    map.put(e.getName(), e.getTextTrim());
                }//如果为叶子节点，那么直接把名字和值放入map
            }
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 将盘点扣款插入数据库
     * @param itPDKKMap
     * @return
     */
    @Override
    public int insertDeduction(Map<String, List<ItPDKK>> itPDKKMap) {
        int i = 0;
        for (String pernr : itPDKKMap.keySet()){
            List<ItPDKK> itPDKKList = itPDKKMap.get(pernr);
            for (ItPDKK itPDKK : itPDKKList){
                Deduction deduction = new Deduction();
                deduction.setPernr(itPDKK.getPernr().substring(2));
                deduction.setMonth(itPDKK.getZyyyynn());
                deduction.setMoney(new BigDecimal(itPDKK.getBetrg()).setScale(2, BigDecimal.ROUND_HALF_UP));
                List<Deduction> deductionByPernr = deductionMapper.getDeductionByPernr(deduction.getPernr());
                int insertResult = 0;
                if (deductionByPernr.size() == 0){
                    insertResult = deductionMapper.insertDeduction(deduction);
                    i = insertResult + i;
                }else if(deductionByPernr.size() > 0){
                    int deleteResult = deductionMapper.deleteDeduction(deduction.getPernr());
                    insertResult = deductionMapper.insertDeduction(deduction);
                    i = insertResult + i;
                }
            }
        }
        return i;
    }

    /**
     * 将任职履历插入数据库
     * @param itRZLIMap
     * @return
     */
    @Override
    public int insertResume(Map<String, List<ItRZLI>> itRZLIMap) throws ParseException {
        int i = 0;
        for (String pernr : itRZLIMap.keySet()){
            List<ItRZLI> itPDKKList = itRZLIMap.get(pernr);
            for (ItRZLI itRZLI : itPDKKList){
                Resume resume = new Resume();
                resume.setPernr(itRZLI.getPernr().substring(2));
                java.util.Date start = new SimpleDateFormat("yyyy-MM-dd").parse(itRZLI.getBegda());
                resume.setStartDate(new Date(start.getTime()));
                java.util.Date end = new SimpleDateFormat("yyyy-MM-dd").parse(itRZLI.getEndda());
                resume.setEndDate(new Date(end.getTime()));
                resume.setStoreId(itRZLI.getStoreId());
                resume.setStoreName(itRZLI.getStoreName());
                resume.setPosition(itRZLI.getPosition());
                resume.setPost(itRZLI.getPost());
                List<ResumeRes> resumeByPernr = resumeMapper.getResumeByPernr(resume.getPernr());
                int insertResult = 0;
                if (resumeByPernr.size() == 0){
                    insertResult = resumeMapper.insertResume(resume);
                    i = insertResult + i;
                }else if (resumeByPernr.size() > 0){
                    int deleteResult = resumeMapper.deleteResume(resume.getPernr());
                    insertResult = resumeMapper.insertResume(resume);
                    i = insertResult + i;
                }
            }
        }
        return i;
    }

    @Override
    public List<Approve> getApproveByPernr(String pernr) {
        return approveMapper.getApproveByPernr(pernr);
    }

    @Override
    public int deleteImportData(List<ImportDataDto> dataList) {
        int i = 0;
        for (ImportDataDto importDataDto:dataList){
            int deleteImport = importDataMapper.deleteImportData(importDataDto.getPernr());
            i = i + deleteImport;
        }
        return i;
    }

    @Override
    public int deletePDKKandRZLL(List<ImportDataDto> dataList) {
        int i = 0;
        for (ImportDataDto importDataDto:dataList){
            int deleteResume = resumeMapper.deleteResume(importDataDto.getPernr());
            int deleteDeduction = deductionMapper.deleteDeduction(importDataDto.getPernr());
            i = deleteResume + deleteDeduction + i;
        }
        return i;
    }

    @Override
    public List<ResumeRes> getResume(String pernr) {
        return resumeMapper.getResumeByPernr(pernr);
    }

    @Override
    public List<Deduction> getDeduction(String pernr) {
        return deductionMapper.getDeductionByPernr(pernr);
    }

    @Override
    public List<SearchUserInfoRes> getUserPernrOrUserName(String pernrOrName) {
        return sapUserInfoMapper.getUserPernrOrUserName(pernrOrName);
    }
}
