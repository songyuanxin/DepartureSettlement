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

    @Autowired
    private LoanBalanceMapper loanBalanceMapper;

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
    public List<String> getAuditQuitPernr(String reviewerPernr, Integer approveContent) {
        return approveMapper.getAuditQuitPernr(reviewerPernr, approveContent);
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
            AuditUserRes auditUserRes = sapUserInfoMapper.getUserInfoByPernrList(quitPernr, approve.getLaunchId());
            if (auditUserRes.getPersg().equals("A")){
                auditUserRes.setPersg("正式");
            }else if (auditUserRes.getPersg().equals("B")){
                auditUserRes.setPersg("试用");
            }else if (auditUserRes.getPersg().equals("C")){
                auditUserRes.setPersg("兼职");
            }else if (auditUserRes.getPersg().equals("D")){
                auditUserRes.setPersg("实习生");
            }else if (auditUserRes.getPersg().equals("E")){
                auditUserRes.setPersg("退休");
            }else if (auditUserRes.getPersg().equals("F")){
                auditUserRes.setPersg("退休返聘");
            }else if (auditUserRes.getPersg().equals("G")){
                auditUserRes.setPersg("不在岗");
            }else if (auditUserRes.getPersg().equals("I")){
                auditUserRes.setPersg("劳务工");
            }
            //根据离职员工工号查询盘点扣款
            List<Deduction> deductionByPernr = deductionMapper.getDeductionByPernr(quitPernr);
            //根据离职员工工号查询任职履历
            List<ResumeRes> resumeByPernr = resumeMapper.getResumeByPernr(quitPernr);

            auditUserRes.setLaunchId(approve.getLaunchId());
            auditUserRes.setDeductions(deductionByPernr);
            auditUserRes.setResumes(resumeByPernr);

            if (auditUserRes.getPersonScope().equals("门店")){
                auditUserRes.setDivision(sapStoreHeadMapper.getSAPStoreNameByStoreId(auditUserRes.getDepartment()));
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
                Deduction deductionByPernr = deductionMapper.getDeductionByPernrAndMonth(deduction.getPernr(),deduction.getMonth());
                int insertResult = 0;
                if (deductionByPernr == null) {
                    insertResult = deductionMapper.insertDeduction(deduction);
                    i = insertResult + i;
                } else if (deductionByPernr != null) {
                    int updateResult = deductionMapper.updateDeduction(deduction);
                    i = updateResult + i;
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
                ResumeRes resumeByPernr = resumeMapper.getResumeByPernrAndStartDate(resume.getPernr(), resume.getStartDate());
                int insertResult = 0;
                if (resumeByPernr == null) {
                    insertResult = resumeMapper.insertResume(resume);
                    i = insertResult + i;
                } else if (resumeByPernr != null) {
                    int updateResult = resumeMapper.updateResume(resume);
                    i = updateResult + i;
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
        List<String> storeNameList = new ArrayList<>();//创建存放店编的集合
        List<String> shopStoreNameList = new ArrayList<>();//创建属于门店范围员工的店编的集合
        for (ImportData importData:importDataByTime){
            launchs.add("LaunchID" + "||" + importData.getLaunchId().toString() + "||");
            if (importData.getPersonScope().equals("门店")){
                storeQuitPernrList.add(importData.getQuitPernr());
            }
        }
        List<ApproveGetRes> exportApproveData = approveMapper.getApproveDataByLQ("exportApproveData", launchs);
        for (ApproveGetRes approveGetRes:exportApproveData){
            //若属于门店员工则将店编拼接作为表入参传给存储过程
            if (approveGetRes.getPersonScope().equals("门店")){
                storeNameList.add("StoreName" + "||" + approveGetRes.getStoreName() + "||");
                shopStoreNameList.add(approveGetRes.getStoreName());
            }
        }
        //调用存储过程获得管理地区的List集合
        List<SAPStoreHead> sapStoreHeadList = sapStoreHeadMapper.getSAPStoreHeadByStoreIdAndSqlserver("管理地区",storeNameList);
        //遍历查询出来的审核数据数组
        for (ApproveGetRes approveGetRes:exportApproveData){
            //若存储人员范围为门店的店编list中包含该离职员工所属门店店编则遍历门店主数据为门店员工所属分部赋值
            if (shopStoreNameList.contains(approveGetRes.getStoreName())){
                for (SAPStoreHead sapStoreHead:sapStoreHeadList){
                    if (sapStoreHead.getStoreId().equals(approveGetRes.getStoreName())){
                        approveGetRes.setDivision(sapStoreHead.getManageArea());
                    }
                }
            }
        }
        return exportApproveData;
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
        //判断如果前端传过来的参数没有结束时间，就获取当前系统时间为结束时间
        if (StringUtils.isBlank(dataResByTime.getImportEndTime()) || dataResByTime.getImportEndTime().equals("")) {
            //获取当前系统时间
            String importEndTime = DateUtils.getDate();
            //把获得的系统时间写进ImportDataGetDto
            dataResByTime.setImportEndTime(importEndTime);
        }
        //创建ApproveGetDto对象
        ApproveGetDto approveGetDto = new ApproveGetDto();
        //把dataResByTime的值赋给approveGetDto
        approveGetDto.setStartTime(dataResByTime.getImportStartTime());
        approveGetDto.setEndTime(dataResByTime.getImportEndTime());
        approveGetDto.setQuitPernr(dataResByTime.getQuitPernr());
        approveGetDto.setImportPernr(dataResByTime.getImportPernr());
        approveGetDto.setPersonScope(dataResByTime.getPersonScope());
        approveGetDto.setDivision(dataResByTime.getDivision());
        //调用Mapper方法获得launchId
        List<ImportData> importDataList = importDataMapper.getImoprtDataByTime(approveGetDto);
        List<String> launchs = new ArrayList<>();//创建存放launchId的集合
        List<String> storeNameList = new ArrayList<>();//创建存放店编的集合
        List<String> shopStoreNameList = new ArrayList<>();//创建属于门店范围员工的店编的集合
        //获得launchId的集合，需要拼接成LaunchId||1||的形式作为表值参数传给存储过程调用
        for (ImportData importData : importDataList) {
            launchs.add("LaunchId" + "||" + importData.getLaunchId().toString() + "||");
        }
        //调用存储过程获得监控报表的List集合
        List<ApproveDataRes> approveDataResList = approveMapper.getApproveDataResBySqlServer("监控报表", launchs);

        //获得店编的集合，需要拼接成StoreName||1||的形式作为表值参数传给存储过程调用
        for (ApproveDataRes approveDataRes : approveDataResList) {
            storeNameList.add("StoreName" + "||" + approveDataRes.getStoreNameId() + "||");
            //判断人员范围，把人员范围属于门店的对应离职店编放进集合，方便查询
            if(approveDataRes.getPersonScope().equals("门店")){
                shopStoreNameList.add(approveDataRes.getStoreNameId());
            }
        }
        //调用存储过程获得管理地区的List集合
        List<SAPStoreHead> sapStoreHeadList = sapStoreHeadMapper.getSAPStoreHeadByStoreIdAndSqlserver("管理地区",storeNameList);
        for (ApproveDataRes approveDataRes : approveDataResList) {
            //判断包含关系
            if (shopStoreNameList.contains(approveDataRes.getStoreNameId())){
                for (SAPStoreHead sapStoreHead : sapStoreHeadList) {
                    //如果店编相等且包含
                    if(sapStoreHead.getStoreId().equals(approveDataRes.getStoreNameId())){
                        //替换人员所属分部范围
                        approveDataRes.setDivision(sapStoreHead.getManageArea());
                    }
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

    /**
     * 获取离职员工的借款余额
     *
     * @param quitPernrList
     * @return
     * @throws Exception
     */
    @Override
    public int getJKYE(List<String> quitPernrList) throws Exception {
        String postUrl = "http://s4hanadb01.jzj.cn:8002/sap/bc/srt/rfc/sap/zfi_interface_01/800/zfi_interface_01/zfi_interface_01";
        String Username = "S4CONN";
        String Password = "SAP_po1234**";
        String soapXml = getJKYEXml(quitPernrList);
        String result = HttpClientUtils.postSoapOne(soapXml, postUrl, Username, Password);
        List<ItJKYE> jkyeList = new ArrayList<>();

        List<Map<String, String>> maps = parseSoap(result);

        maps.stream().forEach(map -> {
            ItJKYE itJKYE = new ItJKYE();
            itJKYE.setPernr(map.get("PERNR"));
            itJKYE.setZjkye(map.get("ZJKYE"));
            jkyeList.add(itJKYE);
        });

        Map<String, List<ItJKYE>> itJKYEMap = jkyeList.stream().filter(item -> item.getPernr() != null)
                .collect(Collectors.groupingBy(item -> item.getPernr()));

        int i = 0;
        if (itJKYEMap.size() > 0) {
            int insertLoanBalance = insertLoanBalance(itJKYEMap);
            if (itJKYEMap.size() > 0 && insertLoanBalance <= 0) {
                return -1;
            }
            i = i + insertLoanBalance;
        }
        return i;
    }


    /**
     * 拼接请求SAP WebService的请求报文（借款余额）
     *
     * @param pernrList
     * @return
     */
    private String getJKYEXml(List<String> pernrList) {
        StringBuilder pernr = new StringBuilder();
        for (String quitPernr : pernrList) {
            pernr.append("<item>" + "<LIFNR>" + "00" + quitPernr + "</LIFNR>" + "</item>");
        }
        String soapXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:sap-com:document:sap:rfc:functions\">"
                + "<soapenv:Header/>"
                + "<soapenv:Body>"
                + "<urn:ZFI_INTERFACE_01>"
                + "<INPUT>"
                + pernr
                + "</INPUT>"
                + "</urn:ZFI_INTERFACE_01>"
                + "</soapenv:Body>"
                + "</soapenv:Envelope>";
        return soapXml;
    }

    /**
     * 将借款余额插入数据库
     *
     * @param itJKYEMap
     * @return
     */
    @Override
    public int insertLoanBalance(Map<String, List<ItJKYE>> itJKYEMap) {
        int i = 0;
        List<ItJKYE> itJKYEList = new ArrayList<>();
        for (String pernr : itJKYEMap.keySet()) {
            List<ItJKYE> itJKYEList1 = itJKYEMap.get(pernr);
            for (ItJKYE itJKYE:itJKYEList1){
                itJKYEList.add(itJKYE);
            }
        }
        for (ItJKYE itJKYE : itJKYEList) {
            LoanBalance loanBalance = new LoanBalance();
            String itJKYEPernr = itJKYE.getPernr().substring(2);
            loanBalance.setPernr(itJKYEPernr);
            loanBalance.setMoney(new BigDecimal(itJKYE.getZjkye()).setScale(2, BigDecimal.ROUND_HALF_UP));
            LoanBalance loanBalance1 = loanBalanceMapper.getLoanBalanceByPernr(loanBalance.getPernr());
            int insertResult = 0;
            //如果借款余额表中存在工号相同的数据
            if (loanBalance1 != null) {
                insertResult = loanBalanceMapper.updateLoanBalance(loanBalance);
                i = insertResult + i;
            } else {
                insertResult = loanBalanceMapper.insertLoanBalance(loanBalance);
                i = insertResult + i;
            }
        }

        return i;
    }

    /**
     * 根据离职员工工号删除借款余额
     *
     * @param dataList
     * @return
     */
    @Override
    public int deleteJKYE(List<ImportDataDto> dataList) {
        int i = 0;
        for (ImportDataDto importDataDto : dataList) {
            int deleteLoanBalance = loanBalanceMapper.deleteLoanBalance(importDataDto.getPernr());
            i = deleteLoanBalance + i;
        }
        return 0;
    }
}
