package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${superServiceImplClassPackage};
<#if dataSourceEnabled>
    import com.baomidou.dynamic.datasource.annotation.DS;
</#if>
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
* <p>
    * ${table.comment!} 服务实现类
    * </p>
*
* @author ${author}
* @since ${date}
*/
<#if dataSourceEnabled>
    <#if dataSourceName>
        @DS("${dataSourceName}")
    <#else>
        @DS
    </#if>
</#if>
@Repository
<#if kotlin>
    open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

    }
<#else>
    public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {

    private ${table.mapperName} ${table.mapperName?uncap_first};

    @Autowired
    public ${table.serviceImplName}(${table.mapperName} ${table.mapperName?uncap_first}){
    this.${table.mapperName?uncap_first} = ${table.mapperName?uncap_first};
    }
    }
</#if>
