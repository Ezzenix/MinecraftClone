package com.ezzenix.game.worldgenerator;

import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.math.BlockPos;

import java.util.HashMap;

public class WorldGeneratorRequest {
	public Chunk chunk;

	public HashMap<BlockPos, BlockType> blocks = new HashMap<>();

	public WorldGeneratorRequest(Chunk chunk) {
		this.chunk = chunk;
	}

	public void setBlock(BlockPos blockPos, BlockType blockType) {
		blocks.put(blockPos, blockType);
	}

	public BlockType getBlock(BlockPos blockPos) {
		return blocks.get(blockPos);
	}

	public void apply() {
		for (BlockPos blockPos : blocks.keySet()) {
			BlockType blockType = blocks.get(blockPos);

			chunk.setBlock(blockPos, blockType);
		}

		blocks.clear();
	}
}