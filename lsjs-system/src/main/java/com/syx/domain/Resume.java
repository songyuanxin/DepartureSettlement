package com.syx.domain;

import lombok.Data;

import java.sql.Date;

/**
 * @author 宋远欣
 * @date 2022/5/18
 **/
@Data
public class Resume {
    //工号
    private String pernr;
    //开始日期
    private Date startDate;
    //结束日趋
    private Date endDate;
    //门店编码
    private String storeId;
    //门店名称
    private String storeName;
    //职位
    private String position;
    //职务
    private String post;
}
