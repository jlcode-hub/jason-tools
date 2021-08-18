package com.jason.liu.distributed.lock.redis;

import com.jason.liu.distributed.lock.DistributedLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;

/**
 * @author: meng.liu
 * @date: 2020/12/18
 * TODO:
 */
public class RedisDistributedLock implements DistributedLock {

    private RedisTemplate<String, Object> redisTemplate;

    public static RedisDistributedLock create(RedisTemplate<String, Object> redisTemplate) {
        return new RedisDistributedLock(redisTemplate);
    }

    public RedisDistributedLock(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private static final String LUA_LOCK = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then redis.call('expire', KEYS[1], ARGV[2]) return 'true' else return 'false' end";

    private static final String LUA_UNLOCK = "if redis.call('get', KEYS[1]) == ARGV[1] then redis.call('del', KEYS[1]) return 'true' end";

    private static final RedisScript<String> LOCK_SCP = RedisScript.of(LUA_LOCK, String.class);

    private static final RedisScript<String> UNLOCK_SCP = RedisScript.of(LUA_UNLOCK, String.class);


    @Override
    public boolean lock(String lockKey) {
        return this.lock(lockKey, LockUtils.getLockId(), 60);
    }

    @Override
    public boolean lock(String lockKey, String value) {
        return this.lock(lockKey, value, 60);
    }

    @Override
    public boolean lock(String lockKey, long expire) {
        return this.lock(lockKey, LockUtils.getLockId(), expire);
    }

    @Override
    public boolean lock(String lockKey, String value, long expire) {
        Object result = redisTemplate.execute(LOCK_SCP, Collections.singletonList(lockKey), value, expire);
        if (Boolean.parseBoolean(String.valueOf(result))) {
            return true;
        }
        return false;
    }

    @Override
    public void unLock(String lockKey) {
        redisTemplate.execute(UNLOCK_SCP, Collections.singletonList(lockKey), LockUtils.getLockId());
    }

    @Override
    public void unLock(String lockKey, String value) {
        redisTemplate.execute(UNLOCK_SCP, Collections.singletonList(lockKey), value);
    }
}
