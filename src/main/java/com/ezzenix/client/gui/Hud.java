package com.ezzenix.client.gui;

import com.ezzenix.client.gui.chat.ChatHud;
import com.ezzenix.client.gui.library.Gui;
import com.ezzenix.client.gui.library.UDim2;
import com.ezzenix.client.gui.library.components.GuiText;
import org.joml.Vector2f;

public class Hud {

	ChatHud chatHud = new ChatHud();

	GuiText crosshair;


	public Hud() {
		chatHud.addMessage("Hello world!");
		chatHud.addMessage("Test message...");
		crosshair = new GuiText();
		crosshair.position = new UDim2(0.5f, 0, 0.5f, -2);
		crosshair.anchorPoint = new Vector2f(0.5f, 0.5f);
		crosshair.text = "+";
		crosshair.size = UDim2.fromOffset(22, 22);
		crosshair.textAlign = Gui.TextAlign.Center;
	}
}
