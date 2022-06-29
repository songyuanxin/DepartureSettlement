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
    //导入人工号
    private String importPernr;
    //人员范围
    private String personScope;
}
