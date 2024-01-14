package com.ezzenix.engine.scheduler;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private static final List<SchedulerRunnable> runnables = new ArrayList<>();
    private static float fps = 0;
    private static float deltaTime = (float) 1 / 60;
    private static long lastUpdate = System.currentTimeMillis();

    public static void update() {
        deltaTime = (System.currentTimeMillis() - lastUpdate);
        fps = (float) Math.round(1000f / deltaTime);
        lastUpdate = System.currentTimeMillis();

        for (SchedulerRunnable schedulerRunnable : runnables) {
            if (schedulerRunnable.canRun()) {
                schedulerRunnable.run();
            }
        }
    }

    public static float getDeltaTime() {
        return deltaTime;
    }

    public static float getFps() {
        return fps;
    }

    public static SchedulerRunnable bindToUpdate(Runnable runnable) {
        SchedulerRunnable schedulerRunnable = new SchedulerRunnable(runnable);
        runnables.add(schedulerRunnable);
        return schedulerRunnable;
    }

    public static SchedulerRunnable runPeriodic(Runnable runnable, long delay) {
        SchedulerRunnable schedulerRunnable = new SchedulerRunnable(runnable, delay);
        runnables.add(schedulerRunnable);
        return schedulerRunnable;
    }

    public static void removeRunnable(SchedulerRunnable schedulerRunnable) {
        runnables.remove(schedulerRunnable);
    }

    public static void sleep(float ms) {
        try {
            Thread.sleep((long) ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
