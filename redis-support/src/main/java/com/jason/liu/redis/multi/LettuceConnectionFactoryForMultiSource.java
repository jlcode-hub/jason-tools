package com.jason.liu.redis.multi;

import io.lettuce.core.resource.ClientResources;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.Optional;

/**
 * 在多数据源模式下，ClientResource为工厂私有，在容器销毁时，通过工厂的destroy代理关闭
 *
 * @author meng.liu
 * @version v1.0
 * @date 2021-06-28 09:58:50
 * @todo
 */
public class LettuceConnectionFactoryForMultiSource extends LettuceConnectionFactory {

    /**
     * 单机模式
     *
     * @param standaloneConfig
     * @param clientConfig
     */
    public LettuceConnectionFactoryForMultiSource(RedisStandaloneConfiguration standaloneConfig,
                                                  LettuceClientConfiguration clientConfig) {

        super(standaloneConfig, clientConfig);
    }

    /**
     * 哨兵模式
     *
     * @param sentinelConfiguration
     * @param clientConfig
     */
    public LettuceConnectionFactoryForMultiSource(RedisSentinelConfiguration sentinelConfiguration,
                                                  LettuceClientConfiguration clientConfig) {

        super(sentinelConfiguration, clientConfig);
    }

    /**
     * 集群模式
     *
     * @param clusterConfiguration
     * @param clientConfig
     */
    public LettuceConnectionFactoryForMultiSource(RedisClusterConfiguration clusterConfiguration,
                                                  LettuceClientConfiguration clientConfig) {

        super(clusterConfiguration, clientConfig);
    }


    @Override
    public void destroy() {
        super.destroy();
        this.destroyClientResource();
    }

    private void destroyClientResource() {
        Optional<ClientResources> optionalClientResources = this.getClientConfiguration().getClientResources();
        optionalClientResources.ifPresent(ClientResources::shutdown);
    }
}
