package com.syx.domains.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/6/9
 **/
@Data
public class QueryApproveStatusRes {
    /**
     * 审核状态：1、未发起；2、审批中；3、审批完成
     */
    private int status;
    private String name;
    private List<QueryApproveRes> queryApproveRes;
}
