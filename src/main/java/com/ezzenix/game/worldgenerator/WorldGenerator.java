package com.ezzenix.game.worldgenerator;

import com.ezzenix.game.BlockPos;
import com.ezzenix.engine.core.FastNoiseLite;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.game.worldgenerator.WorldGeneratorRequest;

import java.util.Random;

public class WorldGenerator {
    private static final FastNoiseLite noise;

    public static int WORLD_GENERATION_HEIGHT = 50;

    static {
        noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        noise.SetFractalOctaves(25);
    }

    private static BlockType getBlockTypeAtHeight(float y, float height) {
        float blocksToTop = height - y;
        if (blocksToTop > 4) return BlockType.STONE;
        if (blocksToTop > 1) return BlockType.DIRT;
        return new Random().nextBoolean() ? BlockType.GRASS_BLOCK : BlockType.OAK_PLANKS;
    }

    private static BlockType randomBlockType() {
        int i = (int) (Math.round(Math.random() * 4));
        if (i == 0) return BlockType.STONE;
        if (i == 1) return BlockType.GRASS_BLOCK;
        if (i == 2) return BlockType.DIRT;
        return BlockType.OAK_PLANKS;
    }

    public static void placeTree(WorldGeneratorRequest request, BlockPos blockPos) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if ((x==-2 && z==-2) || (x==2 && z==2) || (x==-2 && z==2) || (x==2 && z==-2)) continue;
                request.setBlock(blockPos.add(new BlockPos(x, 3, z)), BlockType.OAK_LEAVES);
                request.setBlock(blockPos.add(new BlockPos(x, 4, z)), BlockType.OAK_LEAVES);
            }
        }
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                request.setBlock(blockPos.add(new BlockPos(x, 5, z)), BlockType.OAK_LEAVES);
				if ((x != -1 || z != -1) && (x != 1 || z != 1) && (x != -1 || z != 1) && (x != 1 || z != -1)) {
                    request.setBlock(blockPos.add(new BlockPos(x, 6, z)), BlockType.OAK_LEAVES);
				};
            }
        }

        // Logs
        request.setBlock(blockPos.add(new BlockPos(0, 0, 0)), BlockType.OAK_LOG);
        request.setBlock(blockPos.add(new BlockPos(0, 1, 0)), BlockType.OAK_LOG);
        request.setBlock(blockPos.add(new BlockPos(0, 2, 0)), BlockType.OAK_LOG);
        request.setBlock(blockPos.add(new BlockPos(0, 3, 0)), BlockType.OAK_LOG);
        request.setBlock(blockPos.add(new BlockPos(0, 4, 0)), BlockType.OAK_LOG);
    }

    public static void process(WorldGeneratorRequest request) {
        //long startTime = System.currentTimeMillis();

        for (int localX = 0; localX < Chunk.CHUNK_SIZE; localX++) {
            for (int absoluteY = 0; absoluteY < WORLD_GENERATION_HEIGHT; absoluteY++) {
                for (int localZ = 0; localZ < Chunk.CHUNK_SIZE; localZ++) {
                    int absoluteX = request.chunkColumnPos.x * Chunk.CHUNK_SIZE + localX;
                    int absoluteZ = request.chunkColumnPos.z * Chunk.CHUNK_SIZE + localZ;

                    float value = (noise.GetNoise(absoluteX, absoluteY, absoluteZ) + 1) / 2;
                    float density = (float) absoluteY / (16 * 4);

                    //if (((localX == 7) && (localZ == 7) && (absoluteY == 50)) || ((localX == 21) && (localZ == 21) && (absoluteY == 50))) {
                    //    placeTree(output, chunk, new BlockPos(absoluteX, absoluteY, absoluteZ));
                    //}

                    //if (absoluteY == 40 && absoluteX % 2 == 0 && absoluteZ % 2 == 0) {
                    //    output.blocks.put(new BlockPos(absoluteX, absoluteY, absoluteZ), Math.random() > 0.3f ? BlockType.GRASS : BlockType.POPPY);
                    //}

                    if (value > density) {
                        request.setBlock(new BlockPos(absoluteX, absoluteY, absoluteZ), (absoluteY <= 26) ? BlockType.SAND : BlockType.GRASS_BLOCK);
                    } else {
                        if (absoluteY <= 25) {
                            request.setBlock(new BlockPos(absoluteX, absoluteY, absoluteZ), BlockType.WATER);
                        }
                    }
                }
            }
        }

        for (int localX = 0; localX < Chunk.CHUNK_SIZE; localX++) {
            for (int absoluteY = 0; absoluteY < WORLD_GENERATION_HEIGHT; absoluteY++) {
                for (int localZ = 0; localZ < Chunk.CHUNK_SIZE; localZ++) {
                    int absoluteX = request.chunkColumnPos.x * Chunk.CHUNK_SIZE + localX;
                    int absoluteZ = request.chunkColumnPos.z * Chunk.CHUNK_SIZE + localZ;

                    BlockPos blockPos = new BlockPos(absoluteX, absoluteY, absoluteZ);
                    BlockPos blockPosBelow = new BlockPos(absoluteX, absoluteY-1, absoluteZ);
                    BlockType blockType = request.getBlock(blockPos);
                    BlockType blockTypeBelow = request.getBlock(blockPosBelow);

                    if (blockTypeBelow == BlockType.GRASS_BLOCK && blockType == BlockType.AIR) {
                        if (Math.random() > 0.78f) {
                            request.setBlock(blockPos, Math.random() > 0.15f ? BlockType.GRASS : BlockType.POPPY);
                        } else if (Math.random() > 0.97f) {
                            placeTree(request, blockPos);
                        }
                    }
                }
            }
        }

        //System.out.println("Chunk generated in " + (System.currentTimeMillis() - startTime) + "ms");
    }
}
