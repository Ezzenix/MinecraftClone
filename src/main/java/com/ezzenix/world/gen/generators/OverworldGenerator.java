package com.ezzenix.world.gen.generators;

import com.ezzenix.blocks.Block;
import com.ezzenix.blocks.BlockState;
import com.ezzenix.blocks.Blocks;
import com.ezzenix.engine.core.FastNoiseLite;
import com.ezzenix.math.BlockPos;
import com.ezzenix.world.World;
import com.ezzenix.world.chunk.Chunk;
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
		BlockPos chunkBlockPos = chunk.getBlockPos();
		World world = chunk.getWorld();

		for (int x = chunkBlockPos.x; x < chunkBlockPos.x + 16; x++) {
			for (int y = 0; y <= Chunk.CHUNK_HEIGHT; y++) {
				for (int z = chunkBlockPos.z; z < chunkBlockPos.z + 16; z++) {
					float value = (noise.GetNoise(x, y, z) + 1) / 2;
					float density = (float) y / (16 * 4);

					if (value > density) {
						Block block = (y <= 26) ? ((y <= 18) ? Blocks.STONE : Blocks.SAND) : Blocks.GRASS_BLOCK;
						world.setBlockState(new BlockPos(x, y, z), block.getDefaultState());
					} else {
						if (y <= 25) {
							world.setBlockState(new BlockPos(x, y, z), Blocks.WATER.getDefaultState());
						}
					}
				}
			}
		}

		for (int localX = 0; localX < Chunk.CHUNK_WIDTH; localX++) {
			for (int y = 0; y <= Chunk.CHUNK_HEIGHT; y++) {
				for (int localZ = 0; localZ < Chunk.CHUNK_WIDTH; localZ++) {
					int absoluteX = chunk.getPos().x * Chunk.CHUNK_WIDTH + localX;
					int absoluteZ = chunk.getPos().z * Chunk.CHUNK_WIDTH + localZ;

					BlockPos blockPos = new BlockPos(absoluteX, y, absoluteZ);
					BlockPos blockPosBelow = blockPos.add(0, -1, 0);
					Block blockType = world.getBlockState(blockPos).getBlock();
					Block blockTypeBelow = world.getBlockState(blockPosBelow).getBlock();

					if (blockTypeBelow == Blocks.GRASS_BLOCK && blockType == Blocks.AIR) {
						if (Math.random() > 0.78f) {
							world.setBlockState(blockPos, (Math.random() > 0.15f ? Blocks.GRASS : Blocks.POPPY).getDefaultState());
						} else if (Math.random() > 0.98f) {
							placeTree(world, blockPos);
						}
					}
				}
			}
		}
	}

	public void placeTree(World world, BlockPos blockPos) {
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
			if (world.getBlockState(pos).getBlock() == Blocks.AIR) {
				world.setBlockState(pos, Blocks.OAK_LEAVES.getDefaultState());
			}
		}

		// Logs
		for (int y = 0; y <= 4; y++) {
			world.setBlockState(blockPos.add(0, y, 0), Blocks.OAK_LOG.getDefaultState());
		}
	}
}
