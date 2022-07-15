package com.syx.domains.dto;

import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/5/20
 **/
@Data
public class RegionalOrAreaApproveDto {
    private Integer approveId;
    private Integer launchId;
    private String quitPernr;
    private String reviewerPernr;
    private String regionPernr;
    private String areaPernr;
    private String auditMind;
}
