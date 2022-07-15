package com.syx.domains.dto;

import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/5/20
 **/
@Data
public class DirectApproveFunctionDto {
    private Integer approveId;
    private Integer launchId;
    private String quitPernr;
    private String reviewerPernr;
    private String work;
    private String fixedAsset;
    private String isa;
    private String dormitory;
    private String goodsRefund;
    private String documentNum;
    private String auditMind;
}
