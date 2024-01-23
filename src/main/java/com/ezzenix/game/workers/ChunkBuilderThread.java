package com.ezzenix.game.workers;

import com.ezzenix.engine.core.WorkerThread;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.rendering.chunkbuilder.builder.ChunkBuilder;

public class ChunkBuilderThread {
	private static final WorkerThread<ChunkBuildRequest> workerThread;

	static {
		workerThread = new WorkerThread<>(
				1,
				5,
				200,
				(request) -> {
					ChunkBuilder.generate(request);
					return null;
				},
				(request) -> {
					if (request.chunk.isDisposed) return null;
					request.chunk.getChunkMesh().applyRequest(request);
					return null;
				}
		);
	}

	public static synchronized void scheduleChunkForRemeshing(Chunk chunk) {
		ChunkBuildRequest request = new ChunkBuildRequest(chunk);
		workerThread.add(request);
	}
}