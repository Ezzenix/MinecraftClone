package com.ezzenix.engine.scheduler;

public class SchedulerRunnable {
    private final Runnable runnable;
    private final long delay;
    private long lastRun;

    public SchedulerRunnable(Runnable runnable, long delay) {
        this.lastRun = 0;
        this.delay = 0;
        this.runnable = runnable;
    }

    public SchedulerRunnable(Runnable runnable) {
        this(runnable, 0L);
    }

    public void run() {
        lastRun = System.currentTimeMillis();
        runnable.run();
    }

    public boolean canRun() {
        return System.currentTimeMillis() > (this.lastRun + this.delay);
    }

    public void dispose() {
        Scheduler.removeRunnable(this);
    }
}
