package com.ezzenix.game.world;

import com.ezzenix.engine.utils.BlockPos;
import com.ezzenix.engine.utils.FastNoiseLite;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.chunk.Chunk;
import com.ezzenix.game.threads.WorldGeneratorThread;
import org.joml.Vector3i;

import java.util.Random;

public class WorldGenerator {
    private static FastNoiseLite noise;

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

    public static void placeTree(Chunk chunk, BlockPos blockPos) {
        chunk.setBlock(blockPos.add(new BlockPos(0, 0, 0)), BlockType.OAK_LOG);
        chunk.setBlock(blockPos.add(new BlockPos(0, 1, 0)), BlockType.OAK_LOG);
        chunk.setBlock(blockPos.add(new BlockPos(0, 2, 0)), BlockType.OAK_LOG);
        chunk.setBlock(blockPos.add(new BlockPos(0, 3, 0)), BlockType.OAK_LOG);
        chunk.setBlock(blockPos.add(new BlockPos(0, 4, 0)), BlockType.OAK_LOG);
        chunk.setBlock(blockPos.add(new BlockPos(0, 5, 0)), BlockType.OAK_LOG);
        chunk.setBlock(blockPos.add(new BlockPos(0, 6, 0)), BlockType.OAK_LOG);
        chunk.setBlock(blockPos.add(new BlockPos(0, 7, 0)), BlockType.OAK_LOG);

        chunk.setBlock(blockPos.add(new BlockPos(1, 7, 0)), BlockType.OAK_LEAVES);
        chunk.setBlock(blockPos.add(new BlockPos(-1, 7, 0)), BlockType.OAK_LEAVES);
        chunk.setBlock(blockPos.add(new BlockPos(0, 7, 1)), BlockType.OAK_LEAVES);
        chunk.setBlock(blockPos.add(new BlockPos(0, 7, -1)), BlockType.OAK_LEAVES);
        chunk.setBlock(blockPos.add(new BlockPos(0, 8, 0)), BlockType.OAK_LEAVES);
    }

    public static WorldGeneratorThread.WorldGeneratorOutput generateChunk(Chunk chunk) {
        //long startTime = System.currentTimeMillis();

        WorldGeneratorThread.WorldGeneratorOutput output = new WorldGeneratorThread.WorldGeneratorOutput(chunk);

        for (int localX = 0; localX < 16; localX++) {
            for (int localY = 0; localY < 16; localY++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    int absoluteX = chunk.x * 16 + localX;
                    int absoluteY = chunk.y * 16 + localY;
                    int absoluteZ = chunk.z * 16 + localZ;

                    float value = (noise.GetNoise(absoluteX, absoluteY, absoluteZ) + 1) / 2;
                    float density = (float) absoluteY / (16 * 4);

                    if (value > density) {
                        output.blocks.put(new BlockPos(absoluteX, absoluteY, absoluteZ), (absoluteY <= 26) ? BlockType.SAND : BlockType.GRASS_BLOCK);
                    } else {
                        if (absoluteY <= 25) {
                            output.blocks.put(new BlockPos(absoluteX, absoluteY, absoluteZ), BlockType.WATER);
                        }
                    }
                }
            }
        }

        //System.out.println("Chunk generated in " + (System.currentTimeMillis() - startTime) + "ms");
        return output;
    }
}
