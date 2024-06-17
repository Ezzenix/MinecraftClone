package com.ezzenix.client.gui.chat;

import com.ezzenix.client.gui.screen.Screen;
import com.ezzenix.client.gui.library.UDim2;
import com.ezzenix.client.gui.library.components.GuiFrame;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class ChatScreen extends Screen {

	public ChatScreen() {
		super("Chat");
	}

	public void init(int width, int height) {

		GuiFrame fieldBackground = new GuiFrame();
		fieldBackground.color = new Vector3f(0f, 0f, 0f);
		fieldBackground.transparency = 0.4f;
		fieldBackground.position = new UDim2(0, 8, 0.9f, 0);
		fieldBackground.anchorPoint = new Vector2f(0, 1);
		fieldBackground.screen = this;
		fieldBackground.size = new UDim2(0.45f, 0, 0, 22);
	}

	@Override
	public void renderBackground() {
	}
}
