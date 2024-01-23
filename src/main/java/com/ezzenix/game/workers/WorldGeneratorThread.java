package com.ezzenix.game.workers;

import com.ezzenix.engine.core.WorkerThread;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.game.world.WorldGenerator;

public class WorldGeneratorThread {
    private static final WorkerThread<WorldGeneratorRequest> workerThread;

    static {
        workerThread = new WorkerThread<>(
                1,
                5,
                200,
                (request) -> {
                    WorldGenerator.process(request);
					return null;
				},
                (request) -> {
                    request.apply();
                    request.chunk.hasGenerated = true;
                    request.chunk.isGenerating = false;
                    request.chunk.flagMeshForUpdate(true);
                    return null;
                }
        );
    }

    public static synchronized void scheduleChunkForWorldGeneration(Chunk chunk) {
        if (chunk.hasGenerated) return;
        WorldGeneratorRequest request = new WorldGeneratorRequest(chunk);
        workerThread.add(request);
    }
}