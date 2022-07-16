package com.syx.client.mybatis;

import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import lombok.val;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.format.SignStyle;
import java.util.List;
import java.util.Map;

/**
 * @author weiran-lsx
 * @date 2022/7/16 10:52
 */

public class SetApproveTypeHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement p0, int p1,List<String> p2, JdbcType p3) throws SQLException {
        SQLServerDataTable sqlServerDataTable = new SQLServerDataTable();
        sqlServerDataTable.addColumnMetadata("Row", Types.VARCHAR);
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
