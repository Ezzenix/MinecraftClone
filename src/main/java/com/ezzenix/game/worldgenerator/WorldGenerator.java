package com.ezzenix.game.worldgenerator;

import com.ezzenix.game.BlockType;
import com.ezzenix.game.BlockTypes;
import com.ezzenix.game.Chunk;
import com.ezzenix.utils.BlockPos;
import org.joml.SimplexNoise;

import java.util.Random;

public class WorldGenerator {
    private static BlockType getBlockTypeAtHeight(float y, float height) {
        float blocksToTop = height-y;
        if (blocksToTop > 4) return BlockTypes.STONE;
        if (blocksToTop > 1) return BlockTypes.DIRT;
        return new Random().nextBoolean() ? BlockTypes.GRASS : BlockTypes.OAK_PLANKS;
    }

    public static void generateChunk(Chunk chunk) {
        int offsetX = chunk.getChunkX()*16;
        int offsetZ = chunk.getChunkZ()*16;

        for (int x = offsetX; x < offsetX+16; x++) {
            for (int z = offsetZ; z < offsetZ+16; z++) {
                float height = (SimplexNoise.noise((float) x /50f, (float) z /50f)+1)/2;
                height = 10 + height * 20;
                for (int y = 0; y < height; y++) {
                    BlockType blockType = getBlockTypeAtHeight(y, height);
                    chunk.setBlock(new BlockPos(x, y, z), blockType);
                }
            }
        }
    }
}
