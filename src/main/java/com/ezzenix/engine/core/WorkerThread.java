package com.ezzenix.engine.core;

import com.ezzenix.engine.scheduler.Scheduler;
import com.ezzenix.hud.Debug;

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
            //System.out.println("To process " + toProcessQueue.size() + "  To apply " + processedQueue.size());

            int count = 0;
            while (!toProcessQueue.isEmpty()) {
                if (count >= maxPerInterval) return; // cancel and do the rest next interval
                T obj = toProcessQueue.peek();
                if (obj == null) break;
                toProcessQueue.remove(obj);
                try {
                    processObject.apply(obj);
                } catch (Exception e) {
                    System.err.println("[WorkerThread] Error on process!");
                    e.printStackTrace();
                }
                processedQueue.add(obj);
                count++;
            }

        }, 0, interval, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));

        Scheduler.runPeriodic(() -> {
            if (processedQueue.isEmpty()) return;
            T result;
            while ((result = processedQueue.poll()) != null) {
                try {
                    objectProcessed.apply(result);
                } catch (Exception e) {
                    System.err.println("[WorkerThread] Error on apply!");
                    e.printStackTrace();
                }
            }
        }, interval);
    }

    public void add(T obj) {
        toProcessQueue.offer(obj);
    }
}