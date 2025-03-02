package dixi.bupt.lock;

/**
 * @author chendixi
 * Created on 2025-02-19
 */
public interface DistributeLock {

    boolean tryLock(String key, int expireSeconds);

    default void unlock(String key) {
        return;
    }
}
