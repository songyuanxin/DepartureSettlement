package com.syx.domain;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 宋远欣
 * @date 2022/5/18
 **/
@Data
public class ApproveLog {
    //审核ID
    private int id;
    //离职员工工号
    private String quitPernr;
    //审核内容(1:直接上级审核；2:借款、短款审核；3:质量简报扣款审核；4:工牌、工装审核；5:管理责任盘点扣款审核；6:区域经理审核；7:地区经理审核)
    private int approveContent;
    //审核内容描述
    private String approveContentDesc;
    //审核人工号(操作人工号)
    private String reviewerPernr;
    //审核时间(操作时间)
    private Timestamp approveTime;
    //审核结果(1:通过；2:待办；3:退回)
    private int approveResult;
    //审核结果描述
    private String approveResultDesc;
    //审核意见
    private String approveOpinion;
}
