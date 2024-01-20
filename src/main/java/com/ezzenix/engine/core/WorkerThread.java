package com.ezzenix.engine.core;

import com.ezzenix.engine.scheduler.Scheduler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class WorkerThread<I, O> {
    private final Queue<I> toProcessQueue = new ConcurrentLinkedDeque<>();
    private final Queue<O> processedQueue = new ConcurrentLinkedDeque<>();

    public WorkerThread(int poolSize, int interval, int maxPerInterval, Function<I, O> processObject, Function<O, Void> objectProcessed) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(poolSize);

        executor.scheduleWithFixedDelay(() -> {
            int count = 0;
            while (!toProcessQueue.isEmpty()) {
                if (count >= maxPerInterval) return; // cancel and do the rest next interval
                I obj = toProcessQueue.peek();
                if (obj != null) {
                    toProcessQueue.remove(obj);
                    O output = processObject.apply(obj);
                    processedQueue.add(output);
                    count++;
                }
            }

        }, 0, interval, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));

        Scheduler.runPeriodic(() -> {
            synchronized (processedQueue) {
                O result;
                while ((result = processedQueue.poll()) != null) {
                    objectProcessed.apply(result);
                }
            }
        }, 500);
    }

    public void add(I obj) {
        if (!toProcessQueue.contains(obj)) {
            toProcessQueue.add(obj);
        };
    }
}