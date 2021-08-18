package com.jason.liu.distributed.lock.redis;

import com.jason.liu.distributed.lock.DistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 分布式任务执行器
 * 分布式线程安全
 *
 * @author meng.liu
 * @version v1.0
 * @date 2021-07-05 15:10:16
 * @todo
 */
@Slf4j
public class DistributedWorker implements Runnable {

    private final String workerName;

    private final DistributedLock lock;

    private final Runnable worker;

    private String lockKey;

    public DistributedWorker(String workerName,
                             RedisTemplate<String, Object> template,
                             Runnable worker) {
        this.workerName = workerName;
        this.lockKey = this.workerName + "InvokeLock";
        this.lock = RedisDistributedLock.create(template);
        this.worker = worker;
    }

    @Override
    public void run() {
        if (!this.lock.lock(this.lockKey)) {
            log.debug("try lock for worker {} failed.", this.workerName);
            return;
        }
        try {
            this.worker.run();
        } finally {
            this.lock.unLock(this.lockKey);
        }
    }
}
