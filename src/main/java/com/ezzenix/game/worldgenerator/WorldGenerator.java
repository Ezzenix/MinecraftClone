package com.ezzenix.game.worldgenerator;

import com.ezzenix.game.Chunk;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.utils.BlockPos;
import com.ezzenix.utils.FastNoiseLite;

import java.util.Random;

public class WorldGenerator {
    private static BlockType getBlockTypeAtHeight(float y, float height) {
        float blocksToTop = height - y;
        if (blocksToTop > 4) return BlockType.STONE;
        if (blocksToTop > 1) return BlockType.DIRT;
        return new Random().nextBoolean() ? BlockType.GRASS : BlockType.OAK_PLANKS;
    }

    private static BlockType randomBlockType() {
        int i = (int)(Math.round(Math.random() * 4));
        if (i == 0) return BlockType.STONE;
        if (i == 1) return BlockType.GRASS;
        if (i == 2) return BlockType.DIRT;
        return BlockType.OAK_PLANKS;
    }

    public static void generateChunk(Chunk chunk) {
        FastNoiseLite noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        noise.SetFractalOctaves(25);

        for (int localX = 0; localX < 16; localX++) {
            for (int localY = 0; localY < 16; localY++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    int absoluteX = chunk.x * 16 + localX;
                    int absoluteY = chunk.y * 16 + localY;
                    int absoluteZ = chunk.z * 16 + localZ;

                    float value = (noise.GetNoise(absoluteX, absoluteY, absoluteZ)+1)/2;
                    float density = (float) absoluteY /(16*7);
                    if (value > density) {
                        chunk.setBlock(new BlockPos(absoluteX, absoluteY, absoluteZ), BlockType.GRASS);
                    }


                    //BlockType blockType = new Random().nextBoolean() ? BlockType.GRASS : BlockType.STONE;
                    //chunk.setBlock(new BlockPos(absoluteX, absoluteY, absoluteZ), blockType);

                    /*
                    float height = (SimplexNoise.noise((float) x / 50f, (float) z / 50f) + 1) / 2;
                    height = 50 + height * 20;
                    for (int y = 0; y < height; y++) {
                        BlockType blockType = getBlockTypeAtHeight(y, height);
                        chunk.setBlock(new BlockPos(x, y, z), blockType);
                    }
                    */
                }
            }
        }
    }
}
