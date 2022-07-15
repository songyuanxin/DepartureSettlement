package com.syx.domains.dto;

import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/5/26
 **/
@Data
public class FinanceApproveDto {
    private Integer launchId;
    private Integer approveId;
    private String quitPernr;
    private String reviewerPernr;
    private String loanMoney;
    private String shortMoney;
    private String storeId;
    private String storeName;
    private String qualityMoney;
    private String careMoney;
    private String careDocumentNum;
    private String auditMind;
}
