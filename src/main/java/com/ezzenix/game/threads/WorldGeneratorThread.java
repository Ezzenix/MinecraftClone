package com.ezzenix.game.threads;

import com.ezzenix.engine.utils.WorkerThread;
import com.ezzenix.game.chunk.Chunk;
import com.ezzenix.game.world.WorldGenerator;

import java.util.function.Consumer;

public class WorldGeneratorThread {
    private static WorkerThread<Chunk> workerThread;

    static {
        Consumer<Chunk> generateChunk = (Chunk chunk) -> {
            WorldGenerator.generateChunk(chunk);
        };

        Consumer<Chunk> didGenerateChunk = (Chunk chunk) -> {
            chunk.updateMesh(false);
        };

        workerThread = new WorkerThread<>(1, 50, 30, generateChunk, didGenerateChunk);
    }

    public static synchronized void scheduleChunkForWorldGeneration(Chunk chunk) {
        if (chunk.hasGenerated) return;
        workerThread.add(chunk);
    }
}
