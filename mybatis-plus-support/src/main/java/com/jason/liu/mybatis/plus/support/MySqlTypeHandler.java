package com.jason.liu.mybatis.plus.support;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: meng.liu
 * @date: 2021/2/25
 * TODO: Mybatis枚举类型转换器
 */
public class MySqlTypeHandler extends BaseTypeHandler<MySqlType> {

    private Map<Integer, MySqlType> enumMap = new HashMap<>();

    private Class<MySqlType> type;

    public MySqlTypeHandler() {

    }

    public MySqlTypeHandler(Class<MySqlType> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        for (MySqlType enumConstant : type.getEnumConstants()) {
            enumMap.put(enumConstant.getCode(), enumConstant);
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MySqlType parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setInt(i, -1);
        } else {
            ps.setInt(i, parameter.getCode());
        }
    }

    @Override
    public MySqlType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return enumMap.get(rs.getInt(columnName));
    }

    @Override
    public MySqlType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return enumMap.get(rs.getInt(columnIndex));
    }

    @Override
    public MySqlType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return enumMap.get(cs.getInt(columnIndex));
    }
}
