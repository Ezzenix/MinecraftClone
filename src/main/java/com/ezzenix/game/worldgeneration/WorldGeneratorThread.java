package com.ezzenix.game.worldgeneration;

import com.ezzenix.engine.scheduler.Scheduler;
import com.ezzenix.game.Chunk;

import java.util.concurrent.*;

public class WorldGeneratorThread {
    private static final BlockingQueue<Chunk> chunksToGenerate =  new LinkedBlockingDeque<>();
    private static final BlockingQueue<Chunk> chunksDoneGenerating =  new LinkedBlockingDeque<>();

    private static final int INTERVAL = 50;
    private static final int MAX_CHUNKS_PER_INTERVAL = 30;

    static {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.scheduleWithFixedDelay(() -> {
            int count = 0;
            while (!chunksToGenerate.isEmpty()) {
                if (count >= MAX_CHUNKS_PER_INTERVAL) return; // cancel and do the rest next interval
                try {
                    Chunk chunk = chunksToGenerate.take();
                    WorldGenerator.generateChunk(chunk);
                    chunksDoneGenerating.add(chunk);
                    count++;
                } catch (InterruptedException ignored) {}
            }

        }, 0, INTERVAL, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));

        Scheduler.runPeriodic(() -> {
            for (Chunk chunk : chunksDoneGenerating) {
                chunk.updateMesh(false);
                chunksDoneGenerating.remove(chunk);
            }
        }, 500);
    }

    public static void scheduleChunkForWorldGeneration(Chunk chunk) {
        if (chunk.hasGenerated) return;
        if (chunksToGenerate.contains(chunk)) return;
        chunksToGenerate.add(chunk);
    }
}
