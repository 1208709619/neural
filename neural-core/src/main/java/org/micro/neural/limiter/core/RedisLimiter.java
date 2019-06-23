package org.micro.neural.limiter.core;

import lombok.extern.slf4j.Slf4j;
import org.micro.neural.config.store.IStore;
import org.micro.neural.config.store.StorePool;
import org.micro.neural.extension.Extension;

/**
 * The Limiter pf Redis.
 *
 * @author lry
 **/
@Slf4j
@Extension("redis")
public class RedisLimiter extends AbstractCallLimiter {

    private StorePool storePool = StorePool.getInstance();

    @Override
    protected Acquire tryAcquireConcurrency() {
        IStore store = storePool.getStore();

        try {
            Integer result = store.concurrency(limiterConfig.identity(), 0,
                    limiterConfig.getConcurrency(), limiterConfig.getConcurrencyTimeout());
            if (result == null) {
                return Acquire.EXCEPTION;
            } else if (result == 0) {
                return Acquire.FAILURE;
            } else {
                return Acquire.SUCCESS;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Acquire.EXCEPTION;
        }
    }

    @Override
    protected void releaseAcquireConcurrency() {
        IStore store = storePool.getStore();

        try {
            store.concurrency(limiterConfig.identity(), 1,
                    limiterConfig.getConcurrency(), limiterConfig.getConcurrencyTimeout());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    protected Acquire tryAcquireRateLimiter() {
        return null;
    }

}
