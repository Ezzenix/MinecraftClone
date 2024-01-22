package com.ezzenix.game.workers;

import com.ezzenix.engine.core.WorkerThread;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.game.world.WorldGenerator;

public class WorldGeneratorThread {
    private static final WorkerThread<Chunk> workerThread;

    static {
        workerThread = new WorkerThread<>(
                1,
                10,
                100,
                (chunk) -> {
                    WorldGenerator.generate(chunk);
					return null;
				},
                (chunk) -> {
                    chunk.hasGenerated = true;
                    chunk.isBeingGenerated = false;
                    chunk.flagMeshForUpdate(true);
                    return null;
                }
        );
    }

    public static synchronized void scheduleChunkForWorldGeneration(Chunk chunk) {
        if (chunk.hasGenerated) return;
        workerThread.add(chunk);
    }
}