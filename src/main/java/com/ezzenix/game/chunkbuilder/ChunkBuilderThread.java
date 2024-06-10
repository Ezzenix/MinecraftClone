package com.ezzenix.game.chunkbuilder;

import com.ezzenix.engine.core.WorkerThread;
import com.ezzenix.game.chunkbuilder.builder.ChunkBuilder;
import com.ezzenix.game.world.Chunk;

public class ChunkBuilderThread {
	private static final WorkerThread<ChunkBuildRequest> workerThread;

	static {
		workerThread = new WorkerThread<>(
			1,
			5,
			200,
			(request) -> {
				if (request.chunk.isDisposed) return null;
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

	public static void scheduleChunkForRemeshing(Chunk chunk) {
		ChunkBuildRequest request = new ChunkBuildRequest(chunk);
		workerThread.add(request);
	}
}