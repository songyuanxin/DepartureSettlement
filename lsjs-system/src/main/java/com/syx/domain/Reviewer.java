package com.syx.domain;

import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/5/18
 **/
@Data
public class Reviewer {
    //主键
    private int id;
    //所属分部
    private int parcel;
    //所属分部描述
    private String parcelDesc;
    //所属地区
    private String region;
    //人员范围
    private String personScope;
    //借款审核人工号
    private String loanPernr;
    //短款审核人工号
    private String shortPernr;
    //质量简报扣款审核人工号
    private String qualityPernr;
    //工牌、工装审核人工号
    private String toolPernr;
    //管理责任盘点扣款审核人工号
    private String carePernr;
}
