package com.ezzenix.game.world.chunk.rendering;

import com.ezzenix.engine.core.enums.Face;
import com.ezzenix.game.world.chunk.Chunk;
import com.ezzenix.game.world.chunk.rendering.builder.ChunkBuilder;
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
        this.translationMatrix = new Matrix4f().translate(new Vector3f(chunk.x * Chunk.CHUNK_SIZE, chunk.y * Chunk.CHUNK_SIZE, chunk.z * Chunk.CHUNK_SIZE));
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
                        chunk.x + face.getNormal().x,
                        chunk.y + face.getNormal().y,
                        chunk.z + face.getNormal().z
                );
                if (c != null) {
                    c.updateMesh(true);
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
