package com.syx.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 宋远欣
 * @date 2022/7/20
 **/
@Data
public class LoanBalance {
    //工号
    @TableId
    private String pernr;

    //借款金额
    private BigDecimal money;
}
