package com.syx.service.directly.impl;

import com.syx.domain.Approve;
import com.syx.domains.dto.ImportDataDto;
import com.syx.domains.vo.SendMsgRes;
import com.syx.service.directly.IFuncterProcessService;
import com.syx.service.impl.LsjsServiceImpl;
import com.syx.service.impl.WeChatServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
@Service
public class FuncterProcessServiceImpl implements IFuncterProcessService {

    @Autowired
    private WeChatServiceImpl weChatServiceImpl;

    @Autowired
    private LsjsServiceImpl lsjsServiceImpl;

    /**
     * 发送至直接上级企业微信中审核
     * @param
     * @return
     */
    @Override
    public SendMsgRes sendDirectly(Map<String,List<ImportDataDto>> functionDirectMap, String isReturn) {
        SendMsgRes sendMsgRes = new SendMsgRes();
        //一、对按照门店or职能进行分组后的数据遍历
        for(String direct:functionDirectMap.keySet()){
            List<ImportDataDto> importDataDtos = functionDirectMap.get(direct);
            Iterator<ImportDataDto> data = functionDirectMap.get(direct).iterator();
            if (data.hasNext()){
                String quitpernr = "";//存放属于同一个直接上级的离职员工工号+姓名，为发送消息时的content组装数据
                String pernr = "";
                for (ImportDataDto quitImportDataDto : importDataDtos){
                    quitpernr = quitpernr + quitImportDataDto.getPernr() + quitImportDataDto.getName() + "、";
                    pernr = quitImportDataDto.getPernr();
                }

                int insertApproveResult = 0;
                int updateApproveResult = 0;
                if (isReturn.equals("1")) {
                    //若退回标识是区域经理，则需要删除区域经理审核记录并修改直接上级审核记录
                    lsjsServiceImpl.deleteApproveByPernr(pernr, "6");
                    updateApproveResult = updateApprove(importDataDtos);
                }else if(isReturn.equals("2")){
                    //若退回标识是地区经理，则需要删除区域经理和地区经理的审核记录并修改直接上级审核记录
                    lsjsServiceImpl.deleteApproveByPernr(pernr, "6");
                    lsjsServiceImpl.deleteApproveByPernr(pernr, "7");
                    updateApproveResult = updateApprove(importDataDtos);
                }else if(isReturn.equals("0")){
                    //若不属于退回则属于发起流程时发送至直接上级
                    insertApproveResult = insertApprove(importDataDtos, direct.substring(0,6));
                }
                if (insertApproveResult == 0 && updateApproveResult == 0){
                    sendMsgRes.setErrcode(1);
                    sendMsgRes.setErrmsg("写入数据库失败");
                    return sendMsgRes;
                }

                String splicing = "离司结算审核提醒:\n您收到了"+ quitpernr.substring(0, quitpernr.length()-1) + "<a href=\"http://localhost:8080/approve/#/pages/index/directly/functionApproval?pernr="+direct.substring(0,6) + "\">"+"审批入口</a>";
//                String splicing = "离司结算审核提醒:\n您收到了"+ quitpernr.substring(0, quitpernr.length()-1) + "<a href=\"http://hrfico.jzj.cn:19004/approve/#/pages/index/directly/functionApproval?pernr="+direct.substring(0,6) + "\">"+"审批入口</a>";
                sendMsgRes = weChatServiceImpl.sendMsg(direct.substring(0,6), splicing);
                //判断提醒消息是否发送成功，若发送成功则写入审核表以及审核记录表
                if(sendMsgRes.getErrcode() != 0){
                    sendMsgRes.setErrcode(1);
                    sendMsgRes.setErrmsg("发生至直接上级企业微信失败");
                    return sendMsgRes;
                }
            }
        }
        return sendMsgRes;
    }

    /**
     * 区域经理或地区经理退回时修改直接上级审核记录为待审核
     * @param importDataDtos
     * @return
     */
    private int updateApprove(List<ImportDataDto> importDataDtos) {
        int i =0;
        for (ImportDataDto quitImportDataDto:importDataDtos){
            LocalDateTime now = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(now);

            Approve approve = new Approve();
            approve.setQuitPernr(quitImportDataDto.getPernr());

            approve.setApproveContent(1);
            approve.setApproveResult(1);
            approve.setApproveResultDesc("待审核");
            approve.setSendTime(timestamp);

            i = lsjsServiceImpl.updateApprove(approve) + i;
        }
        return i;
    }

    /**
     * 将发送成功的离司结算申请写入审核表
     * @param importDatumDtos
     * @param directPernr
     * @return
     */
    public int insertApprove(List<ImportDataDto> importDatumDtos, String directPernr) {
        int i =0;
        for (ImportDataDto quitImportDataDto : importDatumDtos){
            //根据员工工号查询员工基本信息
            LocalDateTime now = LocalDateTime.now();
            Timestamp timestamp = Timestamp.valueOf(now);

            Approve approve = new Approve();
            approve.setQuitPernr(quitImportDataDto.getPernr());

            approve.setApproveContent(1);
            approve.setApproveContentDesc("直接上级审核");
            approve.setApproveResult(1);
            approve.setApproveResultDesc("待审核");
            approve.setReviewerPernr(directPernr);
            approve.setSendTime(timestamp);

            i = lsjsServiceImpl.insertApprove(approve) + i;
        }
        return 1;
    }

}
