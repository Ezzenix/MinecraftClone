package com.ezzenix;

import com.ezzenix.engine.Input;
import com.ezzenix.entities.player.Player;
import com.ezzenix.gui.Color;
import com.ezzenix.gui.DebugHud;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.rendering.LineRenderer;
import com.ezzenix.rendering.Renderer;
import com.ezzenix.world.chunk.Chunk;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Debug {
	public static boolean wireframeMode = false;
	private static boolean showChunkBorders = false;

	private static final int TOGGLE_KEY = GLFW_KEY_F3;
	private static boolean usedActionWhileHoldingToggleKey = false;

	static {
		Input.keyDown(TOGGLE_KEY, () -> {
			usedActionWhileHoldingToggleKey = false;
		});
		Input.keyUp(TOGGLE_KEY, () -> {
			if (!usedActionWhileHoldingToggleKey) {
				DebugHud.setEnabled(!DebugHud.isEnabled());
			}
		});

		Input.keyDown(GLFW_KEY_A, () -> {
			if (!Input.getKey(TOGGLE_KEY)) return;

			usedActionWhileHoldingToggleKey = true;
			Renderer.getWorldRenderer().reloadAllChunks();
		});
		Input.keyDown(GLFW_KEY_G, () -> {
			if (!Input.getKey(TOGGLE_KEY)) return;

			usedActionWhileHoldingToggleKey = true;
			showChunkBorders = !showChunkBorders;
		});
		Input.keyDown(GLFW_KEY_Z, () -> {
			if (!Input.getKey(TOGGLE_KEY)) return;

			usedActionWhileHoldingToggleKey = true;
			wireframeMode = !wireframeMode;
		});
	}

	public static void renderChunkBorders() {
		Player player = Client.getPlayer();
		ChunkPos chunkPos = new ChunkPos(player.getBlockPos());

		int viewDistance = 1;

		int color = Color.pack(244, 255, 128, 255);
		for (int x = chunkPos.x * Chunk.CHUNK_WIDTH - Chunk.CHUNK_WIDTH * viewDistance; x <= chunkPos.x * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH * viewDistance; x += Chunk.CHUNK_WIDTH) {
			for (int z = chunkPos.z * Chunk.CHUNK_WIDTH - Chunk.CHUNK_WIDTH * viewDistance; z <= chunkPos.z * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH * viewDistance; z += Chunk.CHUNK_WIDTH) {
				LineRenderer.drawBox(new Vector3f(x, 0, z), new Vector3f(x + Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT + 1, z + Chunk.CHUNK_WIDTH), color);
			}
		}
	}

	public static void render() {
		if (showChunkBorders) {
			renderChunkBorders();
		}

		DebugHud.render();
	}
}
