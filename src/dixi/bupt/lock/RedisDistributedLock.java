package dixi.bupt.lock;

import java.util.function.Supplier;

import org.junit.Test;

/**
 * 利用 redis 的 setnx 方法实现分布式锁
 *
 * @author chendixi
 * Created on 2025-02-18
 */
public class RedisDistributedLock {

    private DistributeLock lockService = (key, expireSeconds) -> true;

    @Test
    public void testLockRun() {
        String key = "demo_key";
        lockAndRun(key, 3, () -> {
            System.out.println("execute with lock: " + key);
        });
    }

    private void lockAndRun(String key, int expireSeconds, Runnable runnable) {
        boolean lock = false;
        try {
            lock = lockService.tryLock(key, expireSeconds);
            if (!lock) {
                throw new RuntimeException("并发冲突");
            }
            runnable.run();
        } finally {
            if (lock) {
                lockService.unlock(key);
            }
        }
    }

    @Test
    public void testLockAndCall() {
        String key = "demo_key";
        System.out.println(
                lockAndRun(key, 3, () -> "execute with lock: " + key)
        );
    }

    private <T> T lockAndRun(String key, int expireSeconds, Supplier<T> supplier) {
        boolean lock = false;
        try {
            lock = lockService.tryLock(key, expireSeconds);
            if (!lock) {
                throw new RuntimeException("并发冲突");
            }
            return supplier.get();
        } finally {
            if (lock) {
                lockService.unlock(key);
            }
        }
    }
}
