package com.ezzenix.hud;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.game.Chunk;
import com.ezzenix.game.World;
import com.ezzenix.rendering.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Hud {
    FontRenderer fontRenderer;
    TextComponent fpsText;
    TextComponent positionText;
    TextComponent cameraText;
    TextComponent vertexText;
    TextComponent memoryText;

    Shader textShader;

    public Matrix4f hudProjectionMatrix = new Matrix4f().setOrtho2D(0, 500, 0, 500);

    private long lastDebugTextUpdate = System.currentTimeMillis();

    public Hud() {
        this.textShader = new Shader("text.vert", "text.frag");
        this.fontRenderer = new FontRenderer(new Font("Arial", Font.PLAIN, 18));

        fpsText = new TextComponent(fontRenderer, "", 10, 10);
        positionText = new TextComponent(fontRenderer, "", 10, 10+20);
        cameraText = new TextComponent(fontRenderer, "", 10, 10+20*2);
        vertexText = new TextComponent(fontRenderer, "", 10, 10+20*3);
        memoryText = new TextComponent(fontRenderer, "", 10, 10+20*4);
    }

    public void render(long window) {
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glBindTexture(GL_TEXTURE_2D, this.fontRenderer.getAtlasTextureId());
        this.textShader.use();

        if (System.currentTimeMillis() > (lastDebugTextUpdate + 250)) {
            Camera camera = Game.getInstance().getRenderer().getCamera();
            Vector3f position = camera.getPosition();

            lastDebugTextUpdate = System.currentTimeMillis();
            fpsText.setText("FPS: " + (int)Game.getInstance().fps);
            positionText.setText("XYZ: " + (int)position.x + " " + (int)position.y + " " + (int)position.z);
            cameraText.setText("Pitch: " + (int)camera.getPitch() + " Yaw: " + (int)camera.getYaw());

            int vertexCount = 0;
            World world = Game.getInstance().getWorld();
            for (Chunk chunk : world.getChunks().values()) {
                vertexCount += chunk.mesh.vertexCount;
            }
            for (Chunk chunk : world.getChunks().values()) {
                vertexCount += chunk.waterMesh.vertexCount;
            }
            vertexText.setText("Vertices: " + vertexCount);

            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
            memoryText.setText("Memory: " + heapMemoryUsage.getUsed() / (1024 * 1024) + "MB");
        }

        fpsText.render();
        positionText.render();
        cameraText.render();
        vertexText.render();
        memoryText.render();

        glUseProgram(0);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    }
}
