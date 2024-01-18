package com.ezzenix.game.chunk;

import com.ezzenix.engine.opengl.utils.FrustumBoundingBox;
import com.ezzenix.engine.utils.BlockPos;
import com.ezzenix.game.blocks.BlockRegistry;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.world.World;
import com.ezzenix.game.world.generator.WorldGenerator;
import com.ezzenix.rendering.Mesh;
import com.ezzenix.rendering.chunkbuilder.ChunkBuilder;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Chunk {
    public static final int CHUNK_SIZE = 16;
    public static final int CHUNK_SIZE_SQUARED = (int) Math.pow(CHUNK_SIZE, 2);
    public static final int CHUNK_SIZE_CUBED = (int) Math.pow(CHUNK_SIZE, 3);

    private final World world;
    public Mesh mesh;
    public Mesh waterMesh;
    public final int x;
    public final int y;
    public final int z;

    private final byte[] blocks;
    private int blockCount;

    public boolean hasGenerated = false;

    public FrustumBoundingBox frustumBoundingBox;

    public Chunk(int x, int y, int z, World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.blocks = new byte[CHUNK_SIZE_CUBED];
        this.blockCount = 0;
        for (int i = 0; i < CHUNK_SIZE_CUBED; i++) {
            blocks[i] = (byte) 0;
        }

        this.frustumBoundingBox = new FrustumBoundingBox(
                new Vector3f(this.x, this.y, this.z),
                new Vector3f(this.x + CHUNK_SIZE, this.y + CHUNK_SIZE, this.z + CHUNK_SIZE)
        );
    }

    public Vector3i getLocalPosition(BlockPos blockPos) {
        return ChunkUtil.getLocalPosition(this, blockPos);
    }
    public int getIndexFromLocalPosition(Vector3i localPosition) {
        return ChunkUtil.getIndexFromLocalPosition(localPosition);
    }
    public Vector3i getLocalPositionFromIndex(int index) {
        return ChunkUtil.getLocalPositionFromIndex(index);
    }
    public BlockPos toWorldPos(int x, int y, int z) {
        return new BlockPos(this.x*Chunk.CHUNK_SIZE + x, this.y*Chunk.CHUNK_SIZE + y, this.z*Chunk.CHUNK_SIZE + z);
    }
    public BlockPos toWorldPos(Vector3i voxel) {
        return toWorldPos(voxel.x, voxel.y, voxel.z);
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
        return getBlockTypeAt(blockArrayIndex);
    }
    public BlockType getBlockTypeAt(Vector3i voxel) {
        return this.getWorld().getBlockTypeAt(toWorldPos(voxel));
    }
    public BlockType getBlockTypeAt(int index) {
        BlockType type = BlockRegistry.getBlockFromId(blocks[index]);
        return type != null ? type : BlockType.AIR;
    }

    public void generate() {
        WorldGenerator.generateChunk(this);
        this.updateMesh(false);
    }

    public byte[] getBlockArray() {
        return this.blocks;
    }

    public World getWorld() {
        return this.world;
    }

    public void updateMesh(boolean dontTriggerUpdatesAround) {
        if (this.mesh != null) {
            this.mesh.dispose();
            this.mesh = null;
        }
        if (this.waterMesh != null) {
            this.waterMesh.dispose();
            this.waterMesh = null;
        }
        if (blockCount > 0) {
            this.mesh = ChunkBuilder.createMesh(this, false);
            //this.waterMesh = ChunkBuilder.createMesh(this, true);
        }
        if (!dontTriggerUpdatesAround) {
            for (Vector3f face : com.ezzenix.engine.opengl.utils.OldFace.ALL) {
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

    public void dispose() {
        if (this.mesh != null) this.mesh.dispose();
        if (this.waterMesh != null) this.waterMesh.dispose();
        this.world.getChunks().remove(new Vector3i(x, y, z));
    }
}
