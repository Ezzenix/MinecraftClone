package com.ezzenix.client.gui;

import com.ezzenix.Game;
import com.ezzenix.client.Client;
import com.ezzenix.client.gui.library.Gui;
import com.ezzenix.client.gui.library.Screen;
import com.ezzenix.client.gui.library.UDim2;
import com.ezzenix.client.gui.library.components.GuiButton;
import com.ezzenix.client.gui.library.components.GuiFrame;
import com.ezzenix.client.gui.library.components.GuiText;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class PauseScreen extends Screen {
	public PauseScreen() {
		super("Pause Menu");
	}

	public void init() {
		Client.isPaused = true;

		GuiFrame background = new GuiFrame();
		background.size = UDim2.fromScale(1, 1);
		background.color = new Vector3f(0.1f, 0.1f, 0.1f);
		background.transparency = 0.3f;
		background.screen = this;

		GuiText text = new GuiText();
		text.text = "Game is paused";
		text.anchorPoint = new Vector2f(0.5f, 0.5f);
		text.textAlign = Gui.TextAlign.Center;
		text.position = new UDim2(0.5f, 0, 0.4f, 0);
		text.screen = this;

		GuiButton button = new GuiButton(() -> {
			glfwSetWindowShouldClose(Game.getInstance().getWindow().getHandle(), true);
		});
		button.text = "Leave game";
		button.anchorPoint = new Vector2f(0.5f, 0.5f);
		button.position = new UDim2(0.5f, 0, 0.6f, 0);
		button.size = UDim2.fromScale(0.4f, 0.05f);
		button.sizeConstraint = Gui.SizeConstraint.YY;
		button.screen = this;
	}

	public void remove() {
		Client.isPaused = false;
	}
}
