# Redis支持类

- 自定义Redis的装配规则，替换Spring框架的默认装配类，采用Lettuce客户端进行连接
- ~~@EnableRedisSupport开启Redis的自动装配~~ @since 1.0.4采用自动装配方式
- ~~@EnableRedisSupport(multi=true)开启多数据源配置~~ @since 1.0.4 使用spring.redis.enable-multi-source配置进行开启
- 通过注入RedisTemplateSupport来进行Redis操作
- 在多数据源时注册bean的名称对应如下，可以通过Bean名称获取别名来注入指定的bean

  |  对象   | Bean名称  | 别名 |
        |  ----   | ----     | ---- |
  |  RedisTemplate   | key$$RedisTemplate  | 无 |
  |  RedisTemplateSupport   | key$$RedisTemplate$$Support  | key |
  |  StringRedisTemplate   | key$$StringRedisTemplate  | 无 |
  |  StringRedisTemplateSupport   | key$$StringRedisTemplate$$Support  | $key |

- 在多数据源时也可以通过RedisTemplateSupportFactory工厂类来获取相应的RedisTemplateSupport
- 使用spring.redis.use-key-prefix来配置是否启用前缀，默认启用
-

使用spring.redis.key-prefix来配置Redis的业务前缀，不配置时使用spring.application.name来作为默认配置，当spring.application.name也未配置时使用"[JasonToolsUnnamedServer]"
来作为默认前缀

- 当开启前缀时，如果某些特殊场景不需要使用前缀，可以通过RedisTemplateSupport.withoutPrefixConsumer或RedisTemplateSupport.withoutPrefix来实现

