# JasonTools

收集开发中编写的一些基于SpringBoot的小工具类

- counter：业务调用计数，采用独立线程异步冲刷的方式进行持久化存储
- distributed-lock：分布式锁，当前提供Redis Lua脚本实现
- env-adapter：条件适配器，适配不同场景的spring装载
- invoker-callback：基于AOP实现的方法回调
- mybatis-code-generator：Mybatis代码生成器，在MybatisPlus基础之上拆分dao层
- mybatis-plus-support：MybatisPlus基础配置支持，增分布式ID适配
- nacos-sprin-boot-refresh：Nacos补充，支持基于@ConfigurationProperties和@Value的配置刷新和日志动态刷新
- redis-cache：Mybatis基于Redis的二级缓存支持
- redis-limiter：基于Redis Lua的令牌桶限流器
- redis-support：Redis工具类支持，支持Redis多数据源配置
- slide-window：滑动窗口
- time-consume：基于滑动窗口的接口耗时统计
- verification-code：验证码工具类
- warning-notice：邮件告警通知
