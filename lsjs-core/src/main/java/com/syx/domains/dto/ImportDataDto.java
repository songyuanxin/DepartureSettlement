package com.syx.domains.dto;

import com.syx.utils.annotation.Excel;
import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
@Data
public class ImportDataDto {

    @Excel(name = "离职员工工号", cellType = Excel.ColumnType.STRING)
    private String pernr;

    @Excel(name = "离职员工姓名", cellType = Excel.ColumnType.STRING)
    private String name;

    @Excel(name = "人员范围", cellType = Excel.ColumnType.STRING)
    private String personScope;

    @Excel(name = "直接上级工号", cellType = Excel.ColumnType.STRING)
    private String directPernr;

    @Excel(name = "直接上级姓名", cellType = Excel.ColumnType.STRING)
    private String directName;

    @Excel(name = "所属分部", cellType = Excel.ColumnType.STRING)
    private String division;

    @Excel(name = "发起人工号" , cellType = Excel.ColumnType.STRING)
    private String originatorPernr;

    @Excel(name = "旷工发文号", cellType = Excel.ColumnType.STRING)
    private String absenteeismDoc;
}
