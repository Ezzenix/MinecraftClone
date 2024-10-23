package com.ezzenix.rendering.chunk;

import com.ezzenix.Client;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.world.chunk.Chunk;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BuiltChunkStorage {
	private final ConcurrentHashMap<ChunkPos, ChunkBuilder.BuiltChunk> builtChunks;
	private int viewDistance;

	public BuiltChunkStorage() {
		this.setViewDistance(12);

		this.builtChunks = new ConcurrentHashMap<>();
	}

	public void update() {
		ChunkPos cameraChunkPos = new ChunkPos(Client.getCamera().getPosition());

		//Set<Long> builtChunksToRemove = new HashSet<>(builtChunks.keySet());

		for (int i = -viewDistance; i <= viewDistance; i++) {
			for (int k = -viewDistance; k <= viewDistance; k++) {

				int x = cameraChunkPos.x + i;
				int z = cameraChunkPos.z + k;

				ChunkPos chunkPos = new ChunkPos(x, z);

				Chunk chunk = Client.getWorld().getChunkManager().getChunk(chunkPos, false);
				if (chunk != null) {

					ChunkBuilder.BuiltChunk builtChunk = builtChunks.get(chunkPos);
					if (builtChunk == null) {
						builtChunk = new ChunkBuilder.BuiltChunk(chunk);
						builtChunks.put(chunkPos, builtChunk);
					}

					//builtChunksToRemove.remove(key);
				}
			}
		}

		/*
		for (long key : builtChunksToRemove) {
			ChunkBuilder.BuiltChunk builtChunk = builtChunks.remove(key);
			if (builtChunk != null) {
				builtChunk.delete();
			}
		}
		 */
	}

	public ChunkBuilder.BuiltChunk getBuiltChunk(Chunk chunk) {
		return builtChunks.get(chunk.getPos());
	}

	public void scheduleRebuild(Chunk chunk) {
		ChunkBuilder.BuiltChunk builtChunk = getBuiltChunk(chunk);
		if (builtChunk != null) {
			builtChunk.scheduleRebuild();
		}
	}

	public Collection<ChunkBuilder.BuiltChunk> getBuiltChunks() {
		return this.builtChunks.values();
	}

	public void setViewDistance(int distance) {
		this.viewDistance = distance;
	}
}
