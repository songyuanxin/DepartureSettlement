package com.syx.domain;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 宋远欣
 * @date 2022/5/18
 **/
@Data
public class ImportData {
    //离职员工工号
    private String quitPernr;
    //人员范围
    private String personScope;
    //直接上级工号
    private String directPernr;
    //所属分部
    private String division;
    //发起人工号
    private String originatorPernr;
    //导入时间
    private Timestamp importTime;
    //旷工发文号
    private String absenteeismDoc;
}
