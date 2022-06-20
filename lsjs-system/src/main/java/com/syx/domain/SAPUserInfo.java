package com.syx.domain;

import lombok.Data;

import java.sql.Date;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
@Data
public class SAPUserInfo {
    //工号
    private String pernr;
    //姓名
    private String name;
    //身份证号
    private String idNumber;
    //门店编码/所属部门
    private String department;
    //职务ID
    private String postId;
    //职务
    private String post;
    //职位ID
    private String positionId;
    //职位
    private String position;
    //入司日期
    private Date joinedDate;
    //转正日期
    private Date confirDate;
    //离职日期
    private Date leaveDate;
    //联系电话
    private String phone;
    //职能体系
    private String dutySystem;
    //三级组织ID
    private String fourOrganizationId;
    //三级组织
    private String fourOrganization;
}
