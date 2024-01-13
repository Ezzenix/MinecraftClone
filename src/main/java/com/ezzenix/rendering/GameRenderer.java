package com.ezzenix.rendering;

import com.ezzenix.Game;
import com.ezzenix.game.Chunk;
import com.ezzenix.game.World;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.utils.ImageUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL45.glGenerateTextureMipmap;

public class GameRenderer {
    private final Camera camera;
    private final Shader worldShader = new Shader("world.vert", "world.frag");
    private final Shader waterShader = new Shader("water.vert", "water.frag");
    private final int blockTexture;

    public GameRenderer() {
        camera = new Camera();

        blockTexture = ImageUtil.loadTexture(Game.getInstance().blockTextures.getAtlasImage());
        glGenerateTextureMipmap(blockTexture);
        glTexParameterf( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    }

    public Camera getCamera() {
        return this.camera;
    }

    public void render(long window) {
        World world = Game.getInstance().getWorld();

        glBindTexture(GL_TEXTURE_2D, blockTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        if (world != null) {
            worldShader.use();
            worldShader.uploadMat4f("projectionMatrix", camera.getProjectionMatrix());
            worldShader.uploadMat4f("viewMatrix", camera.getViewMatrix());
            for (Chunk chunk : world.getChunks().values()) {
                Mesh mesh = chunk.mesh;
                if (mesh != null) {
                    Matrix4f translationMatrix = new Matrix4f();
                    translationMatrix.translate(new Vector3f(chunk.x * 16, chunk.y * 16, chunk.z * 16));
                    worldShader.uploadMat4f("chunkPosition", translationMatrix);

                    mesh.render();
                }
            }


            waterShader.use();
            waterShader.uploadMat4f("projectionMatrix", camera.getProjectionMatrix());
            waterShader.uploadMat4f("viewMatrix", camera.getViewMatrix());
            long timestamp = System.currentTimeMillis();
            waterShader.uploadFloat("timestamp", (float)timestamp);

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDepthMask(false);
            for (Chunk chunk : world.getChunks().values()) {
                Mesh waterMesh = chunk.waterMesh;
                if (waterMesh != null) {
                    Matrix4f translationMatrix = new Matrix4f();
                    translationMatrix.translate(new Vector3f(chunk.x * 16, chunk.y * 16, chunk.z * 16));
                    waterShader.uploadMat4f("chunkPosition", translationMatrix);

                    waterMesh.render();
                }
            }
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LESS);
            glEnable(GL_CULL_FACE);
        }
    }
}
