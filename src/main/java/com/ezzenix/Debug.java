package com.ezzenix;

import com.ezzenix.engine.Input;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.engine.gui.UDim2;
import com.ezzenix.engine.gui.components.GuiFrame;
import com.ezzenix.engine.gui.components.GuiText;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.game.entities.Player;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.game.world.World;
import com.ezzenix.hud.LineRenderer;
import com.ezzenix.engine.gui.FontRenderer;
import com.ezzenix.hud.font.TextComponent;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.ChunkPos;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Debug {
	public static boolean debugHudEnabled = true;

	public static boolean wireframeMode = false;
	private static boolean showChunkBorders = false;

	private static final int TOGGLE_KEY = GLFW_KEY_F3;
	private static boolean usedActionWhileHoldingToggleKey = false;

	static FontRenderer fontRenderer;
	static TextComponent fpsText;
	static TextComponent positionText;
	static TextComponent cameraText;
	static TextComponent vertexText;
	static TextComponent memoryText;
	static TextComponent chunkPosText;
	static TextComponent isChunkAtPlayerText;
	static TextComponent crosshair;

	static TextComponent isGroundedText;
	static TextComponent velocityText;
	static TextComponent absolutePositionText;

	static Shader textShader;

	static {
		Input.keyDown(TOGGLE_KEY, () -> {
			usedActionWhileHoldingToggleKey = false;
		});
		Input.keyUp(TOGGLE_KEY, () -> {
			if (!usedActionWhileHoldingToggleKey) {
				debugHudEnabled = !debugHudEnabled;
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


		textShader = new Shader("gui/text.vert", "gui/text.frag");
		fontRenderer = new FontRenderer(new Font("Arial", Font.PLAIN, 18));

		fpsText = new TextComponent(fontRenderer, "", 6, 0);
		positionText = new TextComponent(fontRenderer, "", 6, 18);
		cameraText = new TextComponent(fontRenderer, "", 6, 18 * 2);
		vertexText = new TextComponent(fontRenderer, "", 6, 18 * 3);
		memoryText = new TextComponent(fontRenderer, "", 6, 18 * 4);
		chunkPosText = new TextComponent(fontRenderer, "", 6, 18 * 5);
		isChunkAtPlayerText = new TextComponent(fontRenderer, "", 6, 18 * 6);

		isGroundedText = new TextComponent(fontRenderer, "", 300, 18 * 2);
		velocityText = new TextComponent(fontRenderer, "", 300, 18);
		absolutePositionText = new TextComponent(fontRenderer, "", 300, 0);

		crosshair = new TextComponent(fontRenderer, "+", Game.getInstance().getWindow().getWidth() / 2 - fontRenderer.getGlyph('+').width / 2, Game.getInstance().getWindow().getHeight() / 2 - fontRenderer.getGlyph('+').height / 2);

		Scheduler.setInterval(() -> {
			Player player = Game.getInstance().getPlayer();
			Vector3f position = player.getPosition();
			BlockPos blockPos = BlockPos.from(position);

			fpsText.setText("FPS: " + (int) Scheduler.getFps());
			positionText.setText("XYZ: " + blockPos.x + " " + blockPos.y + " " + blockPos.z);
			cameraText.setText(getDirectionString(player.getYaw()) + " (" + (int) player.getYaw() + " / " + (int) player.getPitch() + ")");
			chunkPosText.setText(ChunkPos.from(position).toString());

			isGroundedText.setText("Grounded: " + player.isGrounded);
			velocityText.setText(player.getVelocity().x + " " + player.getVelocity().y + " " + player.getVelocity().z);
			absolutePositionText.setText(position.x + " " + position.y + " " + position.z);

			Chunk c = player.getWorld().getChunk(player.getBlockPos());
			if (c != null) {
				isChunkAtPlayerText.setText("Chunk: Yes " + c.blockCount);
			} else {
				isChunkAtPlayerText.setText("Chunk: No");
			}

			int vertexCount = 0;
			World world = Game.getInstance().getWorld();
			for (Chunk chunk : world.getChunks().values()) {
				if (chunk.getChunkMesh().getBlockMesh() != null)
					vertexCount += chunk.getChunkMesh().getBlockMesh().vertexCount;
				if (chunk.getChunkMesh().getWaterMesh() != null)
					vertexCount += chunk.getChunkMesh().getWaterMesh().vertexCount;
			}
			vertexText.setText("Vertices: " + vertexCount);

			MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
			MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
			memoryText.setText("Memory: " + heapMemoryUsage.getUsed() / (1024 * 1024) + " MB");
		}, 100);
	}

	private static String getDirectionString(float yaw) {
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

		if (debugHudEnabled) {
			glDisable(GL_DEPTH_TEST);
			glDisable(GL_CULL_FACE);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			fontRenderer.getAtlasTexture().bind();
			textShader.bind();

			fpsText.render();
			positionText.render();

			isGroundedText.render();
			velocityText.render();
			absolutePositionText.render();

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
}
