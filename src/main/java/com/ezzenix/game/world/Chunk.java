package com.ezzenix.game.world;

import com.ezzenix.game.BlockPos;
import com.ezzenix.game.ChunkPos;
import com.ezzenix.game.blocks.BlockRegistry;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.workers.ChunkBuilderThread;
import com.ezzenix.game.workers.WorldGeneratorThread;
import com.ezzenix.rendering.chunkbuilder.ChunkMesh;
import org.joml.Vector3i;

import java.util.Arrays;

public class Chunk {
	public static final int CHUNK_SIZE = 32;
	public static final int CHUNK_SIZE_SQUARED = (int) Math.pow(CHUNK_SIZE, 2);
	public static final int CHUNK_SIZE_CUBED = (int) Math.pow(CHUNK_SIZE, 3);


	private final ChunkPos chunkPos;
	private final ChunkMesh chunkMesh;
	private final World world;

	private final byte[] blockIDs = new byte[CHUNK_SIZE_CUBED];
	public int blockCount = 0;

	public boolean isGenerating = false;
	public boolean hasGenerated = false;
	public boolean isDisposed = false;
	public boolean isLoaded = false;


	public Chunk(ChunkPos chunkPos, World world) {
		this.chunkPos = chunkPos;
		this.world = world;

		Arrays.fill(this.blockIDs, (byte) 1);

		this.chunkMesh = new ChunkMesh(this);
	}


	public synchronized void setBlock(BlockPos blockPos, BlockType blockType) {
		int index = getIndex(getLocalPosition(blockPos));
		if (index == -1) {
			//System.out.println("setBlock() redirect " + blockPos + " " + this.getPos());
			world.setBlock(blockPos, blockType); // not in this chunk, redirect to world
			return;
		}
		byte id = blockIDs[index];
		if (id != blockType.getId()) {
			blockIDs[index] = blockType.getId();
			this.blockCount += (blockType != BlockType.AIR ? 1 : -1);
		}
	}

	public BlockType getBlock(BlockPos blockPos) {
		int blockArrayIndex = getIndex(getLocalPosition(blockPos));
		if (blockArrayIndex == -1) {
			return world.getBlock(blockPos); // not in this chunk, redirect to world
		}
		return getBlock(blockArrayIndex);
	}
	public BlockType getBlock(Vector3i voxel) {
		return this.getWorld().getBlock(toWorldPos(voxel));
	}
	public synchronized BlockType getBlock(int index) {
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

	public void flagMeshForUpdate(boolean dontTriggerUpdatesAround) {
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
		isDisposed = true;
		this.chunkMesh.dispose();
	}

	// Helper
	public BlockPos toWorldPos(int x, int y, int z) {
		return new BlockPos(chunkPos.x * Chunk.CHUNK_SIZE + x, chunkPos.y * Chunk.CHUNK_SIZE + y, chunkPos.z * Chunk.CHUNK_SIZE + z);
	}
	public BlockPos toWorldPos(Vector3i voxel) {
		return toWorldPos(voxel.x, voxel.y, voxel.z);
	}

	public Vector3i getLocalPosition(BlockPos blockPos) {
		return new Vector3i(blockPos.x - chunkPos.x * Chunk.CHUNK_SIZE, blockPos.y - chunkPos.y * Chunk.CHUNK_SIZE, blockPos.z - chunkPos.z * Chunk.CHUNK_SIZE);
	}
	public Vector3i getLocalPosition(int index) {
		int shift = 5;
		int mask = Chunk.CHUNK_SIZE - 1;
		int x = index & mask;
		int y = (index >> shift) & mask;
		int z = (index >> (2 * shift)) & mask;
		return new Vector3i(x, y, z);
	}

	public int getIndex(int x, int y, int z) {
		if (x < 0 || x > CHUNK_SIZE - 1 || y < 0 || y > CHUNK_SIZE - 1 || z < 0 || z > CHUNK_SIZE - 1)
			return -1; // validate
		int index = x | y << 5 | z << (2 * 5);
		return index <= CHUNK_SIZE_CUBED - 1 ? index : -1;
	}
	public int getIndex(Vector3i localPos) {
		return getIndex(localPos.x, localPos.y, localPos.z);
	}
}
