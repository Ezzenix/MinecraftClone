package com.ezzenix.engine.scheduler;

public class SchedulerRunnable {
    private final Runnable runnable;
    private final long interval;
    private long lastRun;

    public SchedulerRunnable(Runnable runnable, long interval) {
        this.lastRun = 0;
        this.interval = interval * 1000000;
        this.runnable = runnable;
    }

    public SchedulerRunnable(Runnable runnable) {
        this(runnable, 0L);
    }

    public void run() {
        lastRun = System.nanoTime();
        runnable.run();
    }

    public boolean canRun() {
        return System.nanoTime() > (this.lastRun + this.interval);
    }

    public void dispose() {
        Scheduler.removeRunnable(this);
    }
}
