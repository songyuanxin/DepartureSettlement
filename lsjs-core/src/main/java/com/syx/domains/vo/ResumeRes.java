package com.syx.domains.vo;

import lombok.Data;

import java.sql.Date;

/**
 * @author 宋远欣
 * @date 2022/6/7
 **/
@Data
public class ResumeRes {
    //工号
    private String pernr;
    //开始日期
    private String startDate;
    //结束日趋
    private String endDate;
    //门店编码
    private String storeId;
    //门店名称
    private String storeName;
    //职位
    private String position;
    //职务
    private String post;
}
