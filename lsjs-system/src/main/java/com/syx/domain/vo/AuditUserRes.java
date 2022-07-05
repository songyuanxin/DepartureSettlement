package com.syx.domain.vo;

import com.syx.domain.Deduction;
import com.syx.domain.Resume;
import com.syx.domains.vo.ResumeRes;
import lombok.Data;

import java.util.List;

/**
 * @author 宋远欣
 * @date 2022/5/27
 **/
@Data
public class AuditUserRes {
    private String pernr;
    private String userName;
    private String sex;
    private String position;
    private String department;
    private String joinedDate;
    private String confirDate;
    private String leaveDate;
    private String phone;
    private String division;
    private String work;
    private String fixedAsset;
    private String isa;
    private String dormitory;
    private String goodsRefund;
    private List<Deduction> deductions;
    private List<ResumeRes> resumes;
    private String documentNum;
    private String regionalPernr;
    private String areaPernr;
    private String isShopowner;
    private String auditMind;
    private String quitPernr;
    private String reviewerPernr;
    private String loanMoney;
    private String shortMoney;
    private String storeId;
    private String storeName;
    private String qualityMoney;
    private String careMoney;
    private String careDocumentNum;
    private String cardMoney;
    private String clothesMoney;
}
