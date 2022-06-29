package com.syx.domains.dto;

import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/6/29
 **/
@Data
public class ImportDataGetDto {
    //导入日期
    private String importTime;
    //离职员工工号
    private String quitPernr;
}
