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
    //导入开始日期
    private String importStartTime;
    //导入结束日期
    private String importEndTime;
    //所属分部
    private String division;
}
