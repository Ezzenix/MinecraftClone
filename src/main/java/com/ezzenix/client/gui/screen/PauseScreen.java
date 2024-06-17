package com.ezzenix.client.gui.screen;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.GuiContext;
import com.ezzenix.client.gui.library.Gui;
import com.ezzenix.client.gui.library.UDim2;
import com.ezzenix.client.gui.library.components.GuiButton;
import com.ezzenix.client.gui.library.components.GuiFrame;
import com.ezzenix.client.gui.library.components.GuiText;
import com.ezzenix.client.gui.widgets.ButtonWidget;
import com.ezzenix.client.gui.widgets.SliderWidget;
import com.ezzenix.engine.opengl.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class PauseScreen extends Screen {
	public PauseScreen() {
		super("Pause Menu");
	}

	@Override
	public void init(int width, int height) {
		this.addWidget(new ButtonWidget("Quit game", width / 2 - 100, height / 2 - 50, 200, 40, () -> {
			glfwSetWindowShouldClose(Client.getWindow().getHandle(), true);
		}));

		this.addWidget(new SliderWidget(width / 2 - 100, height / 2 + 50, 200, 40));
	}

	@Override
	public boolean shouldPauseGame() {
		return true;
	}
}
