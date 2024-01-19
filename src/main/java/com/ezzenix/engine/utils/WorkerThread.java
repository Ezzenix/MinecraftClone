package com.ezzenix.engine.utils;

import com.ezzenix.engine.scheduler.Scheduler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class WorkerThread<T> {
    private final Queue<T> toProcessQueue = new ConcurrentLinkedDeque<>();
    private final Queue<T> processedQueue = new ConcurrentLinkedDeque<>();

    public WorkerThread(int poolSize, int interval, int maxPerInterval, Consumer<T> processObject, Consumer<T> objectProcessed) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(poolSize);

        executor.scheduleWithFixedDelay(() -> {
            int count = 0;
            while (!toProcessQueue.isEmpty()) {
                if (count >= maxPerInterval) return; // cancel and do the rest next interval
                T obj = toProcessQueue.peek();
                if (obj != null) {
                    toProcessQueue.remove(obj);
                    processObject.accept(obj);
                    processedQueue.add(obj);
                    count++;
                }
            }

        }, 0, interval, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));

        Scheduler.runPeriodic(() -> {
            for (T obj : processedQueue) {
                objectProcessed.accept(obj);
                processedQueue.remove(obj);
            }
        }, 500);
    }

    public void add(T obj) {
        if (!toProcessQueue.contains(obj)) {
            toProcessQueue.add(obj);
        };
    }
}