package com.syx.domains;

import com.syx.utils.annotation.Excel;
import lombok.Data;

/**
 * @author 宋远欣
 * @date 2022/5/12
 **/
@Data
public class ImportData {

    @Excel(name = "离职员工工号", cellType = Excel.ColumnType.STRING)
    private String pernr;

    @Excel(name = "离职员工姓名", cellType = Excel.ColumnType.STRING)
    private String name;

    @Excel(name = "人员范围", cellType = Excel.ColumnType.STRING)
    private String scope;

    @Excel(name = "直接上级工号", cellType = Excel.ColumnType.STRING)
    private String directPernr;

    @Excel(name = "直接上级姓名", cellType = Excel.ColumnType.STRING)
    private String directName;
}
