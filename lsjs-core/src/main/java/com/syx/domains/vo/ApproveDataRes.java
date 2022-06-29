package com.syx.domains.vo;

import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/6/29
 **/
@Data
public class ApproveDataRes {
    //分部
    private String division;
    //工号
    private String quitPernr;
    //姓名
    private String quitName;
    //离职日期
    private String leaveDate;
    //人员范围
    private String personScope;
    //导入时间
    private String importTime;
    //导入人工号
    private String importPernr;
    //直接上级审核结果
    private String directApproveResult;
    //直接上级接收时间
    private String directSendTime;
    //直接上级审核时间
    private String directApproveTime;
    //区域经理/地区经理审核结果
    private String areaApproveResult;
    //区域经理/地区经理接收时间
    private String areaSendTime;
    //区域经理/第七区经理审核时间
    private String areaApproveTime;
    //财务借款/短款审核结果
    private String loanApproveResult;
    //财务借款/短款接收时间
    private String loanSendTime;
    //财务借款/短款审核时间
    private String loanApproveTime;
    //质量简报扣款审核结果
    private String qualityApproveResult;
    //质量简报扣款接收时间
    private String qualitySendTime;
    //质量简报扣款审核时间
    private String qualityApproveTime;
    //工牌/工装审核结果
    private String cardApproveResult;
    //工牌/工装接收时间
    private String cardSendTime;
    //工牌/工装审核时间
    private String cardApproveTime;
}
