package com.ezzenix.game.worldgeneration;

import com.ezzenix.game.Chunk;

import java.util.concurrent.*;

public class WorldGeneratorThread {
    private static final BlockingQueue<Chunk> chunksToGenerate =  new ArrayBlockingQueue<>(10);

    static {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        executor.scheduleWithFixedDelay(() -> {

            while (!chunksToGenerate.isEmpty()) {
                Chunk chunk = null;
                try {
                    chunk = chunksToGenerate.take();
                } catch (InterruptedException ignored) {}
                if (chunk != null) {
                    WorldGenerator.generateChunk(chunk);
                }
            }

        }, 0, 500, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
    }

    public static void scheduleChunkForWorldGeneration(Chunk chunk) {
        if (chunksToGenerate.size() >= 10) return;
        chunksToGenerate.add(chunk);
    }
}
