package com.ezzenix.game.world;

import com.ezzenix.client.Client;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.game.world.gen.ChunkGenerator;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class World {
	private final ConcurrentHashMap<ChunkPos, Chunk> chunks = new ConcurrentHashMap<>();
	private final ChunkGenerator generator;
	private final List<Entity> entities;

	public World(ChunkGenerator generator) {
		this.generator = generator;
		this.entities = new ArrayList<>();
	}

	private void loadInitialChunks() {
		int WORLD_SIZE = 1;
		for (int x = 0; x < WORLD_SIZE; x++) {
			for (int z = 0; z < WORLD_SIZE; z++) {
				createChunk(x, z);
			}
		}
	}

	private Chunk createChunk(ChunkPos chunkPos, boolean doNotGenerate) {
		Chunk chunk = chunks.get(chunkPos);
		if (chunk != null) { // already exists
			chunk.doNotGenerate = doNotGenerate;
			return null;
		}
		chunk = new Chunk(chunkPos, this);
		chunks.put(chunkPos, chunk);
		chunk.doNotGenerate = doNotGenerate;
		return chunk;
	}
	private Chunk createChunk(ChunkPos chunkPos) {
		return createChunk(chunkPos, false);
	}
	private Chunk createChunk(int x, int z) {
		return createChunk(new ChunkPos(x, z));
	}

	public void setBlock(BlockPos blockPos, BlockType blockType) {
		if (!blockPos.isValid())
			throw new RuntimeException("Attempt to place block outside of world");

		ChunkPos chunkPos = ChunkPos.from(blockPos);
		Chunk chunk = getChunk(chunkPos);
		if (chunk == null) {
			chunk = createChunk(chunkPos, true);
			if (chunk == null) return;
		}
		chunk.setBlock(blockPos, blockType);
	}

	public BlockType getBlock(BlockPos blockPos) {
		Chunk chunk = getChunk(blockPos);
		if (chunk == null || !chunk.hasGenerated) return null;
		return chunk.getBlock(blockPos);
	}

	public Chunk getChunk(ChunkPos chunkPos) {
		return chunks.get(chunkPos);
	}
	public Chunk getChunk(int x, int z) {
		return getChunk(new ChunkPos(x, z));
	}
	public Chunk getChunk(BlockPos blockPos) {
		if (!blockPos.isValid()) return null;
		return getChunk(ChunkPos.from(blockPos));
	}

	public ConcurrentHashMap<ChunkPos, Chunk> getChunks() {
		return this.chunks;
	}

	public ChunkGenerator getGenerator() {
		return this.generator;
	}

	public void loadNewChunks() {
		ChunkPos chunkPos = ChunkPos.from(Client.getPlayer().getBlockPos());

		int renderDistance = 14;

		// get chunk positions in a spiral
		List<ChunkPos> chunkPositions = new ArrayList<>();
		for (int r = 0; r < renderDistance; r++) {
			for (int i = -r; i <= r; i++) {
				chunkPositions.add(new ChunkPos(chunkPos.x + i, chunkPos.z + r));
				if (r != 0) chunkPositions.add(new ChunkPos(chunkPos.x + i, chunkPos.z - r));
			}
			for (int j = -r + 1; j < r; j++) {
				chunkPositions.add(new ChunkPos(chunkPos.x + r, chunkPos.z + j));
				if (r != 0) chunkPositions.add(new ChunkPos(chunkPos.x - r, chunkPos.z + j));
			}
		}

		// create chunks
		for (ChunkPos pos : chunkPositions) {
			createChunk(pos);
		}

		// dispose chunks outside of view distance
		for (Chunk chunk : chunks.values()) {
			if (!chunkPositions.contains(chunk.getPos())) {
				chunk.dispose();
			}
		}
	}

	public List<Entity> getEntities() {
		return this.entities;
	}
}
