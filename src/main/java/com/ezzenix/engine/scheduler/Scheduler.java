package com.ezzenix.engine.scheduler;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private static final List<SchedulerRunnable> runnables = new ArrayList<>();
    private static float deltaTime = (float) 1 / 60;
    private static long lastUpdate = System.nanoTime();

    private static final int FPS_BUFFER_SIZE = 60;
    private static final List<Float> fpsBuffer = new ArrayList<>();

    public static void update() {
        deltaTime = (System.nanoTime() - lastUpdate)/1_000_000f;
        fpsBuffer.add((float) Math.round(1000f / deltaTime));
        if (fpsBuffer.size() > FPS_BUFFER_SIZE) fpsBuffer.remove(0);
        lastUpdate = System.nanoTime();

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
        return (float) fpsBuffer.stream().mapToDouble(Float::doubleValue).average().orElse(0.0);
    }

    public static SchedulerRunnable bindToUpdate(Runnable runnable) {
        SchedulerRunnable schedulerRunnable = new SchedulerRunnable(runnable);
        runnables.add(schedulerRunnable);
        return schedulerRunnable;
    }

    public static SchedulerRunnable runPeriodic(Runnable runnable, long interval) {
        SchedulerRunnable schedulerRunnable = new SchedulerRunnable(runnable, interval);
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
