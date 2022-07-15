package com.syx.domains.dto;

import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/5/20
 **/
@Data
public class DirectApproveStoreDto {
    private Integer approveId;
    private Integer launchId;
    private String quitPernr;
    private String reviewerPernr;
    private String work;
    private String dormitory;
    private String goodsRefund;
    private String documentNum;
    private String auditMind;
    private String isShopowner;
    private String regionalPernr;
    private String areaPernr;
}
