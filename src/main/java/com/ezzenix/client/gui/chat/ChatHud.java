package com.ezzenix.client.gui.chat;

import com.ezzenix.client.gui.library.Gui;
import com.ezzenix.client.gui.library.UDim2;
import com.ezzenix.client.gui.library.components.GuiFrame;
import com.ezzenix.client.gui.library.components.GuiText;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ChatHud {

	List<ChatMessage> messages = new ArrayList<>();

	public ChatHud() {

	}

	public void update() {
		int i = 0;
		for (ChatMessage message : messages) {
			if (message.guiText != null) message.guiText.dispose();
			if (message.guiFrame != null) message.guiFrame.dispose();

			if (i > 5) continue;

			GuiFrame guiFrame = new GuiFrame();
			guiFrame.color = new Vector3f(0f, 0f, 0f);
			guiFrame.transparency = 0.6f;
			guiFrame.position = new UDim2(0, 8, 0.8f, -(i * 22));
			guiFrame.anchorPoint = new Vector2f(0, 1);
			guiFrame.size = new UDim2(0.45f, 0, 0, 22);

			GuiText guiText = new GuiText();
			guiText.text = message.text;
			guiText.position = new UDim2(0, 12, 0.8f, -(i * 22));
			guiText.anchorPoint = new Vector2f(0, 1);
			guiText.size = new UDim2(0.45f, 0, 0, 22);

			message.guiText = guiText;
			message.guiFrame = guiFrame;
			i++;
		}
	}

	public void addMessage(String text) {
		ChatMessage message = new ChatMessage(text);

		messages.add(0, message);
		update();
	}


	private static class ChatMessage {
		String text;
		GuiText guiText;
		GuiFrame guiFrame;

		public ChatMessage(String text) {
			this.text = text;
		}
	}
}
