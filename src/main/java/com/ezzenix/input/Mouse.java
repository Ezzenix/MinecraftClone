package com.ezzenix.input;

import com.ezzenix.Client;
import com.ezzenix.engine.Input;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.gui.screen.Screen;
import com.ezzenix.item.Item;

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

		Input.mouseButton2Up(() -> {
			if (Client.getScreen() == null) return;
			Client.getScreen().mouse2Up(this.x, this.y);
		});

		Input.mouseButton2Down(() -> {
			if (Client.getScreen() == null) return;
			Client.getScreen().mouse2Down(this.x, this.y);
		});


		Input.mouseButton2Down(() -> {
			if (Client.isPaused()) return;
			if (Client.getScreen() != null) return;

			Item item = Client.getPlayer().getHeldItem();
			if (item != null) {
				item.use();
			}
		});

		Input.mouseButton1Down(() -> {
			if (Client.isPaused()) return;
			if (Client.getScreen() != null) return;
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

	public boolean isMouseButton1Down() {
		return glfwGetMouseButton(Client.getWindow().getHandle(), GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS;
	}
	public boolean isMouseButton2Down() {
		return glfwGetMouseButton(Client.getWindow().getHandle(), GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS;
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
