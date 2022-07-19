package com.syx.domains.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/6/29
 **/
@Data
@JsonInclude(value= JsonInclude.Include.NON_NULL)
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

    //直接上级审核人工号
    private String directReviewerPernr;
    //直接上级审核结果
    private String directApproveResult;
    //直接上级接收时间
    private String directSendTime;
    //直接上级审核时间
    private String directApproveTime;

    //地区经理审核人工号
    private String areaReviewerPernr;
    //地区经理审核结果
    private String areaApproveResult;
    //地区经理接收时间
    private String areaSendTime;
    //地区经理审核时间
    private String areaApproveTime;

    //区域经理审核人工号
    private String regionalReviewerPernr;
    //区域经理审核结果
    private String regionalApproveResult;
    //区域经理接收时间
    private String regionalSendTime;
    //区域经理审核时间
    private String regionalApproveTime;

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

    //审核状态
    private String approveStatus;

    //离职原因
    private String leaveReson;

    //店编,不返回给前端
    @JSONField(serialize = false)
    private String storeNameId;

}
