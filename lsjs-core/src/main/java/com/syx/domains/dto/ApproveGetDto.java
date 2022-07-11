package com.syx.domains.dto;

import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/6/27
 **/
@Data
public class ApproveGetDto {
    private String startTime;
    private String endTime;
    private Integer approveStatus;
    private String division;
    private String personScope;
    private String importPernr;
    private String quitPernr;
}
