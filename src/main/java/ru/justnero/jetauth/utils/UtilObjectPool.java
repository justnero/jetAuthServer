package ru.justnero.jetauth.utils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

abstract class UtilObjectPool<T> {
    
    protected ConcurrentLinkedQueue<T> pool;
    protected ScheduledExecutorService executorService;

    public UtilObjectPool() {}
    
    public UtilObjectPool(final int minIdle) {
        initialize(minIdle);
    }
    
    public UtilObjectPool(final int minIdle, final int maxIdle, final long validationInterval) {
        initialize(minIdle);
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                int size = pool.size();
                if(size < minIdle) {
                    int sizeToBeAdded = minIdle - size;
                    for(int i=0;i<sizeToBeAdded;i++) {
                        pool.add(create());
                    }
                } else if(size > maxIdle) {
                    int sizeToBeRemoved = size - maxIdle;
                    for(int i=0;i<sizeToBeRemoved;i++) {
                        pool.poll();
                    }
                }
            }
        },validationInterval,validationInterval,TimeUnit.SECONDS);
    }

    public T pull() {
        T object;
        if((object = pool.poll()) == null) {
            object = create();
        }
        return object;
    }

    public void put(T object) {
        if(object == null) {
            return;
        }
        this.pool.offer(object);
    }

    public void shutdown() {
        if(executorService != null) {
            executorService.shutdown();
        }
    }

    protected abstract T create();

    protected final void initialize(final int minIdle) {
        pool = new ConcurrentLinkedQueue<T>();
        for(int i=0;i<minIdle;i++) {
            pool.add(create());
        }
    }
}