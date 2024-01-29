package com.ezzenix.game.worldgenerator;

import com.ezzenix.game.BlockPos;
import com.ezzenix.game.ChunkColumnPos;
import com.ezzenix.game.ChunkPos;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.game.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class WorldGeneratorRequest {
	private static final int CHUNK_GENERATION_HEIGHT = Math.round((float) WorldGenerator.WORLD_GENERATION_HEIGHT / Chunk.CHUNK_SIZE + 0.5f);

	public final ChunkColumnPos chunkColumnPos;
	public final World world;
	public final List<Chunk> chunks = new ArrayList<>();

	public HashMap<BlockPos, BlockType> blocks = new HashMap<>();

	public WorldGeneratorRequest(World world, ChunkColumnPos chunkColumnPos) {
		this.chunkColumnPos = chunkColumnPos;
		this.world = world;

		System.out.println("adding column " + chunkColumnPos);

		for (int y = 0; y < CHUNK_GENERATION_HEIGHT; y++) {
			Chunk chunk = world.getOrLoadChunk(new ChunkPos(chunkColumnPos.x, y, chunkColumnPos.z), true);
			if (chunk == null || chunk.isGenerating || chunk.hasGenerated) continue;
			System.out.println("---- " + chunk + "  " + y);
			this.chunks.add(chunk);
			chunk.isGenerating = true;
		}
	}

	public boolean hasAnyChunks() {
		return !this.chunks.isEmpty();
	}

	public void setBlock(BlockPos blockPos, BlockType blockType) {
		blocks.put(blockPos, blockType);
	}

	public BlockType getBlock(BlockPos blockPos) {
		BlockType type = blocks.get(blockPos);
		if (type == null) {
			type = world.getBlock(blockPos);
		}
		return type;
	}

	public void apply() {
		for (BlockPos blockPos : blocks.keySet()) {
			Chunk chunk = world.getChunk(ChunkPos.from(blockPos));
			if (chunk != null && !chunk.hasGenerated && chunk.isGenerating && !chunk.isDisposed) {
				BlockType blockType = blocks.get(blockPos);
				chunk.setBlock(blockPos, blockType);
			}
		}

		blocks.clear(); // clean up memory (maybe)

		for (Chunk chunk : chunks) {
			chunk.isGenerating = false;
			chunk.hasGenerated = true;

			chunk.flagMeshForUpdate(true);
		}
	}

	public boolean equals(Object v) {
		if (this == v) return true;
		if (v == null || this.getClass() != v.getClass()) return false;
		WorldGeneratorRequest other = (WorldGeneratorRequest) v;
		return this.chunkColumnPos.x == other.chunkColumnPos.x && this.chunkColumnPos.z == other.chunkColumnPos.z;
	}

	public int hashCode() {
		return Objects.hash(this.chunkColumnPos.x, this.chunkColumnPos.z);
	}
}