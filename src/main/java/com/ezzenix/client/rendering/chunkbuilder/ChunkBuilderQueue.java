package com.ezzenix.client.rendering.chunkbuilder;

import com.ezzenix.client.Client;
import com.ezzenix.client.rendering.chunkbuilder.builder.ChunkBuilder;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.math.ChunkPos;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkBuilderQueue {
	private static final ExecutorService executorService = Executors.newFixedThreadPool(1);
	private static final ConcurrentLinkedQueue<Runnable> mainThreadTasks = new ConcurrentLinkedQueue<>();

	public static void initialize() {
		Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdownNow));

		Scheduler.bindToUpdate(() -> {
			ChunkBuilderQueue.processNext();
			ChunkBuilderQueue.processMainThreadTasks();
		});
	}

	private static Chunk getNextChunk() {
		ConcurrentHashMap<ChunkPos, Chunk> chunks = Client.getWorld().getChunks();
		if (chunks.isEmpty()) return null;

		ChunkPos cameraChunkPos = ChunkPos.from(Client.getCamera().getPosition());

		Chunk closestChunk = null;
		float closestDistance = Float.MAX_VALUE;

		for (Chunk chunk : chunks.values()) {
			if (!chunk.shouldMeshRebuild || chunk.isMeshRebuilding || !chunk.hasGenerated) continue;

			float distance = cameraChunkPos.distanceTo(chunk.getPos());
			if (distance < closestDistance) {
				closestDistance = distance;
				closestChunk = chunk;
			}
		}

		return closestChunk;
	}

	public static void processNext() {
		Chunk chunk = getNextChunk();
		if (chunk == null) return;

		chunk.shouldMeshRebuild = false;
		chunk.isMeshRebuilding = true;

		executorService.submit(() -> {
			ChunkBuildRequest request = new ChunkBuildRequest(chunk);
			ChunkBuilder.generate(request);

			mainThreadTasks.add(() -> {
				if (chunk.isDisposed) return;

				request.chunk.getChunkMesh().applyRequest(request);
				chunk.isMeshRebuilding = false;
			});
		});
	}

	public static void processMainThreadTasks() {
		Runnable task;
		while ((task = mainThreadTasks.poll()) != null) {
			task.run();
		}
	}
}
