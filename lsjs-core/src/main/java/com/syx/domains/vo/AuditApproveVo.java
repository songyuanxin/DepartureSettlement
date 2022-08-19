package com.syx.domains.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author 宋远欣
 * @date 2022/5/18
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditApproveVo {
    //发起ID
    private Integer launchId;
    //人员范围
    private String personScope;
    //离职员工工号
    private String quitPernr;
    //离职员工姓名
    private String name;
    //审核结果(1:直接上级审核；2:借款、短款审核；3:质量简报扣款审核；4:工牌、工装审核；5:管理责任盘点扣款审核；6:区域经理审核；7:地区经理审核)
    private int approveContent;
    //审核结果描述
    private String approveContentDesc;
    //审核人工号
    private String reviewerPernr;
    //发送时间
    private Timestamp sendTime;
    //审核时间
    private Timestamp approveTime;
    //审核结果(1:通过；2:待办；3:退回)
    private int approveResult;
    //审核结果描述
    private String approveResultDesc;
    //审核意见
    private String approveOpinion;
    //工作交接完成情况
    private String work;
    //固定资产交接完成情况
    private String fixedAsset;
    //信息系统权限交接完成情况
    private String isa;
    //宿舍交接完成情况
    private String dormitory;
    //商品赔偿金额
    private BigDecimal goodsRefund;
    //公司发文文件号
    private String documentNum;
    //借款金额
    private BigDecimal loanMoney;
    //短款金额
    private BigDecimal shortMoney;
    //短款出现门店店编
    private String shortStoreId;
    //质量简报扣款金额
    private BigDecimal qualityMoney;
    //管理责任盘点扣款金额
    private BigDecimal careMoney;
    //管理责任盘点扣款发问号
    private String careDocumentNum;
    //工牌扣款金额
    private BigDecimal cardMoney;
    //工装扣款金额
    private BigDecimal clothesMoney;
    //区域经理工号
    private String regionPernr;
    //地区经理工号
    private String areaPernr;
}
