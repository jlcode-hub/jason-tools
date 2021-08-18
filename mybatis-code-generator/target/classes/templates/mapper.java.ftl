package ${package.Mapper};

import ${package.Entity}.${entity};
import ${superMapperClassPackage};
<#if enableCache>
import org.apache.ibatis.annotations.CacheNamespaceRef;
</#if>

/**
 * <p>
 * ${table.comment!} Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if enableCache>
@CacheNamespaceRef(name="${package.Mapper}.${table.mapperName}")
</#if>
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
public interface ${table.mapperName} extends ${superMapperClass}<${entity}> {

}
</#if>
