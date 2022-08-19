package com.syx.domain;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 宋远欣
 * @date 2022/8/17
 **/
@Data
public class LsjsAuditSendLog {
    //主键，编号
    private Integer id;
    //离职员工工号
    private String quitPernr;
    //接收时间
    private Timestamp receivingTime;
    //审核人工号
    private String reviewerPernr;
    //审核内容
    private Integer approveContent;
    //发送标识
    private Integer sendIdent;
    //发送时间
    private Timestamp sendTime;
}
