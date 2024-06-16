package com.ezzenix.client.input;

import com.ezzenix.Game;
import com.ezzenix.client.Client;
import com.ezzenix.client.gui.PauseScreen;
import com.ezzenix.client.gui.chat.ChatScreen;
import com.ezzenix.engine.Input;
import com.ezzenix.engine.core.Profiler;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.engine.physics.Physics;
import com.ezzenix.engine.physics.Raycast;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.BoundingBox;
import com.ezzenix.client.rendering.Camera;
import org.joml.Vector3f;
import org.joml.Vector3i;

import static org.lwjgl.glfw.GLFW.*;

public class Keyboard {
	long lastSpaceClicked = 0;

	public Keyboard() {
		Input.keyUp(GLFW_KEY_F5, () -> {
			if (Client.isPaused) return;
			
			Camera camera = Game.getInstance().getCamera();
			camera.thirdPerson = !camera.thirdPerson;
		});

		Input.keyUp(GLFW_KEY_F4, Profiler::dump);

		Input.keyUp(GLFW_KEY_KP_1, () -> {
			BlockPos blockPos = Game.getInstance().getPlayer().getBlockPos();
			if (blockPos.isValid()) {
				Game.getInstance().getWorld().setBlock(blockPos, BlockType.STONE);
			}
		});

		Input.keyUp(GLFW_KEY_KP_2, () -> {
			Game.getInstance().getPlayer().teleport(new Vector3f(0, 100, 0));
		});

		Input.keyDown(GLFW_KEY_ESCAPE, () -> {
			if (Client.getScreen() == null) {
				Client.setScreen(new PauseScreen());
			} else {
				Client.setScreen(null);
			}
		});

		Input.keyUp(GLFW_KEY_T, () -> {
			if (Client.isPaused) return;

			Client.setScreen(new ChatScreen());
		});

		Input.keyUp(GLFW_KEY_F11, () -> {
			Window window = Game.getInstance().getWindow();
			window.setFullscreen(!window.isFullscreen());
		});

		Input.keyDown(GLFW_KEY_SPACE, () -> {
			if (Client.isPaused) return;

			long now = System.currentTimeMillis();
			if (now - lastSpaceClicked < 300) {
				Game.getInstance().getPlayer().isFlying = !Game.getInstance().getPlayer().isFlying;
			}
			lastSpaceClicked = now;
		});
	}
}
