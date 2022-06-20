package com.syx.domains.vo;

import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/5/24
 **/
@Data
public class QueryApproveRes {
    private String auditNode;
    private String sendTime;
    private String approveResult;
    private String approveTime;
    private String approverName;
    private String approverPernr;
    private String approverPhone;
    private String remarks;
}
