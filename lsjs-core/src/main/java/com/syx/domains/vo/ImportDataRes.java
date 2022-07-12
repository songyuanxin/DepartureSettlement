package com.syx.domains.vo;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author weiran-lsx
 * @date 2022/7/11 15:52
 */
@Data
public class ImportDataRes {
    //离职员工工号
    private String quitPernr;
    //人员范围
    private String personScope;
    //直接上级工号
    private String directPernr;
    //直接上级姓名
    private String directName;
    //区域经理工号
    private String areaPernr;
    //区域经理姓名
    private String areaName;
    //地区经理工号
    private String regionalPernr;
    //地区经理姓名
    private String regionalName;
    //所属分部
    private String division;
    //发起人工号
    private String originatorPernr;
    //导入时间
    private Timestamp importTime;
    //旷工发文号
    private String absenteeismDoc;
}
