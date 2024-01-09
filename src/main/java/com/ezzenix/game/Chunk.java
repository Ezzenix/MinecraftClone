package com.ezzenix.game;

import com.ezzenix.utilities.BlockPos;
import org.joml.Vector2i;

import java.util.HashMap;

public class Chunk {
    private final HashMap<BlockPos, BlockType> blocks = new HashMap<>();
    private final Vector2i chunkPos;

    public Chunk(Vector2i chunkPos) {
        this.chunkPos = chunkPos;

        setBlock(new BlockPos(0, 0, 0), BlockTypes.STONE);
        setBlock(new BlockPos(1, 0, 0), BlockTypes.STONE);
        setBlock(new BlockPos(0, 0, 1), BlockTypes.STONE);
        setBlock(new BlockPos(0, 1, 0), BlockTypes.STONE);
    }

    public void setBlock(BlockPos blockPos, BlockType blockType) {
        blocks.put(blockPos, blockType);
    }
}
