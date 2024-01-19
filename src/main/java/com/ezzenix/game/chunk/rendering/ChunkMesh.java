package com.ezzenix.game.chunk.rendering;

import com.ezzenix.game.chunk.Chunk;
import com.ezzenix.game.chunk.rendering.builder.ChunkBuilder;
import com.ezzenix.engine.opengl.Mesh;
import org.joml.Vector3f;

public class ChunkMesh {
    private Chunk chunk;
    public Mesh blockMesh;
    public Mesh waterMesh;

    public ChunkMesh(Chunk chunk) {
        this.chunk = chunk;
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
            for (Vector3f face : com.ezzenix.engine.opengl.utils.OldFace.ALL) {
                Chunk c = this.chunk.getWorld().getChunk(
                        (int) (chunk.x + face.x),
                        (int) (chunk.y + face.y),
                        (int) (chunk.z + face.z)
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

    public void dispose() {
        if (blockMesh != null) blockMesh.dispose();
        if (waterMesh != null) waterMesh.dispose();
    }
}