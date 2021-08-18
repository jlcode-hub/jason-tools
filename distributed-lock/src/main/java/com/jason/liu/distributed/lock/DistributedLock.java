package com.jason.liu.distributed.lock;

/**
 * @author: meng.liu
 * @date: 2020/12/18
 * TODO:
 */
public interface DistributedLock {

    /**
     * 加锁
     *
     * @param lockKey
     * @return
     */
    boolean lock(String lockKey);

    /**
     * 加锁
     *
     * @param lockKey
     * @param value   锁的值
     * @return
     */
    boolean lock(String lockKey, String value);

    /**
     * 加锁
     *
     * @param lockKey
     * @param expire  过期时间： 单位秒
     * @return
     */
    boolean lock(String lockKey, long expire);

    /**
     * 加锁
     *
     * @param lockKey
     * @param value   锁的值
     * @param expire  过期时间： 单位秒
     * @return
     */
    boolean lock(String lockKey, String value, long expire);

    /**
     * 解锁
     *
     * @param lockKey
     * @return
     */
    void unLock(String lockKey);

    /**
     * 解锁
     *
     * @param lockKey
     * @param value   唯一标识，必须要与加锁时的一直才可以解锁
     * @return
     */
    void unLock(String lockKey, String value);

}
