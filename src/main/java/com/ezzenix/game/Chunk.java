package com.ezzenix.game;

import com.ezzenix.rendering.ChunkBuilder;
import com.ezzenix.rendering.Mesh;
import com.ezzenix.utilities.BlockPos;
import org.joml.Vector2i;

import java.util.HashMap;

public class Chunk {
    private final HashMap<BlockPos, BlockType> blocks = new HashMap<>();
    private Mesh mesh;
    private final int x;
    private final int z;

    public Chunk(int x, int z) {
        this.x = x;
        this.z = z;

        setBlock(new BlockPos(0, 0, 0), BlockTypes.STONE);
        setBlock(new BlockPos(1, 0, 0), BlockTypes.STONE);
        setBlock(new BlockPos(0, 0, 1), BlockTypes.STONE);
        setBlock(new BlockPos(0, 1, 0), BlockTypes.STONE);

        this.updateMesh();
    }

    public void setBlock(BlockPos blockPos, BlockType blockType) {
        blocks.put(blockPos, blockType);
    }

    public HashMap<BlockPos, BlockType> getBlocks() {
        return this.blocks;
    }

    public int getChunkX() {
        return this.x;
    }

    public int getChunkZ() {
        return this.z;
    }

    public void updateMesh() {
        if (this.mesh != null) {
            this.mesh.destroy();
        }
        this.mesh = ChunkBuilder.createMesh(this);
    }

    public Mesh getMesh() {
        return this.mesh;
    }
}
