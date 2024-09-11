package com.ezzenix.client.input;

import com.ezzenix.blocks.BlockType;
import com.ezzenix.client.Client;
import com.ezzenix.client.gui.screen.Screen;
import com.ezzenix.client.rendering.particle.BlockBreakParticle;
import com.ezzenix.engine.Input;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.item.Item;
import com.ezzenix.math.BlockPos;
import com.ezzenix.physics.Raycast;
import org.joml.Math;

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

		glfwSetScrollCallback(window, (long w, double x, double y) -> {
			this.scrolled(x, y);
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

			Item item = Client.getPlayer().getHeldItem();
			if (item != null) {
				item.use();
			}
		});

		Input.mouseButton1Down(() -> {
			if (Client.isPaused()) return;
			Raycast result = Client.getPlayer().raycast();
			if (result != null) {
				BlockPos blockPos = result.blockPos;
				Client.getWorld().setBlock(blockPos, BlockType.AIR);

				float minX = blockPos.x;
				float maxX = blockPos.x + 1;
				float minY = blockPos.y;
				float maxY = blockPos.y + 1;
				float minZ = blockPos.z;
				float maxZ = blockPos.z + 1;

				float step = 1f / 4f;

				float centerX = minX + 0.5f;
				float centerZ = minZ + 0.5f;

				for (float x = minX + step; x < maxX; x += step) {
					for (float y = minY + step; y < maxY; y += step) {
						for (float z = minZ + step; z < maxZ; z += step) {
							float vx = x - centerX;
							float vy = 2f;
							float vz = z - centerZ;

							float spread = 0.25f;
							float px = (x + ((float) Math.random() * 2 - 1) * spread);
							float py = (y + ((float) Math.random() * 2 - 1) * spread);
							float pz = (z + ((float) Math.random() * 2 - 1) * spread);

							new BlockBreakParticle(px, py, pz, vx, vy, vz);
						}
					}
				}
			}
		});
	}

	private void scrolled(double x, double y) {
		if (Client.getScreen() != null) {
			Client.getScreen().scrolled(x, y);
		}
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
