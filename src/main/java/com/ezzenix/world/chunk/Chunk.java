package com.ezzenix.world.chunk;

import com.ezzenix.blocks.BlockState;
import com.ezzenix.blocks.Blocks;
import com.ezzenix.rendering.chunkbuilder.ChunkBuilder;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.BoundingBox;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.world.World;
import org.joml.Vector3f;

public class Chunk {
	public static final int CHUNK_WIDTH = 16;
	public static final int CHUNK_HEIGHT = 256;

	private final ChunkPos chunkPos;
	private ChunkBuilder.BuiltChunk builtChunk;
	private final World world;

	private final PalettedContainer<BlockState> blockStateContainer;
	public int blockCount = 0;

	public boolean isDisposed = false;
	public boolean isGenerating = false;
	public boolean hasGenerated = false;

	private final BoundingBox boundingBox;

	public Chunk(ChunkPos chunkPos, World world) {
		this.chunkPos = chunkPos;
		this.world = world;
		this.blockStateContainer = new PalettedContainer<>(CHUNK_WIDTH, CHUNK_HEIGHT + 1, Blocks.AIR.getDefaultState());
		this.boundingBox = new BoundingBox(getWorldPos(), getWorldPos().add(CHUNK_WIDTH, CHUNK_HEIGHT + 1, CHUNK_WIDTH));
	}

	public void setBlockState(int x, int y, int z, BlockState blockState) {
		x = x & 15;
		z = z & 15;

		BlockState oldState = this.blockStateContainer.get(x, y, z);
		if (blockState == oldState) return;
		if (oldState.getBlock() == Blocks.AIR) {
			this.blockCount += 1;
		} else if (blockState.getBlock() == Blocks.AIR) {
			this.blockCount -= 1;
		}

		this.blockStateContainer.set(x, y, z, blockState);

		if (!this.isGenerating) {
			if (this.builtChunk != null) {
				this.builtChunk.rebuild();
			}
			if (x == 0) rebuildNeighborMesh(-1, 0);
			if (x == 15) rebuildNeighborMesh(1, 0);
			if (z == 0) rebuildNeighborMesh(0, -1);
			if (z == 15) rebuildNeighborMesh(0, 1);
			if (x == 0 && z == 0) rebuildNeighborMesh(-1, -1);
			if (x == 0 && z == 15) rebuildNeighborMesh(-1, 1);
			if (x == 15 && z == 0) rebuildNeighborMesh(1, -1);
			if (x == 15 && z == 15) rebuildNeighborMesh(1, 1);
		}
	}

	public BlockState getBlockState(int x, int y, int z) {
		return this.blockStateContainer.get(x & 15, y, z & 15);
	}

	public void rebuildNeighborMesh(int deltaX, int deltaZ) {
		Chunk chunk = world.getChunkManager().getChunk(new ChunkPos(this.chunkPos.x + deltaX, this.chunkPos.z + deltaZ), false);
		if (chunk != null && chunk.getBuiltChunk() != null) {
			chunk.getBuiltChunk().rebuild();
		}
	}

	public ChunkPos getPos() {
		return chunkPos;
	}

	public Vector3f getWorldPos() {
		return new Vector3f(chunkPos.x * Chunk.CHUNK_WIDTH, 0, chunkPos.z * Chunk.CHUNK_WIDTH);
	}

	public BlockPos getBlockPos() {
		return new BlockPos(chunkPos.x * Chunk.CHUNK_WIDTH, 0, chunkPos.z * Chunk.CHUNK_WIDTH);
	}

	public World getWorld() {
		return this.world;
	}

	public BoundingBox getBoundingBox() {
		return this.boundingBox;
	}

	public ChunkBuilder.BuiltChunk getBuiltChunk() {
		if (this.builtChunk != null) {
			return this.builtChunk;
		}
		if (Scheduler.isMainThread()) {
			this.builtChunk = new ChunkBuilder.BuiltChunk(this);
		}
		return this.builtChunk;
	}

	public void dispose() {
		this.isDisposed = true;
		this.getBuiltChunk().dispose();
	}
}
