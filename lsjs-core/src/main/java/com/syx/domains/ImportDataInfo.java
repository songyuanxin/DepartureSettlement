package com.syx.domains;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author 宋远欣
 * @date 2022/7/14
 **/
@Data
public class ImportDataInfo {
    //发起ID
    private Integer launchId;
    //离职员工工号
    private String quitPernr;
    //离职员工姓名
    private String quitName;
    //人员范围
    private String personScope;
    //直接上级工号
    private String directPernr;
    //直接上级姓名
    private String directName;
    //所属分部
    private String division;
    //发起人工号
    private String originatorPernr;
    //导入时间
    private Timestamp importTime;
    //旷工发文号
    private String absenteeismDoc;
    //是否属于再入职
    private Integer reEntry;
}
