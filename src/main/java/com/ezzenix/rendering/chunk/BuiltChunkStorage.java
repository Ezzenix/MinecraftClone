package com.ezzenix.rendering.chunk;

import com.ezzenix.Client;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.world.chunk.Chunk;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

import java.util.*;

public class BuiltChunkStorage {
	private final Long2ObjectMap<ChunkBuilder.BuiltChunk> builtChunks;
	private int viewDistance;

	public BuiltChunkStorage() {
		this.setViewDistance(50);

		this.builtChunks = new Long2ObjectArrayMap<>();
	}

	public void update() {
		ChunkPos cameraChunkPos = new ChunkPos(Client.getCamera().getPosition());

		//Set<Long> builtChunksToRemove = new HashSet<>(builtChunks.keySet());

		for (int i = -viewDistance; i <= viewDistance; i++) {
			for (int k = -viewDistance; k <= viewDistance; k++) {

				int x = cameraChunkPos.x + i;
				int z = cameraChunkPos.z + k;

				Chunk chunk = Client.getWorld().getChunkManager().getChunk(new ChunkPos(x, z), false);
				if (chunk != null) {

					long key = ChunkPos.toLong(x, z);
					ChunkBuilder.BuiltChunk builtChunk = builtChunks.get(key);
					if (builtChunk == null) {
						System.out.println("create");
						builtChunk = new ChunkBuilder.BuiltChunk(chunk);
						builtChunks.put(key, builtChunk);
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
		long key = chunk.getPos().toLong();
		return builtChunks.get(key);
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
