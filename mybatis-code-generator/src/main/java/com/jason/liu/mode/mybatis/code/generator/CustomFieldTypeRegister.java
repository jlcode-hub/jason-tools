package com.jason.liu.mode.mybatis.code.generator;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: meng.liu
 * @date: 2021/2/25
 * TODO:
 */
public abstract class CustomFieldTypeRegister {

    private Map<String, Class> enumColumn;

    public CustomFieldTypeRegister() {
        enumColumn = new HashMap<>(32);
    }

    /**
     * 获取注册的字段
     *
     * @return
     */
    public Map<String, Class> types() {
        this.registers();
        return this.enumColumn;
    }

    /**
     * 业务自己实现注册的表，需要手动调用{@link TableTypes#register()}
     */
    protected abstract void registers();


    /**
     * 注册
     *
     * @param tableEnums
     */
    private final void registerEnum(TableTypes tableEnums) {
        if (null == tableEnums || tableEnums.getFieldEnums().isEmpty()) {
            return;
        }
        for (FieldType fieldEnum : tableEnums.getFieldEnums()) {
            this.enumColumn.put(tableEnums.getTableName() + "." + fieldEnum.getFieldName(), fieldEnum.getTypeClass());
        }
    }

    public TableTypes of(String tableName) {
        return new TableTypes(tableName, this);
    }


    @Getter
    public static class TableTypes {

        private CustomFieldTypeRegister enumRegister;

        private String tableName;

        private List<FieldType> fieldEnums;

        public TableTypes(String tableName, CustomFieldTypeRegister enumRegister) {
            this.enumRegister = enumRegister;
            this.tableName = tableName;
            this.fieldEnums = new ArrayList<>();
        }

        public TableTypes field(String fieldName, Class typeClass) {
            this.fieldEnums.add(FieldType.of(fieldName, typeClass));
            return this;
        }

        public void register() {
            this.enumRegister.registerEnum(this);
        }
    }


    @Getter
    public static class FieldType {
        private Class typeClass;

        private String fieldName;

        public FieldType(String fieldName, Class typeClass) {
            this.fieldName = fieldName;
            this.typeClass = typeClass;
        }

        static FieldType of(String fieldName, Class typeClass) {
            return new FieldType(fieldName, typeClass);
        }
    }

}
