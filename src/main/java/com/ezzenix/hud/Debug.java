package com.ezzenix.hud;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.game.world.chunk.Chunk;
import com.ezzenix.game.entities.Player;
import com.ezzenix.rendering.Camera;
import com.ezzenix.engine.opengl.Mesh;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_LINES;
import static org.lwjgl.opengl.GL30.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glVertexAttribPointer;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Debug {
    private static final Shader debugShader = new Shader("debugLine.vert", "debugLine.frag");

    private static final List<Float> vertexBatch = new ArrayList<>();

    public static void renderBatch() {
        Camera camera = Game.getInstance().getCamera();

        debugShader.use();
        debugShader.uploadMat4f("projectionMatrix", camera.getProjectionMatrix());
        debugShader.uploadMat4f("viewMatrix", camera.getViewMatrix());

        FloatBuffer buffer = Mesh.convertToBuffer(vertexBatch);
        Mesh mesh = new Mesh(buffer, vertexBatch.size() / 6, GL_LINES);
        vertexBatch.clear();

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        mesh.render();
        mesh.dispose();

        MemoryUtil.memFree(buffer);
    }

    public static void drawLine(Vector3f pos1, Vector3f pos2, Vector3f color) {
        vertexBatch.add(pos1.x);
        vertexBatch.add(pos1.y);
        vertexBatch.add(pos1.z);
        vertexBatch.add(color.x);
        vertexBatch.add(color.y);
        vertexBatch.add(color.z);

        vertexBatch.add(pos2.x);
        vertexBatch.add(pos2.y);
        vertexBatch.add(pos2.z);
        vertexBatch.add(color.x);
        vertexBatch.add(color.y);
        vertexBatch.add(color.z);
    }

    public static void drawLine(Vector3f pos1, Vector3f pos2) {
        drawLine(pos1, pos2, new Vector3f(1, 1, 1));
    }

    public static void drawBox(Vector3f corner1, Vector3f corner2, Vector3f color) {
        float minX = Math.min(corner1.x, corner2.x);
        float maxX = Math.max(corner1.x, corner2.x);
        float minY = Math.min(corner1.y, corner2.y);
        float maxY = Math.max(corner1.y, corner2.y);
        float minZ = Math.min(corner1.z, corner2.z);
        float maxZ = Math.max(corner1.z, corner2.z);

        Debug.drawLine(new Vector3f(minX, minY, minZ), new Vector3f(maxX, minY, minZ), color);
        Debug.drawLine(new Vector3f(minX, minY, minZ), new Vector3f(minX, minY, maxZ), color);
        Debug.drawLine(new Vector3f(maxX, minY, minZ), new Vector3f(maxX, minY, maxZ), color);
        Debug.drawLine(new Vector3f(minX, minY, maxZ), new Vector3f(maxX, minY, maxZ), color);

        Debug.drawLine(new Vector3f(minX, minY, minZ), new Vector3f(minX, maxY, minZ), color);
        Debug.drawLine(new Vector3f(minX, minY, maxZ), new Vector3f(minX, maxY, maxZ), color);
        Debug.drawLine(new Vector3f(maxX, minY, maxZ), new Vector3f(maxX, maxY, maxZ), color);
        Debug.drawLine(new Vector3f(maxX, minY, minZ), new Vector3f(maxX, maxY, minZ), color);

        Debug.drawLine(new Vector3f(minX, maxY, minZ), new Vector3f(maxX, maxY, minZ), color);
        Debug.drawLine(new Vector3f(minX, maxY, minZ), new Vector3f(minX, maxY, maxZ), color);
        Debug.drawLine(new Vector3f(maxX, maxY, minZ), new Vector3f(maxX, maxY, maxZ), color);
        Debug.drawLine(new Vector3f(minX, maxY, maxZ), new Vector3f(maxX, maxY, maxZ), color);
    }

    public static void highlightVoxel(Vector3f voxel, Vector3f color) {
        Debug.drawBox(voxel, new Vector3f(voxel).add(1, 1, 1), color);
    }

    public static void highlightVoxel(Vector3f voxel) {
        highlightVoxel(voxel, new Vector3f(1, 1, 1));
    }

    public static void drawChunkBorders() {
        Player player = Game.getInstance().getPlayer();
        int chunkX = (player.getBlockPos().x >> 5) * Chunk.CHUNK_SIZE;
        int chunkY = (player.getBlockPos().y >> 5) * Chunk.CHUNK_SIZE;
        int chunkZ = (player.getBlockPos().z >> 5) * Chunk.CHUNK_SIZE;
        int distance = 1;
        for (int x = chunkX - Chunk.CHUNK_SIZE*distance; x <= chunkX + Chunk.CHUNK_SIZE*distance; x += Chunk.CHUNK_SIZE) {
            for (int y = chunkY - Chunk.CHUNK_SIZE*distance; y <= chunkY + Chunk.CHUNK_SIZE*distance; y += Chunk.CHUNK_SIZE) {
                for (int z = chunkZ - Chunk.CHUNK_SIZE*distance; z <= chunkZ + Chunk.CHUNK_SIZE*distance; z += Chunk.CHUNK_SIZE) {
                    drawBox(new Vector3f(x, y, z), new Vector3f(x + Chunk.CHUNK_SIZE, y + Chunk.CHUNK_SIZE, z + Chunk.CHUNK_SIZE), new Vector3f((float) 244 /255, (float) 255 /255, (float) 128 /255));
                }
            }
        }
    }
}
