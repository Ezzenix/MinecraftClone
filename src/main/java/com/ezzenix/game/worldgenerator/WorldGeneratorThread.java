package com.ezzenix.game.worldgenerator;

import com.ezzenix.engine.core.WorkerThread;
import com.ezzenix.game.world.Chunk;

public class WorldGeneratorThread {
	private static final WorkerThread<WorldGeneratorRequest> workerThread;

	static {
		workerThread = new WorkerThread<>(
				1,
				5,
				200,
				(request) -> {
					if (request.chunk.isDisposed || request.chunk.hasGenerated) return null;
					WorldGenerator.process(request);
					return null;
				},
				(request) -> {
					request.apply();
					request.chunk.hasGenerated = true;
					request.chunk.isGenerating = false;
					request.chunk.flagMeshForUpdate();
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