package com.ezzenix.game;

import com.ezzenix.game.blocks.BlockRegistry;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.worldgenerator.WorldGenerator;
import com.ezzenix.rendering.ChunkBuilder;
import com.ezzenix.rendering.Mesh;
import com.ezzenix.engine.opengl.utils.BlockPos;
import com.ezzenix.engine.opengl.utils.Face;
import com.ezzenix.engine.opengl.utils.FrustumBoundingBox;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Chunk {
    public static final int CHUNK_SIZE = 32;
    public static final int CHUNK_SIZE_SQUARED = (int) Math.pow(CHUNK_SIZE, 2);
    public static final int CHUNK_SIZE_CUBED = (int) Math.pow(CHUNK_SIZE, 3);

    private final World world;
    private Mesh mesh;
    public final int x;
    public final int y;
    public final int z;

    private final Byte[] blocks;
    private int blockCount;

    public FrustumBoundingBox frustumBoundingBox;

    public Chunk(int x, int y, int z, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.blocks = new Byte[CHUNK_SIZE_CUBED];
        this.blockCount = 0;
        for (int i = 0; i < CHUNK_SIZE_CUBED; i++) {
            blocks[i] = (byte) 0;
        }

        this.frustumBoundingBox = new FrustumBoundingBox(
                new Vector3f(this.x, this.y, this.z),
                new Vector3f(this.x+CHUNK_SIZE, this.y+CHUNK_SIZE, this.z+CHUNK_SIZE)
        );
    }

    public Vector3i getLocalPosition(BlockPos blockPos) {
        return new Vector3i(blockPos.x - this.x * 16, blockPos.y - this.y * 16, blockPos.z - this.z * 16);
    }

    public int getIndexFromLocalPosition(Vector3i localPosition) {
        return localPosition.x | localPosition.y << 4 | localPosition.z << 8;
    }

    public Vector3i getLocalPositionFromIndex(int index) {
        int mask = 0xF; // This is 15 in decimal, representing the lowest 4 bits
        int x = index & mask;
        int y = (index >> 4) & mask;
        int z = (index >> 8) & mask;
        return new Vector3i(x, y, z);
    }

    public void setBlock(BlockPos blockPos, BlockType blockType) {
        int blockArrayIndex = getIndexFromLocalPosition(getLocalPosition(blockPos));
        byte id = blocks[blockArrayIndex];
        if (id != blockType.getId()) {
            blocks[blockArrayIndex] = blockType.getId();
            this.blockCount += (blockType != BlockType.AIR ? 1 : -1);
        }
    }

    public BlockType getBlockTypeAt(BlockPos blockPos) {
        int blockArrayIndex = getIndexFromLocalPosition(getLocalPosition(blockPos));
        Byte id = blocks[blockArrayIndex];
        if (id == null) return BlockType.AIR;
        return BlockRegistry.getBlockFromId(id);
    }

    public void generate() {
        WorldGenerator.generateChunk(this);
        this.updateMesh(false);
    }

    public Byte[] getBlockArray() {
        return this.blocks;
    }

    public World getWorld() {
        return this.world;
    }

    public void updateMesh(boolean dontTriggerUpdatesAround) {
        if (this.mesh != null) {
            this.mesh.destroy();
        }
        if (blockCount > 0) {
            this.mesh = ChunkBuilder.createMesh(this);
        }
        if (!dontTriggerUpdatesAround) {
            for (Vector3f face : Face.ALL) {
                Chunk chunk = getWorld().getChunk(
                        (int) (x + face.x),
                        (int) (y + face.y),
                        (int) (z + face.z)
                );
                if (chunk != null) {
                    chunk.updateMesh(true);
                }
            }
        }
    }

    public Mesh getMesh() {
        return this.mesh;
    }
}
