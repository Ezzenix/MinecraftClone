package com.ezzenix.hud;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.scheduler.Scheduler;
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

    public Hud() {
        this.textShader = new Shader("text.vert", "text.frag");
        this.fontRenderer = new FontRenderer(new Font("Arial", Font.PLAIN, 18));

        fpsText = new TextComponent(fontRenderer, "", 6, 6);
        positionText = new TextComponent(fontRenderer, "", 6, 6 + 18);
        cameraText = new TextComponent(fontRenderer, "", 6, 6 + 18 * 2);
        vertexText = new TextComponent(fontRenderer, "", 6, 6 + 18 * 3);
        memoryText = new TextComponent(fontRenderer, "", 6, 6 + 18 * 4);

        Scheduler.runPeriodic(() -> {
            Camera camera = Game.getInstance().getCamera();
            Vector3f position = camera.getPosition();

            fpsText.setText("FPS: " + (int) Scheduler.getFps());
            positionText.setText("XYZ: " + (int) position.x + " " + (int) position.y + " " + (int) position.z);
            cameraText.setText(getDirectionString(camera.getYaw()) + " (" + (int) camera.getYaw() + " / " + (int) camera.getPitch() + ")");

            int vertexCount = 0;
            World world = Game.getInstance().getWorld();
            for (Chunk chunk : world.getChunks().values()) {
                if (chunk.mesh != null) vertexCount += chunk.mesh.vertexCount;
                if (chunk.waterMesh != null) vertexCount += chunk.waterMesh.vertexCount;
            }
            vertexText.setText("Vertices: " + vertexCount);

            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
            memoryText.setText("Memory: " + heapMemoryUsage.getUsed() / (1024 * 1024) + " MB");
        }, 50);
    }

    private String getDirectionString(float yaw) {
        if (yaw < -135 || yaw >= 135) {
            return "NORTH";
        } else if (yaw > -135 && yaw < -45) {
            return "EAST";
        } else if (yaw >= -45 && yaw <= 45) {
            return "SOUTH";
        } else {
            return "WEST";
        }
    }

    public void render(long window) {
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        this.fontRenderer.getAtlasTexture().bind();
        this.textShader.use();

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
