package com.ezzenix.hud;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.engine.scheduler.Scheduler;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.game.entities.Player;
import com.ezzenix.game.world.World;
import com.ezzenix.hud.font.FontRenderer;
import com.ezzenix.hud.font.TextComponent;
import org.joml.Vector3f;

import java.awt.Font;
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
    TextComponent chunkPosText;
    TextComponent isChunkAtPlayerText;
    TextComponent crosshair;

    Shader textShader;

    public Hud() {
        this.textShader = new Shader("text.vert", "text.frag");
        this.fontRenderer = new FontRenderer(new Font("Arial", Font.PLAIN, 18));

        fpsText = new TextComponent(fontRenderer, "", 6, 6);
        positionText = new TextComponent(fontRenderer, "", 6, 6 + 18);
        cameraText = new TextComponent(fontRenderer, "", 6, 6 + 18 * 2);
        vertexText = new TextComponent(fontRenderer, "", 6, 6 + 18 * 3);
        memoryText = new TextComponent(fontRenderer, "", 6, 6 + 18 * 4);
        chunkPosText = new TextComponent(fontRenderer, "", 6, 6 + 18 * 5);
        isChunkAtPlayerText = new TextComponent(fontRenderer, "", 6, 6 + 18 * 6);

        crosshair = new TextComponent(fontRenderer, "+", Game.getInstance().getWindow().getWidth()/2 - fontRenderer.getGlyph('+').width/2, Game.getInstance().getWindow().getHeight()/2 - fontRenderer.getGlyph('+').height/2);

        Scheduler.runPeriodic(() -> {
            Player player = Game.getInstance().getPlayer();
            Vector3f position = player.getPosition();
            BlockPos blockPos = BlockPos.from(position);

            fpsText.setText("FPS: " + (int) Scheduler.getFps());
            positionText.setText("XYZ: " + blockPos.x + " " + blockPos.y + " " + blockPos.z);
            cameraText.setText(getDirectionString(player.getYaw()) + " (" + (int) player.getYaw() + " / " + (int) player.getPitch() + ")");
            chunkPosText.setText(ChunkPos.from(position).toString());

            Chunk c = player.getWorld().getChunk(player.getBlockPos());
            if (c != null) {
                isChunkAtPlayerText.setText("Chunk: Yes " + c.blockCount + " " + c.isGenerating + " " + c.hasGenerated);
            } else {
                isChunkAtPlayerText.setText("Chunk: No");
            }

            int vertexCount = 0;
            World world = Game.getInstance().getWorld();
            for (Chunk chunk : world.getChunkMap().values()) {
                if (chunk.getChunkMesh().getBlockMesh() != null) vertexCount += chunk.getChunkMesh().getBlockMesh().vertexCount;
                //if (chunk.getChunkMesh() != null) vertexCount += chunk.waterMesh.vertexCount;
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
            return "WEST";
        } else if (yaw >= -45 && yaw <= 45) {
            return "SOUTH";
        } else {
            return "EAST";
        }
    }

    public void render() {
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
        chunkPosText.render();
        isChunkAtPlayerText.render();
        crosshair.render();

        glUseProgram(0);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    }
}
