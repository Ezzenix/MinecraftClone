package com.ezzenix.client.gui.screen;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.Color;
import com.ezzenix.client.gui.Gui;
import com.ezzenix.client.gui.widgets.ButtonWidget;
import com.ezzenix.client.options.OptionsScreen;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class PauseScreen extends Screen {
	public PauseScreen() {
		super("Pause Menu");
	}

	@Override
	public void init(int width, int height) {
		int buttonWidth = 400;

		this.addWidget(new ButtonWidget("Resume game", width / 2 - buttonWidth / 2, height / 2 - 60, buttonWidth, 40, () -> {
			Client.setScreen(null);
		}));

		this.addWidget(new ButtonWidget("Options", width / 2 - buttonWidth / 2, height / 2, buttonWidth, 40, () -> {
			Client.setScreen(new OptionsScreen(this));
		}));

		this.addWidget(new ButtonWidget("Leave game", width / 2 - buttonWidth / 2, height / 2 + 60, buttonWidth, 40, () -> {
			glfwSetWindowShouldClose(Client.getWindow().getHandle(), true);
		}));
	}

	@Override
	public void renderBackground() {
		super.renderBackground();
		Gui.drawCenteredText("Game Paused", Client.getWindow().getWidth() / 2, Client.getWindow().getHeight() / 2 - 100, Color.WHITE);
	}

	@Override
	public boolean shouldPauseGame() {
		return true;
	}
}
