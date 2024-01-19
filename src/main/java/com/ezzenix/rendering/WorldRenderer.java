package com.ezzenix.rendering;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import com.ezzenix.game.chunk.Chunk;
import com.ezzenix.game.chunk.rendering.ChunkMesh;
import com.ezzenix.game.world.World;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer {
    private final Shader worldShader = new Shader("world.vert", "world.frag");
    private final Shader waterShader = new Shader("water.vert", "water.frag");
    private final Texture blockTexture;

    private final Vector2f textureAtlasSize;

    public WorldRenderer() {
        blockTexture = new Texture(Game.getInstance().blockTextures.getAtlasImage());
        textureAtlasSize = new Vector2f(
                Game.getInstance().blockTextures.getAtlasImage().getWidth(),
                Game.getInstance().blockTextures.getAtlasImage().getHeight()
        );
        //blockTexture.generateMipmap();
        //blockTexture.setParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        blockTexture.setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    }

    public void render(long window) {
        World world = Game.getInstance().getWorld();
        if (world == null) return;

        blockTexture.bind();

        Camera camera = Game.getInstance().getCamera();
        Matrix4f projectionMatrix = camera.getProjectionMatrix();
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f viewProjectionMatrix = camera.getViewProjectionMatrix();

        // Frustum culling
        //for (Chunk chunk : world.getChunks().values()) {
        //    chunk.frustumBoundingBox.isShown = chunk.frustumBoundingBox.isInsideFrustum(viewProjectionMatrix);
       // }

        worldShader.use();
        worldShader.uploadMat4f("projectionMatrix", projectionMatrix);
        worldShader.uploadMat4f("viewMatrix", viewMatrix);
        worldShader.uploadVec2f("textureAtlasSize", textureAtlasSize);
        for (Chunk chunk : world.getChunks().values()) {
            //if (!chunk.frustumBoundingBox.isShown) continue;
            ChunkMesh mesh = chunk.getChunkMesh();
            Matrix4f translationMatrix = new Matrix4f().translate(new Vector3f(chunk.x * 16, chunk.y * 16, chunk.z * 16));
            worldShader.uploadMat4f("chunkPosition", translationMatrix);
            mesh.renderBlocks();
        }

        waterShader.use();
        waterShader.uploadMat4f("projectionMatrix", projectionMatrix);
        waterShader.uploadMat4f("viewMatrix", viewMatrix);
        waterShader.uploadVec2f("textureAtlasSize", textureAtlasSize);
        //long timestamp = System.currentTimeMillis();
        //waterShader.uploadFloat("timestamp", (float) timestamp);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(false);
        for (Chunk chunk : world.getChunks().values()) {
            //if (!chunk.frustumBoundingBox.isShown) continue;
            ChunkMesh mesh = chunk.getChunkMesh();
            Matrix4f translationMatrix = new Matrix4f().translate(new Vector3f(chunk.x * 16, chunk.y * 16, chunk.z * 16));
            waterShader.uploadMat4f("chunkPosition", translationMatrix);
            mesh.renderWater();
        }
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glEnable(GL_CULL_FACE);
    }
}
