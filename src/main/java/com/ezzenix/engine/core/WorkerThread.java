package com.ezzenix.engine.core;

import com.ezzenix.engine.scheduler.Scheduler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class WorkerThread<T> {
    private final Queue<T> toProcessQueue = new ConcurrentLinkedDeque<>();
    private final Queue<T> processedQueue = new ConcurrentLinkedDeque<>();

    public WorkerThread(int poolSize, int interval, int maxPerInterval, Function<T, Void> processObject, Function<T, Void> objectProcessed) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(poolSize);

        executor.scheduleWithFixedDelay(() -> {
            int count = 0;
            while (!toProcessQueue.isEmpty()) {
                if (count >= maxPerInterval) return; // cancel and do the rest next interval
                T obj = toProcessQueue.peek();
                if (obj == null) break;
                toProcessQueue.remove(obj);
                processObject.apply(obj);
                processedQueue.add(obj);
                count++;
            }

        }, 0, interval, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));

        Scheduler.runPeriodic(() -> {
            if (processedQueue.isEmpty()) return;
            T result;
            while ((result = processedQueue.poll()) != null) {
                objectProcessed.apply(result);
            }
        }, interval);
    }

    public void add(T obj) {
        if (!toProcessQueue.contains(obj)) {
            toProcessQueue.add(obj);
        };
    }
}