package com.ezzenix.game.chunkbuilder;

import com.ezzenix.Game;
import com.ezzenix.engine.scheduler.Scheduler;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.math.ChunkPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunkBuilderQueue {
	private static final List<Chunk> chunks = Collections.synchronizedList(new ArrayList<>());
	private static final ExecutorService executorService = Executors.newFixedThreadPool(2);
	private static final ConcurrentLinkedQueue<Runnable> mainThreadTasks = new ConcurrentLinkedQueue<>();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdownNow));

		Scheduler.runPeriodic(ChunkBuilderQueue::processMainThreadTasks, 10);
	}

	public static void addChunk(Chunk chunk) {
		synchronized (chunks) {
			if (!chunks.contains(chunk)) {
				chunks.add(chunk);
			}
		}
	}

	public static void removeChunk(Chunk chunk) {
		synchronized (chunks) {
			chunks.remove(chunk);
		}
	}

	private static Chunk getNextChunk() {
		synchronized (chunks) {
			if (chunks.isEmpty()) return null;

			ChunkPos cameraChunkPos = ChunkPos.from(Game.getInstance().getCamera().getPosition());

			Chunk closestChunk = null;
			float closestDistance = Float.MAX_VALUE;

			for (Chunk chunk : chunks) {
				float dx = cameraChunkPos.x - chunk.getPos().x;
				float dy = cameraChunkPos.z - chunk.getPos().z;
				float distance = (float) Math.sqrt(dx * dx + dy * dy);

				if (distance < closestDistance) {
					closestDistance = distance;
					closestChunk = chunk;
				}
			}

			return closestChunk;
		}
	}

	public static void processNext() {
		Chunk chunk = getNextChunk();
		if (chunk == null) return;

		executorService.submit(() -> {
			System.out.println("Building chunk " + chunk.getPos() + " on thread " + Thread.currentThread().getName());

			mainThreadTasks.add(() -> {
				System.out.println("Applying chunk " + chunk.getPos() + " on main thread");
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
