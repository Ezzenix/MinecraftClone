package com.ezzenix.world;

import com.ezzenix.blocks.BlockState;
import com.ezzenix.blocks.Blocks;
import com.ezzenix.entities.Entity;
import com.ezzenix.entities.EntityDimensions;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.world.chunk.Chunk;
import com.ezzenix.world.chunk.ChunkManager;
import com.ezzenix.world.gen.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;

public class World {
	private final ChunkGenerator generator;
	private final List<Entity> entities;
	private final ChunkManager chunkManager;

	public World(ChunkGenerator generator) {
		this.generator = generator;
		this.entities = new ArrayList<>();
		this.chunkManager = new ChunkManager(this);
	}

	public void tick() {
		for (Entity entity : this.entities) {
			entity.tick();
		}
	}

	public void setBlockState(BlockPos blockPos, BlockState blockState) {
		if (!blockPos.isValid())
			throw new IllegalArgumentException("Invalid blockPos: " + blockPos);

		ChunkPos chunkPos = new ChunkPos(blockPos);
		Chunk chunk = chunkManager.getChunk(chunkPos, true, ChunkManager.ChunkState.NO_GEN);

		chunk.setBlockState(blockPos.x, blockPos.y, blockPos.z, blockState);
	}

	public BlockState getBlockState(BlockPos blockPos) {
		if (!blockPos.isValid())
			return Blocks.AIR.getDefaultState();

		ChunkPos chunkPos = new ChunkPos(blockPos);
		Chunk chunk = chunkManager.getChunk(chunkPos, false);

		if (chunk != null) {
			return chunk.getBlockState(blockPos.x, blockPos.y, blockPos.z);
		}

		return Blocks.AIR.getDefaultState();
	}

	public ChunkManager getChunkManager() {
		return this.chunkManager;
	}

	public ChunkGenerator getGenerator() {
		return this.generator;
	}

	public List<Entity> getEntities() {
		return this.entities;
	}
}
