package com.ezzenix.world.gen;

import com.ezzenix.client.Client;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.world.Chunk;

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
		ConcurrentHashMap<ChunkPos, Chunk> chunks = Client.getWorld().getChunks();
		if (chunks.isEmpty()) return null;

		ChunkPos cameraChunkPos = ChunkPos.from(Client.getCamera().getPosition());

		Chunk closestChunk = null;
		float closestDistance = Float.MAX_VALUE;

		for (Chunk chunk : chunks.values()) {
			if (chunk.hasGenerated || chunk.isGenerating || chunk.doNotGenerate) continue;

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
			chunk.getWorld().getGenerator().generate(chunk);

			mainThreadTasks.add(() -> {
				if (chunk.isDisposed) return;

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
