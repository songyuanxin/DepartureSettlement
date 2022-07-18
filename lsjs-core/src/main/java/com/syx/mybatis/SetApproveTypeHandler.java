package com.syx.mybatis;

import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.List;

/**
 * @author weiran-lsx
 * @date 2022/7/16 10:52
 */

public class SetApproveTypeHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement p0, int p1,List<String> p2, JdbcType p3) throws SQLException {
        SQLServerDataTable sqlServerDataTable = new SQLServerDataTable();
        sqlServerDataTable.addColumnMetadata("Row", Types.NVARCHAR);
//        sqlServerDataTable.addColumnMetadata("sourceOrderCode", Types.VARCHAR);
//        sqlServerDataTable.addColumnMetadata("cancelReason", Types.NVARCHAR);
//        sqlServerDataTable.addColumnMetadata("cancelTime", Types.TIME);

        if (p2 != null) {
            for (String stringObjectMap : p2) {
                sqlServerDataTable.addRow(stringObjectMap);
            }
        }

        p0.setObject(p1,sqlServerDataTable);
    }

    @Override
    public List<String> getNullableResult(ResultSet p0, String p1) {
        return null;
    }

    @Override
    public List<String> getNullableResult( ResultSet p0,int p1){
        return null;
    }

    @Override
    public  List<String> getNullableResult(CallableStatement p0, int p1){
        return null;
    }
}
