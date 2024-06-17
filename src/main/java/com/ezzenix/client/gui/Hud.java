package com.ezzenix.client.gui;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.chat.ChatHud;
import com.ezzenix.client.gui.library.Gui;
import com.ezzenix.client.gui.library.UDim2;
import com.ezzenix.client.gui.library.components.GuiText;
import org.joml.Vector2f;

public class Hud {

	ChatHud chatHud = new ChatHud();

	public Hud() {

	}

	private void renderCrosshair() {
		int width = Client.getWindow().getWidth();
		int height = Client.getWindow().getHeight();

		int length = 6;
		int size = 2;

		GuiContext.drawRect(width / 2 - size / 2, height / 2 - length, size, length * 2, 1, 1, 1, 0.5f);
		GuiContext.drawRect(width / 2 - length, height / 2 - size / 2, length * 2, size, 1, 1, 1, 0.5f);
	}

	public void render() {
		renderCrosshair();
	}
}
