package com.syx.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * @author 宋远欣
 * @date 2022/5/18
 **/
@Data
public class Deduction {
    //工号
    private String pernr;
    //月份
    private String month;
    //盘点扣款金额
    private BigDecimal money;
}
