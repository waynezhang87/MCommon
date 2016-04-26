package com.waynezhang.mcommon.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by liuyagang on 16/3/17.
 */
public class DebounceExector {
    private ScheduledExecutorService executor;
    private int timeInterval;
    private AtomicReference<Runnable> runnable = new AtomicReference<>();
    private boolean isWorking;

    public DebounceExector(int timeInterval) {
        this(Executors.newSingleThreadScheduledExecutor(), timeInterval);
    }

    public DebounceExector(ScheduledExecutorService executor, int timeInterval) {
        this.executor = executor;
        this.timeInterval = timeInterval;
    }

    public void execute(final Runnable runnable) {
        this.runnable.set(runnable);
        if (isWorking) {
            return;
        }

        isWorking = true;
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                DebounceExector.this.runnable.get().run();
                DebounceExector.this.runnable.set(null);
                isWorking = false;
            }
        }, timeInterval, TimeUnit.MILLISECONDS);
    }
}
