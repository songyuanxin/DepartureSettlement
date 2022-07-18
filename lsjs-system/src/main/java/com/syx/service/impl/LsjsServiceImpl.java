package com.syx.service.impl;

import com.syx.client.HttpClientUtils;
import com.syx.domain.*;
import com.syx.domain.vo.AuditUserRes;
import com.syx.domains.dto.ApproveGetDto;
import com.syx.domains.dto.ImportDataDto;
import com.syx.domains.dto.ImportDataGetDto;
import com.syx.domains.vo.*;
import com.syx.mapper.SAPStoreHead.SAPStoreHeadMapper;
import com.syx.mapper.lsjs.*;
import com.syx.service.ILsjsService;
import com.syx.utils.DateUtils;
import com.syx.utils.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
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

    @Autowired
    private DeleteImportDataLogMapper deleteImportDataLogMapper;

    /**
     * 根据工号查询员工姓名
     *
     * @param pernr
     * @return
     */
    @Override
    public String getUserNameByPernr(String pernr) {
        return sapUserInfoMapper.getUserNameByPernr(pernr);
    }

    /**
     * 根据工号查询员工基本信息
     *
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
     *
     * @param approve
     * @return
     */
    @Override
    public int insertApprove(Approve approve) {
        return approveMapper.insertApprove(approve);
    }

    /**
     * 将导入数据写入数据库备份
     *
     * @param importData
     * @return
     */
    @Override
    public int insertImportData(ImportData importData) {
        return importDataMapper.insertImportData(importData);
    }

    /**
     * 更新审核表中的审核结果
     *
     * @param approve
     * @return
     */
    @Override
    public int updateApprove(Approve approve) {
        return approveMapper.updateApprove(approve);
    }

    /**
     * 根据离职员工工号查询人事导入数据
     *
     * @param quitPernr
     * @return
     */
    @Override
    public ImportData getLastImoprtDataByPernr(String quitPernr) {
        return importDataMapper.getLastImoprtDataByPernr(quitPernr);
    }

    /**
     * 根据人员范围、所属分部、所属地区查询各部分审核人工号
     *
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
     *
     * @param quitPernr
     * @return
     */
    @Override
    public String getDepartmentByPernr(String quitPernr) {
        return sapUserInfoMapper.getDepartmentByPernr(quitPernr);
    }

    /**
     * 根据审核人工号查询待自己审核的离职员工工号
     *
     * @param reviewerPernr
     * @return
     */
    @Override
    public List<String> getAuditQuitPernr(String reviewerPernr) {
        return approveMapper.getAuditQuitPernr(reviewerPernr);
    }

    /**
     * 根据离职员工工号批量查询员工信息
     *
     * @param quitPernrList
     * @return
     */
    @Override
    public List<AuditUserRes> getUserInfoByPernrList(List<String> quitPernrList, String reviewerPernr) {
        if (quitPernrList.size() == 0) {
            return null;
        }
        List<AuditUserRes> auditUserResList = new ArrayList<>();
        for (String quitPernr : quitPernrList) {
            //根据离职员工工号和审核人工号查询审核ID和发起ID
            Approve approve = approveMapper.getApproveByReviewAndQuitPernr(quitPernr, reviewerPernr);
            //根据离职员工工号查询基本信息
            AuditUserRes auditUserRes = sapUserInfoMapper.getUserInfoByPernrList(quitPernr);
            //根据离职员工工号查询盘点扣款
            List<Deduction> deductionByPernr = deductionMapper.getDeductionByPernr(quitPernr);
            //根据离职员工工号查询任职履历
            List<ResumeRes> resumeByPernr = resumeMapper.getResumeByPernr(quitPernr);

            auditUserRes.setLaunchId(approve.getLaunchId());
            auditUserRes.setDeductions(deductionByPernr);
            auditUserRes.setResumes(resumeByPernr);

            String department = "";
            department = sapStoreHeadMapper.getSAPStoreNameByStoreId(auditUserRes.getDepartment());
            if (StringUtils.isNotBlank(department)) {
                auditUserRes.setDepartment(department);
            }
            auditUserResList.add(auditUserRes);
        }
        return auditUserResList;
    }

    /**
     * 根据员工工号查询审核表中审核记录
     *
     * @param pernr
     * @return
     */
    @Override
    public List<QueryApproveRes> queryApproveByPernr(String pernr) {
        //获取该离职员工最新导入数据
        ImportData lastImoprtDataByPernr = importDataMapper.getLastImoprtDataByPernr(pernr);
        //根据离职员工工号和导入时间查询最新审核数据
        List<QueryApproveRes> approve = approveMapper.queryApproveByPernr(lastImoprtDataByPernr.getQuitPernr(),lastImoprtDataByPernr.getLaunchId());

        List<QueryApproveRes> queryApproveResList = new ArrayList<>();
        //获取当前系统时间
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //遍历数据库查询到的所有审核结果
        for (QueryApproveRes approveRes : approve) {
            QueryApproveRes queryApproveRes = new QueryApproveRes();
            LocalDateTime now = LocalDateTime.now();
            String nowTime = df.format(now);
            //如果审核已经通过则显示审核人联系电话
            if (approveRes.getApproveResult().equals("通过") || approveRes.getApproveResult().equals("同意")) {
                BeanUtils.copyProperties(approveRes, queryApproveRes);
            } else {
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
                    if (diff / (24 * 60 * 60 * 1000) > 3) {
                        //若已经接收超过3天则显示审核人联系电话
                        BeanUtils.copyProperties(approveRes, queryApproveRes);
                    } else if (diff / (24 * 60 * 60 * 1000) <= 3) {
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
     *
     * @param quitPernr
     * @return
     */
    @Override
    public int deleteApproveByPernr(String quitPernr, String approveContent) {
        return approveMapper.deleteApproveByPernr(quitPernr, approveContent);
    }

    /**
     * 将审核记录插入审核记录表
     *
     * @param approveLog
     * @return
     */
    @Override
    public int insertApproveLog(ApproveLog approveLog) {
        return approveLogMapper.insertApproveLog(approveLog);
    }

    /**
     * 根据离职员工工号查询所属地区经理工号
     *
     * @param quitPernr
     * @return
     */
    public String getAreaPernrByQuitPernr(String quitPernr) {
        return approveMapper.getAreaPernrByQuitPernr(quitPernr);
    }

    /**
     * 根据工号查询职能体系
     *
     * @param quitPernr
     * @return
     */
    public String getDutySystemByPernr(String quitPernr) {
        return sapUserInfoMapper.getDuyuSystemByPernr(quitPernr);
    }

    /**
     * 获取离职员工的盘点扣款以及任职履历
     *
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

        for (Map<String, String> map : maps) {
            ItPDKK itPDKK = new ItPDKK();
            ItRZLI itRZLI = new ItRZLI();
            if (map.size() == 3) {
                itPDKK.setPernr(map.get("PERNR"));
                itPDKK.setZyyyynn(map.get("ZYYYYNN"));
                itPDKK.setBetrg(map.get("BETRG"));
                itPDKKList.add(itPDKK);
            } else if (map.size() == 7) {
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
        if (itPDKKMap.size() > 0 || itRZLIMap.size() > 0) {
            int insertDeduction = insertDeduction(itPDKKMap);
            if (itPDKKMap.size() > 0 && insertDeduction <= 0) {
                return -1;
            }
            int insertResume = insertResume(itRZLIMap);
            if (itRZLIMap.size() > 0 && insertResume <= 0) {
                return -1;
            }
            i = i + insertDeduction + insertResume;
        }
        return i;
    }

    /**
     * 拼接请求SAP WebService的请求报文
     *
     * @param pernrList
     * @return
     */
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

    /**
     * 处理SAP返回结果
     *
     * @param soap
     * @return
     * @throws DocumentException
     * @throws DocumentException
     */
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
     *
     * @param itPDKKMap
     * @return
     */
    @Override
    public int insertDeduction(Map<String, List<ItPDKK>> itPDKKMap) {
        int i = 0;
        for (String pernr : itPDKKMap.keySet()) {
            List<ItPDKK> itPDKKList = itPDKKMap.get(pernr);
            for (ItPDKK itPDKK : itPDKKList) {
                Deduction deduction = new Deduction();
                deduction.setPernr(itPDKK.getPernr().substring(2));
                deduction.setMonth(itPDKK.getZyyyynn());
                deduction.setMoney(new BigDecimal(itPDKK.getBetrg()).setScale(2, BigDecimal.ROUND_HALF_UP));
                List<Deduction> deductionByPernr = deductionMapper.getDeductionByPernr(deduction.getPernr());
                int insertResult = 0;
                if (deductionByPernr.size() == 0) {
                    insertResult = deductionMapper.insertDeduction(deduction);
                    i = insertResult + i;
                } else if (deductionByPernr.size() > 0) {
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
     *
     * @param itRZLIMap
     * @return
     */
    @Override
    public int insertResume(Map<String, List<ItRZLI>> itRZLIMap) throws ParseException {
        int i = 0;
        for (String pernr : itRZLIMap.keySet()) {
            List<ItRZLI> itPDKKList = itRZLIMap.get(pernr);
            for (ItRZLI itRZLI : itPDKKList) {
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
                if (resumeByPernr.size() == 0) {
                    insertResult = resumeMapper.insertResume(resume);
                    i = insertResult + i;
                } else if (resumeByPernr.size() > 0) {
                    int deleteResult = resumeMapper.deleteResume(resume.getPernr());
                    insertResult = resumeMapper.insertResume(resume);
                    i = insertResult + i;
                }
            }
        }
        return i;
    }

    /**
     * 根据员工工号获取审核数据
     *
     * @param pernr
     * @return
     */
    @Override
    public List<Approve> getApproveByPernr(String pernr) {
        return approveMapper.getApproveByPernr(pernr);
    }

    /**
     * 根据离职员工工号删除导入数据
     *
     * @param dataList
     * @return
     */
    @Override
    public int deleteImportData(List<ImportDataDto> dataList) {
        int i = 0;
        for (ImportDataDto importDataDto : dataList) {
            ImportData lastImoprtDataByPernr = importDataMapper.getLastImoprtDataByPernr(importDataDto.getPernr());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//定义格式
            String importTime = df.format(lastImoprtDataByPernr.getImportTime());
            int deleteImport = importDataMapper.deleteImportData(importDataDto.getPernr(), importTime);
            i = i + deleteImport;
        }
        return i;
    }

    /**
     * 根据离职员工工号删除任职履历和盘点扣款
     *
     * @param dataList
     * @return
     */
    @Override
    public int deletePDKKandRZLL(List<ImportDataDto> dataList) {
        int i = 0;
        for (ImportDataDto importDataDto : dataList) {
            int deleteResume = resumeMapper.deleteResume(importDataDto.getPernr());
            int deleteDeduction = deductionMapper.deleteDeduction(importDataDto.getPernr());
            i = deleteResume + deleteDeduction + i;
        }
        return i;
    }

    /**
     * 获取任职履历
     *
     * @param pernr
     * @return
     */
    @Override
    public List<ResumeRes> getResume(String pernr) {
        return resumeMapper.getResumeByPernr(pernr);
    }

    /**
     * 获取盘点扣款
     *
     * @param pernr
     * @return
     */
    @Override
    public List<Deduction> getDeduction(String pernr) {
        return deductionMapper.getDeductionByPernr(pernr);
    }

    /**
     * 查询某一时间段内人事导入的离职员工工号
     *
     * @param approveGetDto
     * @return
     */
    @Override
    public List<String> getPernrImoprtDataByTime(ApproveGetDto approveGetDto) {
        List<ImportData> imoprtDataByTime = importDataMapper.getImoprtDataByTime(approveGetDto);
        List<String> pernrList = new ArrayList<>();
        for (ImportData importData : imoprtDataByTime) {
            pernrList.add(importData.getQuitPernr());
        }
        return pernrList;
    }

    /**
     * 人力资源中心查询审核数据
     *
     * @param approveGetDto
     * @return
     */
    @Override
    public List<ApproveGetRes> getLsjsList(ApproveGetDto approveGetDto) {
        //根据开始日期和结束日期查询该时间段内人事发起离司结算人的工号
        List<ImportData> imoprtDataByTime = getImoprtDataByTime(approveGetDto);
        //根据工号查询离职员工离司结算审核数据
        List<ApproveGetRes> approveDataByPernr = getApproveDataByPernr(imoprtDataByTime);
        //若请求参数中有审批状态作为参数则需要对查询出来的审核数据进行过滤
        if (approveGetDto.getApproveStatus() > 0) {
            //若查询结果不为空时才需处理查询结果
            if (approveDataByPernr.size() > 0) {
                for (int i = 0; i < approveDataByPernr.size(); i++) {
                    if (approveDataByPernr.get(i).getApproveStatus() != approveGetDto.getApproveStatus()) {
                        approveDataByPernr.remove(i);
                        i--;
                    }
                }
            }
        }
        return approveDataByPernr;
    }

    /**
     * 根据开始日期和结束日期查询该时间段内人事发起离司结算人的工号
     *
     * @param approveGetDto
     * @return
     */
    private List<ImportData> getImoprtDataByTime(ApproveGetDto approveGetDto) {
        return importDataMapper.getImoprtDataByTime(approveGetDto);
    }

    /**
     * 根据员工工号查询审核数据(人力资源中心查询审核数据)
     *
     * @param importDataByTime
     * @return
     */
    @Override
    public List<ApproveGetRes> getApproveDataByPernr(List<ImportData> importDataByTime) {
        List<ApproveGetRes> approveDataList = new ArrayList<>();
        //根据开始日期和结束日期查询到这一时间段内导入的数据
        List<String> launchs = new ArrayList<>();
        List<String> storeQuitPernrList = new ArrayList<>();
        for (ImportData importData:importDataByTime){
            launchs.add("LaunchID" + "||" + importData.getLaunchId().toString() + "||");
            if (importData.getPersonScope().equals("门店")){
                storeQuitPernrList.add(importData.getQuitPernr());
            }
        }
        List<ApproveGetRes> exportApproveData = approveMapper.getApproveDataByLQ("exportApproveData", launchs);
        for (ApproveGetRes approveGetRes:exportApproveData){
            if (storeQuitPernrList.contains(approveGetRes.getPernr())){
                //根据店编查询门店主数据
                SAPStoreHead sapStoreHeadByStoreId = sapStoreHeadMapper.getSAPStoreHeadByStoreId(approveGetRes.getStoreName());
                //门店员工所属分部取门店主数据中的管理地区
                approveGetRes.setDivision(sapStoreHeadByStoreId.getManageArea());
            }
        }
        return exportApproveData;
    }

    /**
     * 获取审批状态字段值
     *
     * @param launchId
     * @param quitPernr
     * @return
     */
    private Integer getApproveResult(Integer launchId, String quitPernr) {
        List<Integer> resultList = new ArrayList<>();
        Integer approveStatus = 0;
        List<Integer> approveResultList = approveMapper.getApproveResultList(launchId, quitPernr);
        if (approveResultList.size() < 4) {
            approveStatus = 1;
        } else {
            for (Integer result : approveResultList) {
                if (result == 1 || result == 3) {
                    resultList.add(result);
                }
            }
            if (resultList.size() > 0) {
                approveStatus = 1;
            } else {
                approveStatus = 2;
            }
        }
        return approveStatus;
    }

    /**
     * 根据工号查询离职原因
     *
     * @param pernr
     * @return
     */
    @Override
    public String getLeaveResonByQuitPernr(String pernr) {
        return sapUserInfoMapper.getLeaveResonByQuitPernr(pernr);
    }

    /**
     * 人力资源中心删除导入数据前获取导入数据
     *
     * @param quitPernr
     * @return
     */
    @Override
    public List<ImportDataRes> getImportDataList(String quitPernr) {
        List<ImportDataRes> importDataList = importDataMapper.getImoprtDataByPernr(quitPernr);
        return importDataList;
    }

    /**
     * 人力资源中心删除人事导入时的错误数据
     *
     * @param quitPernr
     * @return
     */
    @Override
    public int deleteDataByPernr(String quitPernr, Integer launchId) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int i = 0;
        //判断导入数据表中是否存在该离职员工的数据
        ImportData importData = importDataMapper.getImportDataByQuitPernr(quitPernr, launchId);
        if(importData != null){
            int result = 0;
            result = importDataMapper.deleteImportDataByPernr(quitPernr, launchId);
            if (result == 0) {
                return result;
            }
            i = i + result;
        }
        //判断审核表中是否存在该离职员工的审核数据
        List<QueryApproveRes> approveByPernr = approveMapper.queryApproveByPernr(quitPernr, launchId);
        if (approveByPernr.size() > 0) {
            int result = 0;
            result = approveMapper.deleteApproveDataByPernr(quitPernr, launchId);
            if (result == 0) {
                return result;
            }
            i = i + result;
        }
        //判断是否获取过该离职员工的任职履历和盘点扣款
        List<ResumeRes> resumeByPernr = resumeMapper.getResumeByPernr(quitPernr);
        if (resumeByPernr.size() > 0) {
            int result = 0;
            result = resumeMapper.deleteResume(quitPernr);
            if (result == 0) {
                return result;
            }
            i = i + result;
        }
        List<Deduction> deductionByPernr = deductionMapper.getDeductionByPernr(quitPernr);
        if (deductionByPernr.size() > 0) {
            int result = 0;
            result = deductionMapper.deleteDeduction(quitPernr);
            if (result == 0) {
                return result;
            }
            i = i + result;
        }
        return i;
    }

    /**
     * 直接上级审核门店员工时选择区域经理和地区经理使用远程搜索
     *
     * @param pernrOrName
     * @return
     */
    @Override
    public List<SearchUserInfoRes> getUserPernrOrUserName(String pernrOrName) {
        return sapUserInfoMapper.getUserPernrOrUserName(pernrOrName);
    }

    /**
     * 查询离司监控报表实现方法
     *
     * @param
     * @return
     */
    @Override
    public List<ApproveDataRes> getApproveDataRes(ImportDataGetDto dataResByTime) {
        //TimeStamp转String的工具
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //判断如果前端传过来的参数没有结束时间，就获取当前系统时间为结束时间
        if (StringUtils.isBlank(dataResByTime.getImportEndTime()) || dataResByTime.getImportEndTime().equals("")) {
            //获取当前系统时间
            String importEndTime = DateUtils.getDate();
            //把获得的系统时间写进ImportDataGetDto
            dataResByTime.setImportEndTime(importEndTime);
        }
        List<ApproveDataRes> approveDataResList = new ArrayList<>();
        //如果前端传过来离职员工工号
        if (dataResByTime.getQuitPernr() != null && dataResByTime.getQuitPernr().length() != 0) {
            //创建ApproveGetDto对象
            ApproveGetDto approveGetDto = new ApproveGetDto();
            //把ImportDataGetDto赋值给ApproveGetDto
            approveGetDto.setStartTime(dataResByTime.getImportStartTime());
            approveGetDto.setEndTime(dataResByTime.getImportEndTime());
            //把离职员工工号赋值给approveGetDto进行查询
            approveGetDto.setQuitPernr(dataResByTime.getQuitPernr());
            //赋值导入人工号
            approveGetDto.setImportPernr(dataResByTime.getImportPernr());
            //赋值人员范围
            approveGetDto.setPersonScope(dataResByTime.getPersonScope());
            //调用mapper方法，获得ImportTime
            List<ImportData> importDataList = importDataMapper.getImoprtDataByTime(approveGetDto);
            for (ImportData importData : importDataList) {
                //通过ImportTime查询监控报表数据
                ApproveDataRes approveDataRes = approveMapper.getApproveDataRes((df.format(importData.getImportTime())),
                        importData.getQuitPernr(),
                        importData.getPersonScope(),
                        importData.getOriginatorPernr());
                if (approveDataRes.getPersonScope().equals("门店")) {
                    //通过前端传过来的离职员工工号查询店编
                    String storeName = sapUserInfoMapper.getDepartmentByPernr(approveDataRes.getQuitPernr());
                    //通过店编查询离职人员所属分部地区
                    SAPStoreHead sapStoreHeadByStoreId = sapStoreHeadMapper.getSAPStoreHeadByStoreId(storeName);
                    //查询出来的店编写进监控报表中
                    approveDataRes.setDivision(sapStoreHeadByStoreId.getManageArea());
                    List<String> approveResult = new ArrayList<>();
                    if (StringUtils.isNotBlank(approveDataRes.getDirectApproveResult())){
                        if (approveDataRes.getDirectApproveResult().equals("待审核") || approveDataRes.getDirectApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getDirectApproveResult());
                        }
                    }
                    if (StringUtils.isNotBlank(approveDataRes.getAreaApproveResult())){
                        if (approveDataRes.getAreaApproveResult().equals("待审核") || approveDataRes.getAreaApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getAreaApproveResult());
                        }
                    }
                    if(StringUtils.isNotBlank(approveDataRes.getRegionalApproveResult())){
                        if (approveDataRes.getRegionalApproveResult().equals("待审核") || approveDataRes.getRegionalApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getRegionalApproveResult());
                        }
                    }
                    if(StringUtils.isNotBlank(approveDataRes.getLoanApproveResult())){
                        if (approveDataRes.getLoanApproveResult().equals("待审核") || approveDataRes.getLoanApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getLoanApproveResult());
                        }
                    }
                    if (StringUtils.isNotBlank(approveDataRes.getQualityApproveResult())){
                        if (approveDataRes.getQualityApproveResult().equals("待审核") || approveDataRes.getQualityApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getQualityApproveResult());
                        }
                    }
                    if (StringUtils.isNotBlank(approveDataRes.getCardApproveResult())){
                        if (approveDataRes.getCardApproveResult().equals("待审核") || approveDataRes.getCardApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getCardApproveResult());
                        }
                    }
                    if (approveResult.size() > 0){
                        approveDataRes.setApproveStatus("审批中");
                    }else {
                        approveDataRes.setApproveStatus("审批完成");
                    }
                    //查询离职原因
                    String leaveReson = sapUserInfoMapper.getLeaveResonByQuitPernr(approveDataRes.getQuitPernr());
                    //把离职原因返回给前端
                    approveDataRes.setLeaveReson(leaveReson);
                    approveDataResList.add(approveDataRes);
                }
                if (approveDataRes.getPersonScope().equals("职能")) {
                    List<String> approveResult = new ArrayList<>();
                    if (StringUtils.isNotBlank(approveDataRes.getDirectApproveResult())){
                        if (approveDataRes.getDirectApproveResult().equals("待审核") || approveDataRes.getDirectApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getDirectApproveResult());
                        }
                    }
                    if (StringUtils.isNotBlank(approveDataRes.getAreaApproveResult())){
                        if (approveDataRes.getAreaApproveResult().equals("待审核") || approveDataRes.getAreaApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getAreaApproveResult());
                        }
                    }
                    if(StringUtils.isNotBlank(approveDataRes.getRegionalApproveResult())){
                        if (approveDataRes.getRegionalApproveResult().equals("待审核") || approveDataRes.getRegionalApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getRegionalApproveResult());
                        }
                    }
                    if(StringUtils.isNotBlank(approveDataRes.getLoanApproveResult())){
                        if (approveDataRes.getLoanApproveResult().equals("待审核") || approveDataRes.getLoanApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getLoanApproveResult());
                        }
                    }
                    if (StringUtils.isNotBlank(approveDataRes.getQualityApproveResult())){
                        if (approveDataRes.getQualityApproveResult().equals("待审核") || approveDataRes.getQualityApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getQualityApproveResult());
                        }
                    }
                    if (StringUtils.isNotBlank(approveDataRes.getCardApproveResult())){
                        if (approveDataRes.getCardApproveResult().equals("待审核") || approveDataRes.getCardApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getCardApproveResult());
                        }
                    }
                    if (approveResult.size() > 0){
                        approveDataRes.setApproveStatus("审批中");
                    }else {
                        approveDataRes.setApproveStatus("审批完成");
                    }
                    //查询离职原因
                    String leaveReson = sapUserInfoMapper.getLeaveResonByQuitPernr(approveDataRes.getQuitPernr());
                    //把离职原因返回给前端
                    approveDataRes.setLeaveReson(leaveReson);
                    approveDataResList.add(approveDataRes);
                }
            }
        } else {
            //创建ApproveGetDto对象
            ApproveGetDto approveGetDto = new ApproveGetDto();
            //把ImportDataGetDto赋值给ApproveGetDto
            approveGetDto.setStartTime(dataResByTime.getImportStartTime());
            approveGetDto.setEndTime(dataResByTime.getImportEndTime());
            //把离职员工工号赋值给approveGetDto进行查询
            approveGetDto.setQuitPernr(dataResByTime.getQuitPernr());
            //赋值导入人工号
            approveGetDto.setImportPernr(dataResByTime.getImportPernr());
            //赋值人员范围
            approveGetDto.setPersonScope(dataResByTime.getPersonScope());
            //调用mapper方法，获得ImportTime
            List<ImportData> importDataList = importDataMapper.getImoprtDataByTime(approveGetDto);
            for (ImportData importData : importDataList) {
                //通过ImportTime查询监控报表数据
                ApproveDataRes approveDataRes = approveMapper.getApproveDataRes((df.format(importData.getImportTime())),
                        importData.getQuitPernr(),
                        importData.getPersonScope(),
                        importData.getOriginatorPernr());
                if (approveDataRes.getPersonScope().equals("门店")) {
                    //通过前端传过来的离职员工工号查询店编
                    String storeName = sapUserInfoMapper.getDepartmentByPernr(approveDataRes.getQuitPernr());
                    //通过店编查询离职人员所属分部地区
                    SAPStoreHead sapStoreHeadByStoreId = sapStoreHeadMapper.getSAPStoreHeadByStoreId(storeName);
                    //查询出来的店编写进监控报表中
                    approveDataRes.setDivision(sapStoreHeadByStoreId.getManageArea());
                    List<String> approveResult = new ArrayList<>();
                    if (StringUtils.isNotBlank(approveDataRes.getDirectApproveResult())){
                        if (approveDataRes.getDirectApproveResult().equals("待审核") || approveDataRes.getDirectApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getDirectApproveResult());
                        }
                    }
                    if (StringUtils.isNotBlank(approveDataRes.getAreaApproveResult())){
                        if (approveDataRes.getAreaApproveResult().equals("待审核") || approveDataRes.getAreaApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getAreaApproveResult());
                        }
                    }
                    if(StringUtils.isNotBlank(approveDataRes.getRegionalApproveResult())){
                        if (approveDataRes.getRegionalApproveResult().equals("待审核") || approveDataRes.getRegionalApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getRegionalApproveResult());
                        }
                    }
                    if(StringUtils.isNotBlank(approveDataRes.getLoanApproveResult())){
                        if (approveDataRes.getLoanApproveResult().equals("待审核") || approveDataRes.getLoanApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getLoanApproveResult());
                        }
                    }
                    if (StringUtils.isNotBlank(approveDataRes.getQualityApproveResult())){
                        if (approveDataRes.getQualityApproveResult().equals("待审核") || approveDataRes.getQualityApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getQualityApproveResult());
                        }
                    }
                    if (StringUtils.isNotBlank(approveDataRes.getCardApproveResult())){
                        if (approveDataRes.getCardApproveResult().equals("待审核") || approveDataRes.getCardApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getCardApproveResult());
                        }
                    }
                    if (approveResult.size() > 0){
                        approveDataRes.setApproveStatus("审批中");
                    }else {
                        approveDataRes.setApproveStatus("审批完成");
                    }
                    //查询离职原因
                    String leaveReson = sapUserInfoMapper.getLeaveResonByQuitPernr(approveDataRes.getQuitPernr());
                    //把离职原因返回给前端
                    approveDataRes.setLeaveReson(leaveReson);
                    approveDataResList.add(approveDataRes);
                }
                if (approveDataRes.getPersonScope().equals("职能")) {
                    List<String> approveResult = new ArrayList<>();
                    if (StringUtils.isNotBlank(approveDataRes.getDirectApproveResult())){
                        if (approveDataRes.getDirectApproveResult().equals("待审核") || approveDataRes.getDirectApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getDirectApproveResult());
                        }
                    }
                    if (StringUtils.isNotBlank(approveDataRes.getAreaApproveResult())){
                        if (approveDataRes.getAreaApproveResult().equals("待审核") || approveDataRes.getAreaApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getAreaApproveResult());
                        }
                    }
                    if(StringUtils.isNotBlank(approveDataRes.getRegionalApproveResult())){
                        if (approveDataRes.getRegionalApproveResult().equals("待审核") || approveDataRes.getRegionalApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getRegionalApproveResult());
                        }
                    }
                    if(StringUtils.isNotBlank(approveDataRes.getLoanApproveResult())){
                        if (approveDataRes.getLoanApproveResult().equals("待审核") || approveDataRes.getLoanApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getLoanApproveResult());
                        }
                    }
                    if (StringUtils.isNotBlank(approveDataRes.getQualityApproveResult())){
                        if (approveDataRes.getQualityApproveResult().equals("待审核") || approveDataRes.getQualityApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getQualityApproveResult());
                        }
                    }
                    if (StringUtils.isNotBlank(approveDataRes.getCardApproveResult())){
                        if (approveDataRes.getCardApproveResult().equals("待审核") || approveDataRes.getCardApproveResult().equals("不通过")){
                            approveResult.add(approveDataRes.getCardApproveResult());
                        }
                    }
                    if (approveResult.size() > 0){
                        approveDataRes.setApproveStatus("审批中");
                    }else {
                        approveDataRes.setApproveStatus("审批完成");
                    }
                    //查询离职原因
                    String leaveReson = sapUserInfoMapper.getLeaveResonByQuitPernr(approveDataRes.getQuitPernr());
                    //把离职原因返回给前端
                    approveDataRes.setLeaveReson(leaveReson);
                    approveDataResList.add(approveDataRes);
                }
            }

        }
        return approveDataResList;
    }

    /**
     * 记录删除操作
     * @param deleteImportDataLog
     * @return
     */
    @Override
    public int insertDeleteLog(DeleteImportDataLog deleteImportDataLog) {
        return deleteImportDataLogMapper.insertDeleteLog(deleteImportDataLog);
    }

}
