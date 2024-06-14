package com.ezzenix.client;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Window;

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
