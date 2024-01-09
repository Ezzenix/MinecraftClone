package com.ezzenix.game;

import com.ezzenix.game.worldgenerator.WorldGenerator;
import com.ezzenix.rendering.ChunkBuilder;
import com.ezzenix.rendering.Mesh;
import com.ezzenix.utils.BlockPos;

import javax.sound.midi.SysexMessage;
import java.util.HashMap;

public class Chunk {
    private final HashMap<BlockPos, BlockType> blocks = new HashMap<>();
    private Mesh mesh;
    private final int x;
    private final int z;
    private final World world;

    public Chunk(int x, int z, World world) {
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public void setBlock(BlockPos blockPos, BlockType blockType) {
        blocks.put(blockPos, blockType);
    }

    public BlockType getBlockTypeAt(BlockPos blockPos) {
        for (BlockPos pos : blocks.keySet()) {
            if (blockPos.equals(pos)) {
                return blocks.get(pos);
            }
        }
        return null;
    }

    public void generate() {
        WorldGenerator.generateChunk(this);
        this.updateMesh();
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

    public World getWorld() { return this.world; }

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
