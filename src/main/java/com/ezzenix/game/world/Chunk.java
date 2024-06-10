package com.ezzenix.game.world;

import com.ezzenix.game.blocks.BlockRegistry;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.chunkbuilder.ChunkBuilderThread;
import com.ezzenix.game.chunkbuilder.ChunkMesh;
import com.ezzenix.game.worldgenerator.WorldGeneratorThread;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.math.LocalPosition;

import java.util.Arrays;

public class Chunk {
	public static final int CHUNK_WIDTH = 16;
	public static final int CHUNK_HEIGHT = 256;

	private final ChunkPos chunkPos;
	private final ChunkMesh chunkMesh;
	private final World world;

	private final byte[] blockIDs = new byte[CHUNK_WIDTH * CHUNK_WIDTH * CHUNK_HEIGHT];
	public int blockCount = 0;

	public boolean isGenerating = false;
	public boolean hasGenerated = false;
	public boolean isDisposed = false;


	public Chunk(ChunkPos chunkPos, World world) {
		this.chunkPos = chunkPos;
		this.world = world;

		Arrays.fill(this.blockIDs, (byte) 1);

		this.chunkMesh = new ChunkMesh(this);
	}


	public void setBlock(BlockPos blockPos, BlockType blockType) {
		int index = blockPos.toLocalPosition(this).toIndex();
		if (index == -1) {
			world.setBlock(blockPos, blockType);
			return;
		}

		if (index >= blockIDs.length) {
			System.out.println("Index out of bounds: " + index + "  " + blockPos.toLocalPosition(this));
		}

		byte id = blockIDs[index];
		if (id != blockType.getId()) {
			blockIDs[index] = blockType.getId();
			this.blockCount += (blockType != BlockType.AIR ? 1 : -1);
		}
		this.flagMeshForUpdate();
	}

	public BlockType getBlock(BlockPos blockPos) {
		int index = blockPos.toLocalPosition(this).toIndex();
		if (index == -1) {
			return world.getBlock(blockPos); // not in this chunk, redirect to world
		}
		return getBlock(index);
	}
	public BlockType getBlock(LocalPosition localPosition) {
		return getBlock(localPosition.toWorldPosition(this));
	}
	public BlockType getBlock(int index) {
		if (index == -1) { // invalid index
			//System.err.println("getBlock() got invalid index!");
			return null;
		}
		BlockType type = BlockRegistry.getBlockFromId(blockIDs[index]);
		return type != null ? type : BlockType.AIR;
	}

	public ChunkPos getPos() {
		return chunkPos;
	}

	public byte[] getBlockIDs() {
		return this.blockIDs;
	}

	public World getWorld() {
		return this.world;
	}

	public void flagMeshForUpdate() {
		if (!hasGenerated) return;
		ChunkBuilderThread.scheduleChunkForRemeshing(this);
	}

	public void generate() {
		if (hasGenerated || isGenerating) return;
		this.isGenerating = true;
		WorldGeneratorThread.scheduleChunkForWorldGeneration(this);
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
		this.world.getChunkMap().remove(chunkPos);
		this.isDisposed = true;
		this.chunkMesh.dispose();
	}
}
