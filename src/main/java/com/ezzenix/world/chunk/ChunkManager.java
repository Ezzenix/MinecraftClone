package com.ezzenix.world.chunk;

import com.ezzenix.math.ChunkPos;
import com.ezzenix.world.World;
import com.ezzenix.world.gen.WorldGenerator;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChunkManager {
	World world;
	Long2ObjectMap<Chunk> chunks;

	public ChunkManager(World world) {
		this.world = world;
		this.chunks = new Long2ObjectArrayMap<>();
	}

	public Chunk getChunk(ChunkPos chunkPos, boolean create, ChunkState chunkState) {
		Chunk chunk = chunks.get(chunkPos.toLong());
		if (chunk == null && create) {
			chunk = new Chunk(chunkPos, this.world);
			if (chunkState == ChunkState.FULL) {
				WorldGenerator.generate(chunk);
			}
			chunks.put(chunkPos.toLong(), chunk);
		}
		if (chunk != null && create && chunkState == ChunkState.FULL) {
			WorldGenerator.generate(chunk);
		}

		return chunk;
	}

	public Chunk getChunk(ChunkPos chunkPos, boolean create) {
		return getChunk(chunkPos, create, ChunkState.FULL);
	}

	public Collection<Chunk> getChunks() {
		return this.chunks.values();
	}

	public void removeChunk(Chunk chunk) {
		this.chunks.remove(chunk.getPos().toLong());
		chunk.dispose();
	}

	public void loadChunksAround(int chunkX, int chunkZ, int radius) {
		// get chunk positions in a spiral
		List<ChunkPos> chunkPositions = new ArrayList<>();
		for (int r = 0; r < radius; r++) {
			for (int i = -r; i <= r; i++) {
				chunkPositions.add(new ChunkPos(chunkX + i, chunkZ + r));
				if (r != 0) chunkPositions.add(new ChunkPos(chunkX + i, chunkZ - r));
			}
			for (int j = -r + 1; j < r; j++) {
				chunkPositions.add(new ChunkPos(chunkX + r, chunkZ + j));
				chunkPositions.add(new ChunkPos(chunkX - r, chunkZ + j));
			}
		}

		// create chunks
		for (ChunkPos pos : chunkPositions) {
			getChunk(pos, true);
		}

		// dispose chunks outside of view distance
		for (Chunk chunk : chunks.values()) {
			if (!chunkPositions.contains(chunk.getPos())) {
				removeChunk(chunk);
			}
		}
	}

	public enum ChunkState {
		FULL,
		NO_GEN
	}
}
