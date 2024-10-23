package com.ezzenix.entities.player;

import com.ezzenix.Client;
import com.ezzenix.engine.Input;

import static org.lwjgl.glfw.GLFW.*;

public class MovementInput {
	public boolean pressingForward = false;
	public boolean pressingBack = false;
	public boolean pressingLeft = false;
	public boolean pressingRight = false;
	public boolean jumping = false;
	public boolean sneaking = false;
	public boolean sprinting = false;

	public float movementForward = 0f;
	public float movementSideways = 0f;

	public MovementInput() {
	}

	public void update() {
		boolean hasFocusedTextField = Client.focusedTextField != null;

		pressingForward = Input.getKey(GLFW_KEY_W) && !hasFocusedTextField;
		pressingBack = Input.getKey(GLFW_KEY_S) && !hasFocusedTextField;
		pressingLeft = Input.getKey(GLFW_KEY_A) && !hasFocusedTextField;
		pressingRight = Input.getKey(GLFW_KEY_D) && !hasFocusedTextField;

		jumping = Input.getKey(GLFW_KEY_SPACE) && !hasFocusedTextField;
		sneaking = Input.getKey(GLFW_KEY_LEFT_CONTROL) && !hasFocusedTextField;
		sprinting = Input.getKey(GLFW_KEY_LEFT_SHIFT) && !hasFocusedTextField;

		movementForward = pressingForward == pressingBack ? 0 : (pressingForward ? 1.0f : -1.0f);
		movementSideways = pressingRight == pressingLeft ? 0 : (pressingRight ? 1.0f : -1.0f);
	}
}
