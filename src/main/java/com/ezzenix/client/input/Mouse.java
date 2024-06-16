package com.ezzenix.client.input;

import com.ezzenix.Game;
import com.ezzenix.client.Client;
import com.ezzenix.engine.Input;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.engine.physics.Physics;
import com.ezzenix.engine.physics.Raycast;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.BoundingBox;
import org.joml.Vector3i;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;

public class Mouse {

	private boolean isFirstUpdate = true;
	private int x;
	private int y;
	private int lastX;
	private int lastY;

	private boolean cursorLocked = false;

	public Mouse() {
		long window = Game.getInstance().getWindow().getHandle();

		lockCursor();

		glfwSetCursorPosCallback(window, (windowHandle, xpos, ypos) -> {
			cursorMoved((int) xpos, (int) ypos);
		});

		Input.mouseButton2Down(() -> {
			if (Client.isPaused) return;
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
			if (Client.isPaused) return;
			Raycast result = Game.getInstance().getPlayer().raycast();
			if (result != null) {
				Game.getInstance().getWorld().setBlock(result.blockPos, BlockType.AIR);
			}
		});
	}

	private void cursorMoved(int x, int y) {
		this.x = x;
		this.y = y;

		if (Client.getScreen() != null) return;

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
		Game.getInstance().getPlayer().addYaw(deltaX * sensitivity * -1);
		Game.getInstance().getPlayer().addPitch(deltaY * sensitivity * -1);
	}

	public void resetDelta() {
		isFirstUpdate = true;
	}

	private void changeCursorMode(int mode) {
		Window window = Game.getInstance().getWindow();
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
