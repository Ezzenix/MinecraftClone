package com.ezzenix.engine;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Window;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Input {
	private static Window window;
	private static final List<InputEvent> inputEvents = new ArrayList<>();

	private static class InputEvent {
		int input;
		int action;
		Runnable runnable;

		public InputEvent(int input, int action, Runnable runnable) {
			this.input = input;
			this.action = action;
			this.runnable = runnable;
		}
	}

	private static void runEvents(int input, int action) {
		for (InputEvent inputEvent : inputEvents) {
			if (inputEvent.input == input && (inputEvent.action == action || (inputEvent.action == GLFW_REPEAT && action == GLFW_PRESS))) {
				inputEvent.runnable.run();
			}
		}
	}

	public static void initialize(Window w) {
		window = w;

		glfwSetKeyCallback(Game.getInstance().getWindow().getHandle(), (window, key, scancode, action, mods) -> {
			runEvents(key, action);
		});
		glfwSetMouseButtonCallback(Game.getInstance().getWindow().getHandle(), (window, button, action, mods) -> {
			runEvents(button, action);
		});
	}

	public static boolean getKey(int key) {
		return glfwGetKey(window.getHandle(), key) == GLFW_PRESS;
	}

	public static boolean getMouseButton1() {
		return glfwGetMouseButton(window.getHandle(), GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS;
	}

	public static boolean getMouseButton2() {
		return glfwGetMouseButton(window.getHandle(), GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS;
	}

	public static void keyDown(int key, Runnable runnable) {
		inputEvents.add(new InputEvent(key, GLFW_PRESS, runnable));
	}

	public static void keyDownAndHold(int key, Runnable runnable) {
		inputEvents.add(new InputEvent(key, GLFW_REPEAT, runnable));
	}

	public static void keyUp(int key, Runnable runnable) {
		inputEvents.add(new InputEvent(key, GLFW_RELEASE, runnable));
	}

	public static void mouseButton1Down(Runnable runnable) {
		inputEvents.add(new InputEvent(GLFW_MOUSE_BUTTON_LEFT, GLFW_PRESS, runnable));
	}

	public static void mouseButton1Up(Runnable runnable) {
		inputEvents.add(new InputEvent(GLFW_MOUSE_BUTTON_LEFT, GLFW_RELEASE, runnable));
	}

	public static void mouseButton2Down(Runnable runnable) {
		inputEvents.add(new InputEvent(GLFW_MOUSE_BUTTON_RIGHT, GLFW_PRESS, runnable));
	}

	public static void mouseButton2Up(Runnable runnable) {
		inputEvents.add(new InputEvent(GLFW_MOUSE_BUTTON_RIGHT, GLFW_RELEASE, runnable));
	}
}