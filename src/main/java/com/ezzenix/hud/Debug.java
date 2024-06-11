package com.ezzenix.hud;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Mesh;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.game.entities.Player;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.rendering.Camera;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glVertexAttribPointer;

public class Debug {
	private static final Shader debugShader = new Shader("debugLine.vert", "debugLine.frag");

	private static final List<Float> vertexBatch = new ArrayList<>();

	public static void renderBatch() {
		try (
			MemoryStack stack = MemoryStack.stackPush()
		) {
			Camera camera = Game.getInstance().getCamera();

			debugShader.use();
			debugShader.setUniform("projectionMatrix", camera.getProjectionMatrix());
			debugShader.setUniform("viewMatrix", camera.getViewMatrix());

			FloatBuffer buffer = Mesh.convertToBuffer(vertexBatch, stack);
			Mesh mesh = new Mesh(buffer, vertexBatch.size() / 6, GL_LINES);
			vertexBatch.clear();

			glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
			glEnableVertexAttribArray(0);

			glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
			glEnableVertexAttribArray(1);

			glLineWidth(3f);
			glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
			glEnable(GL_LINE_SMOOTH);
			mesh.render();
			glDepthFunc(GL_LESS);
			mesh.dispose();
			glLineWidth(1f);
		}
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

		drawLine(new Vector3f(minX, minY, minZ), new Vector3f(maxX, minY, minZ), color);
		drawLine(new Vector3f(minX, minY, minZ), new Vector3f(minX, minY, maxZ), color);
		drawLine(new Vector3f(maxX, minY, minZ), new Vector3f(maxX, minY, maxZ), color);
		drawLine(new Vector3f(minX, minY, maxZ), new Vector3f(maxX, minY, maxZ), color);

		drawLine(new Vector3f(minX, minY, minZ), new Vector3f(minX, maxY, minZ), color);
		drawLine(new Vector3f(minX, minY, maxZ), new Vector3f(minX, maxY, maxZ), color);
		drawLine(new Vector3f(maxX, minY, maxZ), new Vector3f(maxX, maxY, maxZ), color);
		drawLine(new Vector3f(maxX, minY, minZ), new Vector3f(maxX, maxY, minZ), color);

		drawLine(new Vector3f(minX, maxY, minZ), new Vector3f(maxX, maxY, minZ), color);
		drawLine(new Vector3f(minX, maxY, minZ), new Vector3f(minX, maxY, maxZ), color);
		drawLine(new Vector3f(maxX, maxY, minZ), new Vector3f(maxX, maxY, maxZ), color);
		drawLine(new Vector3f(minX, maxY, maxZ), new Vector3f(maxX, maxY, maxZ), color);
	}

	public static void highlightVoxel(Vector3f voxel, Vector3f color) {
		drawBox(voxel, new Vector3f(voxel).add(1, 1, 1), color);
	}

	public static void highlightVoxel(Vector3f voxel) {
		highlightVoxel(voxel, new Vector3f(1, 1, 1));
	}

	public static void drawChunkBorders() {
		Player player = Game.getInstance().getPlayer();
		ChunkPos chunkPos = ChunkPos.from(player.getBlockPos());

		int viewDistance = 1;

		for (int x = chunkPos.x * Chunk.CHUNK_WIDTH - Chunk.CHUNK_WIDTH * viewDistance; x <= chunkPos.x * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH * viewDistance; x += Chunk.CHUNK_WIDTH) {
			for (int z = chunkPos.z * Chunk.CHUNK_WIDTH - Chunk.CHUNK_WIDTH * viewDistance; z <= chunkPos.z * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH * viewDistance; z += Chunk.CHUNK_WIDTH) {
				Vector3f color = new Vector3f((float) 244 / 255, (float) 255 / 255, (float) 128 / 255);
				drawBox(new Vector3f(x, 0, z), new Vector3f(x + Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, z + Chunk.CHUNK_WIDTH), color);
			}
		}
	}

	public static void print(String string) {
		if (glfwGetKey(Game.getInstance().getWindow().getId(), GLFW_KEY_U) == GLFW_PRESS) {
			System.out.println("[Debug] " + string);
		}
	}
}
