package com.ezzenix.rendering.chunkbuilder;

import com.ezzenix.game.workers.ChunkBuildRequest;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.engine.opengl.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class ChunkMesh {
    private Chunk chunk;
    private final Matrix4f translationMatrix;
    public Mesh blockMesh;
    public Mesh waterMesh;

    public ChunkMesh(Chunk chunk) {
        this.chunk = chunk;
        this.translationMatrix = new Matrix4f().translate(new Vector3f(chunk.getPos().x * Chunk.CHUNK_SIZE, chunk.getPos().y * Chunk.CHUNK_SIZE, chunk.getPos().z * Chunk.CHUNK_SIZE));
    }

    private Mesh createMesh(FloatBuffer buffer, int length) {
        Mesh mesh = new Mesh(buffer, length);

        int stride = 6 * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 1, GL_FLOAT, false, stride, 5 * Float.BYTES);
        glEnableVertexAttribArray(2);

        mesh.unbind();
        return mesh;
    }

    public void applyRequest(ChunkBuildRequest request) {
        if (blockMesh != null) {
            blockMesh.dispose();
            blockMesh = null;
        }
        if (waterMesh != null) {
            waterMesh.dispose();
            waterMesh = null;
        }
        if (chunk.blockCount > 0) {
            blockMesh = createMesh(request.blockVertexBuffer, request.blockVertexLength);
            waterMesh = createMesh(request.waterVertexBuffer, request.waterVertexLength);
        }
        /*
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
         */
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
