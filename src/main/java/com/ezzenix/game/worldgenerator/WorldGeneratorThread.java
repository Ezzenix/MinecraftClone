package com.ezzenix.game.worldgenerator;

import com.ezzenix.engine.core.WorkerThread;
import com.ezzenix.game.ChunkColumnPos;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.game.world.World;

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
                    return null;
                }
        );
    }

    public static synchronized void scheduleWorldGeneration(World world, ChunkColumnPos chunkColumnPos) {
        WorldGeneratorRequest request = new WorldGeneratorRequest(world, chunkColumnPos);
        if (!request.hasAnyChunks()) return;
        workerThread.add(request);
    }
}