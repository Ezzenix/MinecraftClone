package com.ezzenix.game.worldgenerator;

import com.ezzenix.game.BlockTypes;
import com.ezzenix.game.Chunk;
import com.ezzenix.utils.BlockPos;

public class WorldGenerator {
    public static void generateChunk(Chunk chunk) {
        chunk.setBlock(new BlockPos(0, 0, 0), BlockTypes.STONE);
        //for (int x = 0; x < 16; x++) {
        //    for (int z = 0; z < 16; z++) {
        //        chunk.setBlock(new BlockPos(x, 0, z), BlockTypes.STONE);
        //    }
        //}
    }
}
