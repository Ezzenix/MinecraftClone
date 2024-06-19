package com.ezzenix.client.input;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.screen.Screen;
import com.ezzenix.engine.Input;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.physics.Physics;
import com.ezzenix.physics.Raycast;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.BoundingBox;
import org.joml.Vector3i;

import static org.lwjgl.glfw.GLFW.*;

public class Mouse {

	private boolean isFirstUpdate = true;
	private int x;
	private int y;
	private int lastX;
	private int lastY;

	private boolean cursorLocked = false;

	public Mouse() {
		long window = Client.getWindow().getHandle();

		lockCursor();

		glfwSetCursorPosCallback(window, (windowHandle, xpos, ypos) -> {
			cursorMoved((int) xpos, (int) ypos);
		});

		Input.mouseButton1Up(() -> {
			if (Client.getScreen() == null) return;
			Client.getScreen().mouseUp();
			Client.getScreen().mouseClicked(this.x, this.y);
		});

		Input.mouseButton1Down(() -> {
			if (Client.getScreen() == null) return;
			Client.getScreen().mouseDown(this.x, this.y);
		});


		Input.mouseButton2Down(() -> {
			if (Client.isPaused()) return;
			Raycast result = Client.getPlayer().raycast();
			if (result != null && result.hitDirection != null) {
				Vector3i faceNormal = result.hitDirection.getNormal();
				BlockPos blockPos = result.blockPos.add(faceNormal.x, faceNormal.y, faceNormal.z);
				if (blockPos.isValid()) {

					BoundingBox blockBoundingBox = Physics.getBlockBoundingBox(blockPos);
					for (Entity entity : Client.getWorld().getEntities()) {
						if (entity.boundingBox.getIntersection(blockBoundingBox).length() > 0) return;
					}

					Client.getWorld().setBlock(blockPos, BlockType.GRASS_BLOCK);
				}
			}
		});

		Input.mouseButton1Down(() -> {
			if (Client.isPaused()) return;
			Raycast result = Client.getPlayer().raycast();
			if (result != null) {
				Client.getWorld().setBlock(result.blockPos, BlockType.AIR);
			}
		});
	}

	private void cursorMoved(int x, int y) {
		this.x = x;
		this.y = y;

		if (Client.getScreen() != null) {
			Screen screen = Client.getScreen();
			screen.mouseMoved(x, y);
			return;
		}

		if (isFirstUpdate) {
			lastX = x;
			lastY = y;
			isFirstUpdate = false;
		}

		int deltaX = x - lastX;
		int deltaY = y - lastY;

		lastX = x;
		lastY = y;

		float sensitivity = 0.35f;
		Client.getPlayer().addYaw(deltaX * sensitivity * -1);
		Client.getPlayer().addPitch(deltaY * sensitivity * -1);
	}

	public void resetDelta() {
		isFirstUpdate = true;
	}

	private void changeCursorMode(int mode) {
		Window window = Client.getWindow();
		x = window.getWidth() / 2;
		y = window.getHeight() / 2;
		glfwSetCursorPos(window.getHandle(), x, y);
		glfwSetInputMode(window.getHandle(), GLFW_CURSOR, mode);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isCursorLocked() {
		return this.cursorLocked;
	}

	public void lockCursor() {
		if (this.cursorLocked) return;
		this.cursorLocked = true;

		this.isFirstUpdate = true;

		changeCursorMode(GLFW_CURSOR_DISABLED);
	}

	public void unlockCursor() {
		if (!this.cursorLocked) return;
		this.cursorLocked = false;

		changeCursorMode(GLFW_CURSOR_NORMAL);
	}
}
