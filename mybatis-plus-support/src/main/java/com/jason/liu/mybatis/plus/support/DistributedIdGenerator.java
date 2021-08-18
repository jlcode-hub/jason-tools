package com.jason.liu.mybatis.plus.support;

/**
 * 分布式ID生成器
 *
 * @author meng.liu
 * @version 1.0
 * @date 2021-07-14 10:23:13
 */
public interface DistributedIdGenerator {

    /**
     * 获取Id
     *
     * @param tableKey
     * @return
     */
    Long nextId(String tableKey);

}
