package com.syx.domain;

import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/5/18
 **/
@Data
public class ReviewerInfo {
    //审核人工号
    private String approvePernr;
    //审核人姓名
    private String approveName;
    //审核人联系电话
    private String approvePhone;
}
