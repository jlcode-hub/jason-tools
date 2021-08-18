package com.jason.liu.mode.mybatis.code.generator;

import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.jason.liu.mybatisplus.generator.AutoGenerator;
import com.jason.liu.mybatisplus.generator.InjectionConfig;
import com.jason.liu.mybatisplus.generator.config.*;
import com.jason.liu.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.jason.liu.mybatisplus.generator.config.po.TableField;
import com.jason.liu.mybatisplus.generator.config.po.TableInfo;
import com.jason.liu.mybatisplus.generator.config.rules.DbColumnType;
import com.jason.liu.mybatisplus.generator.config.rules.IColumnType;
import com.jason.liu.mybatisplus.generator.config.rules.NamingStrategy;
import com.jason.liu.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MybatisPlusCodeGenerator {

    private Configuration configuration;

    private String XML = "/templates/mapper.xml.ftl";

    /**
     * 枚举字段生成映射
     * key: 表名.字段名
     * value: 枚举class
     */
    private Map<String, Class> enumTypeMap;
    /**
     * 自定义type convert
     * 该类型转换会导致枚举类型和时间类型转换失效
     */
    private ITypeConvert typeConvert;

    /**
     * @param propertiesFileName 配置文件路径，配置内容可以参考{code-generator-template.properties}
     */
    public MybatisPlusCodeGenerator(String propertiesFileName) {
        configuration = new Configuration(propertiesFileName);
    }

    public void setEnumTypeMap(Map<String, Class> enumTypeMap) {
        this.enumTypeMap = enumTypeMap;
    }

    public void setTypeConvert(ITypeConvert typeConvert) {
        this.typeConvert = typeConvert;
    }

    public Map<String, Class> getEnumTypeMap() {
        return enumTypeMap;
    }

    public ITypeConvert getTypeConvert() {
        return typeConvert;
    }

    /**
     * 生成代码主入口
     */
    public void generator() {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        this.setGlobalConfig(mpg);
        // 数据源配置
        this.setDb(mpg);
        //设置输出路径
        this.setOutPath(mpg);
        // 配置需要生成的文件
        this.setGenerateFile(mpg);
        //设置策略
        this.setStrategy(mpg);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }


    /**
     * 设置全局配置
     *
     * @param mpg
     */
    private void setGlobalConfig(AutoGenerator mpg) {
        GlobalConfig gc = new GlobalConfig();
        gc.setAuthor(configuration.getString("auth", "Meng.Liu"));
        gc.setOpen(false);
        gc.setEnableCache(configuration.getBoolean("enable.cache", false));
        gc.setBaseResultMap(configuration.getBoolean("base.result.map", false));
        gc.setBaseColumnList(configuration.getBoolean("base.column.list", false));
        gc.setFileOverride(configuration.getBoolean("file.override", false));
        gc.setDataSourceEnabled(configuration.getBoolean("data.source.enabled", false));
        gc.setDataSourceName(configuration.getString("data.source.name"));
        setFileName(gc);
        mpg.setGlobalConfig(gc);
    }

    /**
     * 设置文件名称  %s会替换为表名
     *
     * @param gc
     */
    private void setFileName(GlobalConfig gc) {
        if (null != configuration.getString("file.name.controller")) {
            gc.setControllerName(configuration.getString("file.name.controller"));
        }
        if (null != configuration.getString("file.name.entity")) {
            gc.setEntityName(configuration.getString("file.name.entity"));
        }
        if (null != configuration.getString("file.name.service")) {
            gc.setServiceName(configuration.getString("file.name.service"));
        }
        if (null != configuration.getString("file.name.service-impl")) {
            gc.setServiceImplName(configuration.getString("file.name.service-impl"));
        }
        if (null != configuration.getString("file.name.mapper")) {
            gc.setMapperName(configuration.getString("file.name.mapper"));
        }
    }

    /**
     * 设置数据源
     *
     * @param mpg
     */
    private void setDb(AutoGenerator mpg) {
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://" + configuration.getString("db.host") + "/" + configuration.getString("db.name") + "?useUnicode=true&useSSL=false&characterEncoding=utf8");
        dsc.setDriverName(configuration.getString("db.driver", "com.mysql.jdbc.Driver"));
        dsc.setUsername(configuration.getString("db.username"));
        dsc.setPassword(configuration.getString("db.password"));
        ITypeConvert enumTypeConvert = new MySqlTypeConvert() {

            @Override
            public IColumnType processTypeConvert(GlobalConfig globalConfig, TableInfo tableInfo, TableField tableField) {
                return this.processEnumConvert(globalConfig, tableInfo, tableField);
            }

            @Override
            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                //将数据库中datetime转换成date
                if (fieldType.toLowerCase().contains("datetime")) {
                    return DbColumnType.DATE;
                }
                return super.processTypeConvert(globalConfig, fieldType);
            }

            public IColumnType processEnumConvert(GlobalConfig globalConfig, TableInfo tableInfo, TableField tableField) {
                if (CollectionUtils.isNotEmpty(enumTypeMap)) {
                    String keyName = tableInfo.getName() + StringPool.DOT + tableField.getName();
                    if (enumTypeMap.containsKey(keyName)) {
                        Class typeClass = enumTypeMap.get(keyName);
                        return new IColumnType() {
                            @Override
                            public String getType() {
                                return typeClass.getSimpleName();
                            }

                            @Override
                            public String getPkg() {
                                return typeClass.getTypeName();
                            }
                        };
                    }
                }
                return this.processTypeConvert(globalConfig, tableField.getType());
            }
        };
        if (null == this.typeConvert) {
            dsc.setTypeConvert(enumTypeConvert);
        } else {
            dsc.setTypeConvert(this.typeConvert);
        }
        mpg.setDataSource(dsc);
    }

    /**
     * 设置输出路径
     *
     * @param mpg
     */
    private void setOutPath(AutoGenerator mpg) {
        // 包配置
        PackageConfig pc = new PackageConfig();

        String modulePkg = configuration.getString("module.package", "com.jason.liu");
        pc.setParent("");
        pc.setController(modulePkg + ".controller");
        pc.setMapper(modulePkg + ".mapper");
        pc.setService(modulePkg + ".dao");
        pc.setServiceImpl(modulePkg + ".dao.impl");
        pc.setEntity(modulePkg + ".entity.mysql");

        mpg.setPackageInfo(pc);
    }

    /**
     * 设置需要生成的文件
     *
     * @param mpg
     */
    private void setGenerateFile(AutoGenerator mpg) {
        String projectPath = System.getProperty("user.dir");
        PackageConfig pc = mpg.getPackageInfo();
        String entityModuleName = projectPath + StringPool.SLASH + configuration.getString("module.entity.name");
        String mapperModuleName = projectPath + StringPool.SLASH + configuration.getString("module.mapper.name");
        String controllerModuleName = projectPath + StringPool.SLASH + configuration.getString("module.controller.name");
        // 名称的自定义配置无法直接通过packageInfo来修改，必须要自定义
        List<FileOutConfig> focList = new ArrayList<>();
        TemplateConfig templateConfig = new TemplateConfig();
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = this.getMap();
                if (null == map) {
                    map = new HashMap<>();
                }
                this.setMap(map);
                // to do nothing
                String cacheClass = configuration.getString("cache.class");
                if (StringUtils.isNotBlank(cacheClass)) {
                    map.put("cacheClass", cacheClass);
                }
                Boolean isIdSuper = configuration.getBoolean("super.id", false);
                map.put("isIdSuper", isIdSuper);
            }

            @Override
            public Map<String, Object> prepareObjectMap(Map<String, Object> objectMap) {
                if (!CollectionUtils.isEmpty(this.getMap())) {
                    objectMap.putAll(this.getMap());
                }
                return objectMap;
            }
        };
        mpg.setCfg(cfg);
        if (!configuration.getBoolean("generate.controller", true)) {
            templateConfig.setController(null);
        } else {
            templateConfig.setController(null);
            focList.add(new FileOutConfig("/templates/controller.java.ftl") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    return controllerModuleName + "/src/main/java/" + formatPath(pc.getController())
                            + "/" + tableInfo.getControllerName() + StringPool.DOT_JAVA;
                }
            });
        }
        if (!configuration.getBoolean("generate.entity", true)) {
            templateConfig.setEntity(null);
        } else {
            templateConfig.setEntity(null);
            focList.add(new FileOutConfig("/templates/entity.java.ftl") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    return entityModuleName + "/src/main/java/" + formatPath(pc.getEntity())
                            + "/" + tableInfo.getEntityName() + StringPool.DOT_JAVA;
                }
            });
        }
        if (!configuration.getBoolean("generate.service", true)) {
            templateConfig.setService(null);
        } else {
            templateConfig.setService(null);
            focList.add(new FileOutConfig("/templates/service.java.ftl") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    return mapperModuleName + "/src/main/java/" + formatPath(pc.getService())
                            + "/" + tableInfo.getServiceName() + StringPool.DOT_JAVA;
                }
            });
        }
        if (!configuration.getBoolean("generate.service-impl", true)) {
            templateConfig.setServiceImpl(null);
        } else {
            templateConfig.setServiceImpl(null);
            focList.add(new FileOutConfig("/templates/serviceImpl.java.ftl") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    return mapperModuleName + "/src/main/java/" + formatPath(pc.getServiceImpl())
                            + "/" + tableInfo.getServiceImplName() + StringPool.DOT_JAVA;
                }
            });
        }
        if (!configuration.getBoolean("generate.mapper", true)) {
            templateConfig.setMapper(null);
        } else {
            templateConfig.setMapper(null);
            focList.add(new FileOutConfig("/templates/mapper.java.ftl") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    return mapperModuleName + "/src/main/java/" + formatPath(pc.getMapper())
                            + "/" + tableInfo.getMapperName() + StringPool.DOT_JAVA;
                }
            });
        }
        templateConfig.setXml(null);
        if (configuration.getBoolean("generate.xml", true)) {
            focList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    return mapperModuleName + "/src/main/resources/mappers/" + tableInfo.getXmlName() + StringPool.DOT_XML;
                }
            });
        }
        cfg.setFileOutConfigList(focList);
        mpg.setTemplate(templateConfig);
    }

    /**
     * 设置生成策略
     */
    private void setStrategy(AutoGenerator mpg) {
        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        this.setSuperClass(strategy);
        String superColumns = configuration.getString("super.columns");
        if (StringUtils.isNotBlank(superColumns)) {
            strategy.setSuperEntityColumns(superColumns.split(StringPool.COMMA));
        }
        String tables = configuration.getString("db.tables");
        if (StringUtils.isBlank(tables)) {
            throw new IllegalStateException("db tables is empty.");
        }
        strategy.setInclude(tables.split(StringPool.COMMA));
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setControllerMappingHyphenStyle(true);
        if (null != configuration.getString("table.logic.delete.field")) {
            strategy.setLogicDeleteFieldName(configuration.getString("table.logic.delete.field"));
        }
        strategy.setEntityTableFieldAnnotationEnable(true);
        if (null != configuration.getString("table.prefix")) {
            String[] tablePrefixs = configuration.getString("table.prefix").split("\\,");
            strategy.setTablePrefix(tablePrefixs);
        } else {
            strategy.setTablePrefix(mpg.getPackageInfo().getModuleName() + "_");
        }

        mpg.setStrategy(strategy);
    }

    /**
     * 设置父类
     *
     * @param strategy
     */
    private void setSuperClass(StrategyConfig strategy) {
        if (StringUtils.isNotBlank(configuration.getString("super.class.controller"))) {
            strategy.setSuperControllerClass(configuration.getString("super.class.controller"));
        }
        if (StringUtils.isNotBlank(configuration.getString("super.class.entity"))) {
            strategy.setSuperEntityClass(configuration.getString("super.class.entity"));
        }
        if (StringUtils.isNotBlank(configuration.getString("super.interfaces.entity"))) {
            String[] interfaces = configuration.getString("super.interfaces.entity").split("\\,");
            for (String anInterface : interfaces) {
                strategy.setSuperEntityInterface(ClassUtils.toClassConfident(anInterface));
            }
        }
        if (StringUtils.isNotBlank(configuration.getString("super.class.service"))) {
            strategy.setSuperServiceClass(configuration.getString("super.class.service"));
        }
        if (StringUtils.isNotBlank(configuration.getString("super.class.service-impl"))) {
            strategy.setSuperServiceImplClass(configuration.getString("super.class.service-impl"));
        }
        if (StringUtils.isNotBlank(configuration.getString("super.class.mapper"))) {
            strategy.setSuperMapperClass(configuration.getString("super.class.mapper"));
        }
    }

    private static String formatPath(String packageName) {
        return packageName.replaceAll("\\.", StringPool.BACK_SLASH + File.separator);
    }
}

