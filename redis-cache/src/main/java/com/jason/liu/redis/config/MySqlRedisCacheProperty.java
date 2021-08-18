package com.jason.liu.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author meng.liu
 * @version v1.0
 * @date 2021-05-26 17:56:21
 * @todo
 */
@Data
@ConfigurationProperties(prefix = "jason.tools.mysql.cache")
public class MySqlRedisCacheProperty {
    /**
     * 二级缓存有效时长
     */
    private Long expire = 600L;

}
