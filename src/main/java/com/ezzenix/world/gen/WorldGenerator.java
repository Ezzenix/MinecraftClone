package com.ezzenix.world.gen;

import com.ezzenix.Client;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.world.chunk.Chunk;
import com.google.common.collect.Queues;
import com.google.common.primitives.Floats;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public class WorldGenerator {
	private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

	private static final PriorityBlockingQueue<GenerateTask> taskQueue = Queues.newPriorityBlockingQueue();

	public static void pollQueue() {
		GenerateTask task = taskQueue.poll();
		if (task == null) return;

		Chunk chunk = task.chunk;

		CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(task::run, executorService);
		future.thenAccept(success -> {
			Scheduler.recordMainThreadCall(() -> {
				chunk.isGenerating = false;
				chunk.hasGenerated = true;

				chunk.getBuiltChunk().rebuild();
				//chunk.rebuildNeighborMesh(1, 0);
				//chunk.rebuildNeighborMesh(1, 1);
				//chunk.rebuildNeighborMesh(0, 1);
				//chunk.rebuildNeighborMesh(0, 0);
			});
		}).exceptionally(ex -> {
			ex.printStackTrace();
			return null;
		});
	}

	public static void generate(Chunk chunk) {
		if (chunk.isGenerating || chunk.hasGenerated) return;
		chunk.isGenerating = true;

		GenerateTask task = new GenerateTask(chunk);
		taskQueue.offer(task);
	}

	private static class GenerateTask implements Comparable<GenerateTask> {
		private final Chunk chunk;

		public GenerateTask(Chunk chunk) {
			this.chunk = chunk;
		}

		public boolean run() {
			chunk.getWorld().getGenerator().generate(chunk);
			return true;
		}

		public float getDistance() {
			return this.chunk.getPos().distanceTo(new ChunkPos(Client.getCamera().getPosition()));
		}

		@Override
		public int compareTo(@NotNull GenerateTask task) {
			return Floats.compare(this.getDistance(), task.getDistance());
		}
	}
}
