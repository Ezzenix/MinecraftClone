package com.ezzenix.client;

import com.ezzenix.Game;
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
		Input.mouseButton2Down(() -> {
			Raycast result = Game.getInstance().getPlayer().raycast();
			if (result != null && result.hitDirection != null) {
				Vector3i faceNormal = result.hitDirection.getNormal();
				BlockPos blockPos = result.blockPos.add(faceNormal.x, faceNormal.y, faceNormal.z);
				if (blockPos.isValid()) {

					BoundingBox blockBoundingBox = Physics.getBlockBoundingBox(blockPos);
					for (Entity entity : Game.getInstance().getEntities()) {
						if (entity.boundingBox.getIntersection(blockBoundingBox).length() > 0) return;
					}

					Game.getInstance().getWorld().setBlock(blockPos, BlockType.GRASS_BLOCK);
				}
			}
		});

		Input.mouseButton1Down(() -> {
			Raycast result = Game.getInstance().getPlayer().raycast();
			if (result != null) {
				Game.getInstance().getWorld().setBlock(result.blockPos, BlockType.AIR);
			}
		});

		Input.keyUp(GLFW_KEY_F5, () -> {
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
			Client.setScreen(new ChatScreen());
		});

		Input.keyUp(GLFW_KEY_F11, () -> {
			Window window = Game.getInstance().getWindow();
			window.setFullscreen(!window.isFullscreen());
		});

		Input.keyUp(GLFW_KEY_SPACE, () -> {
			long now = System.currentTimeMillis();
			if (now - lastSpaceClicked < 300) {
				Game.getInstance().getPlayer().isFlying = !Game.getInstance().getPlayer().isFlying;
			}
			lastSpaceClicked = now;
		});
	}
}
