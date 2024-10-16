package com.ezzenix.world.gen.generators;

import com.ezzenix.blocks.Block;
import com.ezzenix.blocks.Blocks;
import com.ezzenix.engine.core.FastNoiseLite;
import com.ezzenix.math.BlockPos;
import com.ezzenix.world.Chunk;
import com.ezzenix.world.gen.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;

public class OverworldGenerator extends ChunkGenerator {
	private final FastNoiseLite noise;

	public OverworldGenerator(int seed) {
		super(seed);

		noise = new FastNoiseLite();
		noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
		noise.SetFractalOctaves(25);
	}

	public void generate(Chunk chunk) {
		BlockPos chunkBlockPos = chunk.getWorldBlockPos();

		for (int localX = 0; localX < Chunk.CHUNK_WIDTH; localX++) {
			for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
				for (int localZ = 0; localZ < Chunk.CHUNK_WIDTH; localZ++) {
					int absoluteX = chunk.getPos().x * Chunk.CHUNK_WIDTH + localX;
					int absoluteZ = chunk.getPos().z * Chunk.CHUNK_WIDTH + localZ;

					float value = (noise.GetNoise(absoluteX, y, absoluteZ) + 1) / 2;
					float density = (float) y / (16 * 4);

					if (value > density) {
						chunk.setBlock(new BlockPos(absoluteX, y, absoluteZ), (y <= 26) ? ((y <= 18) ? Blocks.STONE : Blocks.SAND) : Blocks.GRASS_BLOCK);
					} else {
						if (y <= 25) {
							chunk.setBlock(new BlockPos(absoluteX, y, absoluteZ), Blocks.WATER);
						}
					}
				}
			}
		}

		for (int localX = 0; localX < Chunk.CHUNK_WIDTH; localX++) {
			for (int y = 0; y < Chunk.CHUNK_HEIGHT; y++) {
				for (int localZ = 0; localZ < Chunk.CHUNK_WIDTH; localZ++) {
					int absoluteX = chunk.getPos().x * Chunk.CHUNK_WIDTH + localX;
					int absoluteZ = chunk.getPos().z * Chunk.CHUNK_WIDTH + localZ;

					BlockPos blockPos = new BlockPos(absoluteX, y, absoluteZ);
					BlockPos blockPosBelow = blockPos.add(0, -1, 0);
					Block blockType = chunk.getBlock(blockPos);
					Block blockTypeBelow = chunk.getBlock(blockPosBelow);

					if (blockTypeBelow == Blocks.GRASS_BLOCK && blockType == Blocks.AIR) {
						if (Math.random() > 0.78f) {
							chunk.setBlock(blockPos, Math.random() > 0.15f ? Blocks.GRASS : Blocks.POPPY);
						} else if (Math.random() > 0.98f) {
							placeTree(chunk, blockPos);
						}
					}
				}
			}
		}
	}

	public void placeTree(Chunk chunk, BlockPos blockPos) {
		List<BlockPos> leavesPositions = new ArrayList<>();
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				if ((x == -2 && z == -2) || (x == 2 && z == 2) || (x == -2 && z == 2) || (x == 2 && z == -2)) continue;
				leavesPositions.add(blockPos.add(x, 3, z));
				leavesPositions.add(blockPos.add(x, 4, z));
			}
		}
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				leavesPositions.add(blockPos.add(x, 5, z));
				if ((x != -1 || z != -1) && (x != 1 || z != 1) && (x != -1 || z != 1) && (x != 1 || z != -1)) {
					leavesPositions.add(blockPos.add(x, 6, z));
				}
			}
		}

		for (BlockPos pos : leavesPositions) {
			Block block = chunk.getBlock(pos);
			if (block == null || block == Blocks.AIR) {
				chunk.setBlock(pos, Blocks.OAK_LEAVES);
			}
		}

		// Logs
		chunk.setBlock(blockPos.add(new BlockPos(0, 0, 0)), Blocks.OAK_LOG);
		chunk.setBlock(blockPos.add(new BlockPos(0, 1, 0)), Blocks.OAK_LOG);
		chunk.setBlock(blockPos.add(new BlockPos(0, 2, 0)), Blocks.OAK_LOG);
		chunk.setBlock(blockPos.add(new BlockPos(0, 3, 0)), Blocks.OAK_LOG);
		chunk.setBlock(blockPos.add(new BlockPos(0, 4, 0)), Blocks.OAK_LOG);
	}
}
