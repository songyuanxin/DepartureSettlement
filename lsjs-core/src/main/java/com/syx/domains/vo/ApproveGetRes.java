package com.syx.domains.vo;

import com.syx.utils.annotation.Excel;
import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/6/27
 **/
@Data
public class ApproveGetRes {
    @Excel(name = "分部",cellType = Excel.ColumnType.STRING)
    private String division;

    @Excel(name = "工号",cellType = Excel.ColumnType.STRING)
    private String pernr;

    @Excel(name = "姓名",cellType = Excel.ColumnType.STRING)
    private String name;

    @Excel(name = "离职日期",cellType = Excel.ColumnType.STRING)
    private String leaveDate;

    @Excel(name = "商品赔偿",cellType = Excel.ColumnType.STRING)
    private String goodsMoney;

    @Excel(name = "商品赔偿公司发文号",cellType = Excel.ColumnType.STRING)
    private String goodsMoneyDocument;

    @Excel(name = "短款扣款",cellType = Excel.ColumnType.STRING)
    private String shortMoney;

    @Excel(name = "质量简报扣款",cellType = Excel.ColumnType.STRING)
    private String qualityMoney;

    @Excel(name = "管理责任盘点扣款",cellType = Excel.ColumnType.STRING)
    private String careMoney;

    @Excel(name = "工牌扣款",cellType = Excel.ColumnType.STRING)
    private String cardMoney;

    @Excel(name = "工装扣款",cellType = Excel.ColumnType.STRING)
    private String clothesMoney;

    @Excel(name = "离职原因(离职事件操作原因)",cellType = Excel.ColumnType.STRING)
    private String leaveReson;

    @Excel(name = "旷工发文号(分部导入数据)",cellType = Excel.ColumnType.STRING)
    private String absenteeismDoc;

    @Excel(name = "审批状态", cellType = Excel.ColumnType.STRING)
    private String approveResult;
}
