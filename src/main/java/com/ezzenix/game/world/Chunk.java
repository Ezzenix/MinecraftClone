package com.ezzenix.game.world;

import com.ezzenix.game.blocks.BlockRegistry;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.math.BoundingBox;
import com.ezzenix.client.rendering.chunkbuilder.ChunkMesh;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.math.LocalPosition;
import org.joml.Vector3f;

import java.util.Arrays;

public class Chunk {
	public static final int CHUNK_WIDTH = 16;
	public static final int CHUNK_HEIGHT = 255;

	private final ChunkPos chunkPos;
	private final ChunkMesh chunkMesh;
	private final World world;

	private final byte[] blockIDs = new byte[CHUNK_WIDTH * CHUNK_WIDTH * (CHUNK_HEIGHT + 1)];
	public int blockCount = 0;

	public boolean isDisposed = false;

	public boolean hasGenerated = false;
	public boolean isGenerating = false;
	public boolean shouldMeshRebuild = false;
	public boolean isMeshRebuilding = false;

	private BoundingBox boundingBox;


	public Chunk(ChunkPos chunkPos, World world) {
		this.chunkPos = chunkPos;
		this.world = world;

		Arrays.fill(this.blockIDs, (byte) 1);

		this.chunkMesh = new ChunkMesh(this);

		this.boundingBox = new BoundingBox(getWorldPos(), getWorldPos().add(CHUNK_WIDTH, CHUNK_HEIGHT, CHUNK_WIDTH));
	}

	public void setBlock(BlockPos blockPos, BlockType blockType) {
		LocalPosition localPosition = LocalPosition.from(this, blockPos);
		int index = localPosition.toIndex();
		if (index == -1) {
			world.setBlock(blockPos, blockType);
			return;
		}

		if (index >= blockIDs.length)
			throw new RuntimeException("Index out of bounds: " + index + "  " + LocalPosition.from(this, blockPos));

		// if block above is flower then break it
		BlockPos blockAbovePos = blockPos.add(0, 1, 0);
		BlockType blockAbove = getBlock(blockAbovePos);
		if (blockAbove != null && blockAbove.isFlower()) {
			setBlock(blockAbovePos, BlockType.AIR);
		}

		byte id = blockIDs[index];
		if (id != blockType.getId()) {
			blockIDs[index] = blockType.getId();
			this.blockCount += (blockType != BlockType.AIR ? 1 : -1);
		}
		this.flagMeshForUpdate();

		if (localPosition.x == 0) {
			Chunk chunk = world.getChunk(chunkPos.x - 1, chunkPos.z);
			if (chunk != null) chunk.flagMeshForUpdate();
		}
		if (localPosition.x == Chunk.CHUNK_WIDTH - 1) {
			Chunk chunk = world.getChunk(chunkPos.x + 1, chunkPos.z);
			if (chunk != null) chunk.flagMeshForUpdate();
		}
		if (localPosition.z == 0) {
			Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z - 1);
			if (chunk != null) chunk.flagMeshForUpdate();
		}
		if (localPosition.z == Chunk.CHUNK_WIDTH - 1) {
			Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z + 1);
			if (chunk != null) chunk.flagMeshForUpdate();
		}

	}

	public BlockType getBlock(BlockPos blockPos) {
		int index = LocalPosition.from(this, blockPos).toIndex();
		if (index == -1) {
			return world.getBlock(blockPos); // not in this chunk, redirect to world
		}
		return getBlock(index);
	}
	public BlockType getBlock(LocalPosition localPosition) {
		return getBlock(BlockPos.from(this, localPosition));
	}
	public BlockType getBlock(int index) {
		if (index == -1 || index >= blockIDs.length) return null;
		BlockType type = BlockRegistry.getBlockFromId(blockIDs[index]);
		return type != null ? type : BlockType.AIR;
	}

	public ChunkPos getPos() {
		return chunkPos;
	}

	public Vector3f getWorldPos() {
		return new Vector3f(chunkPos.x * Chunk.CHUNK_WIDTH, 0, chunkPos.z * Chunk.CHUNK_WIDTH);
	}

	public byte[] getBlockIDs() {
		return this.blockIDs;
	}

	public World getWorld() {
		return this.world;
	}

	public void flagMeshForUpdate() {
		shouldMeshRebuild = true;
	}

	public BoundingBox getBoundingBox() {
		return this.boundingBox;
	}

	/**
	 * Gets the chunks mesh object
	 */
	public ChunkMesh getChunkMesh() {
		return this.chunkMesh;
	}

	/**
	 * Destroys and unloads the chunk
	 */
	public void dispose() {
		this.world.getChunks().remove(chunkPos);
		this.isDisposed = true;
		this.chunkMesh.dispose();
	}
}
