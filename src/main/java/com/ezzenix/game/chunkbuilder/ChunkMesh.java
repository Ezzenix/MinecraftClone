package com.ezzenix.game.chunkbuilder;

import com.ezzenix.engine.core.enums.Face;
import com.ezzenix.game.chunkbuilder.builder.ChunkBuilder;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.engine.opengl.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ChunkMesh {
    private Chunk chunk;
    private final Matrix4f translationMatrix;
    public Mesh blockMesh;
    public Mesh waterMesh;

    public ChunkMesh(Chunk chunk) {
        this.chunk = chunk;
        this.translationMatrix = new Matrix4f().translate(new Vector3f(chunk.getPos().x * Chunk.CHUNK_SIZE, chunk.getPos().y * Chunk.CHUNK_SIZE, chunk.getPos().z * Chunk.CHUNK_SIZE));
    }

    public void refresh(boolean dontTriggerUpdatesAround) {
        if (blockMesh != null) {
            blockMesh.dispose();
            blockMesh = null;
        }
        if (waterMesh != null) {
            waterMesh.dispose();
            waterMesh = null;
        }
        if (chunk.blockCount > 0) {
            blockMesh = ChunkBuilder.createMesh(chunk, false);
            waterMesh = ChunkBuilder.createMesh(chunk, true);
        }
        if (!dontTriggerUpdatesAround) {
            for (Face face : Face.values()) {
                Chunk c = this.chunk.getWorld().getChunk(
                        chunk.getPos().x + face.getNormal().x,
                        chunk.getPos().y + face.getNormal().y,
                        chunk.getPos().z + face.getNormal().z
                );
                if (c != null) {
                    c.flagMeshForUpdate(true);
                }
            }
        }
    }

    public Mesh getBlockMesh() {
        return blockMesh;
    }
    public Mesh getWaterMesh() {
        return waterMesh;
    }

    public void renderBlocks() {
        if (blockMesh != null) blockMesh.render();
    }

    public void renderWater() {
        if (waterMesh != null) waterMesh.render();
    }

    public Matrix4f getTranslationMatrix() {
        return translationMatrix;
    }

    public void dispose() {
        if (blockMesh != null) blockMesh.dispose();
        if (waterMesh != null) waterMesh.dispose();
    }
}
