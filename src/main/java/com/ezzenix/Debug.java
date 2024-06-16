package com.ezzenix;

import com.ezzenix.client.gui.DebugHud;
import com.ezzenix.engine.Input;
import com.ezzenix.game.entities.Player;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.hud.LineRenderer;
import com.ezzenix.math.ChunkPos;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

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
			Game.getInstance().getRenderer().getWorldRenderer().reloadAllChunks();
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
			if (wireframeMode) {
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
				glDisable(GL_DEPTH_TEST);
				glDisable(GL_CULL_FACE);
			} else {
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
				glEnable(GL_DEPTH_TEST);
				glEnable(GL_CULL_FACE);
			}
		});
	}

	public static void renderChunkBorders() {
		Player player = Game.getInstance().getPlayer();
		ChunkPos chunkPos = ChunkPos.from(player.getBlockPos());

		int viewDistance = 1;

		for (int x = chunkPos.x * Chunk.CHUNK_WIDTH - Chunk.CHUNK_WIDTH * viewDistance; x <= chunkPos.x * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH * viewDistance; x += Chunk.CHUNK_WIDTH) {
			for (int z = chunkPos.z * Chunk.CHUNK_WIDTH - Chunk.CHUNK_WIDTH * viewDistance; z <= chunkPos.z * Chunk.CHUNK_WIDTH + Chunk.CHUNK_WIDTH * viewDistance; z += Chunk.CHUNK_WIDTH) {
				Vector3f color = new Vector3f((float) 244 / 255, (float) 255 / 255, (float) 128 / 255);
				LineRenderer.drawBox(new Vector3f(x, 0, z), new Vector3f(x + Chunk.CHUNK_WIDTH, Chunk.CHUNK_HEIGHT, z + Chunk.CHUNK_WIDTH), color);
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
