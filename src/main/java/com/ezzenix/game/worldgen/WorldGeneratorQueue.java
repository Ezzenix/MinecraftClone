package com.ezzenix.game.worldgen;

import com.ezzenix.Game;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.math.ChunkPos;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldGeneratorQueue {
	private static final ExecutorService executorService = Executors.newFixedThreadPool(1);
	private static final ConcurrentLinkedQueue<Runnable> mainThreadTasks = new ConcurrentLinkedQueue<>();

	public static void initialize() {
		Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdownNow));

		Scheduler.bindToUpdate(() -> {
			WorldGeneratorQueue.processNext();
			WorldGeneratorQueue.processMainThreadTasks();
		});
	}

	private static Chunk getNextChunk() {
		ConcurrentHashMap<ChunkPos, Chunk> chunks = Game.getInstance().getWorld().getChunks();
		if (chunks.isEmpty()) return null;

		ChunkPos cameraChunkPos = ChunkPos.from(Game.getInstance().getCamera().getPosition());

		Chunk closestChunk = null;
		float closestDistance = Float.MAX_VALUE;

		for (Chunk chunk : chunks.values()) {
			if (chunk.hasGenerated || chunk.isGenerating) continue;

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

		chunk.isGenerating = true;

		executorService.submit(() -> {
			WorldGeneratorRequest request = new WorldGeneratorRequest(chunk);
			WorldGenerator.process(request);

			mainThreadTasks.add(() -> {
				if (chunk.isDisposed) return;

				request.apply();
				chunk.hasGenerated = true;
				chunk.isGenerating = false;
				chunk.flagMeshForUpdate();
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
