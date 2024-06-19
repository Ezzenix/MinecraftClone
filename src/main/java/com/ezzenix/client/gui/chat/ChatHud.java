package com.ezzenix.client.gui.chat;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.GuiContext;
import com.ezzenix.engine.Scheduler;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

public class ChatHud {

	List<ChatMessage> messages = new ArrayList<>();

	public ChatHud() {

	}

	public void addMessage(String text) {
		ChatMessage message = new ChatMessage(text);

		messages.add(0, message);
	}


	public void render() {
		int CHAT_WIDTH = (int) (Client.getWindow().getWidth() * 0.4);

		int width = Client.getWindow().getWidth();
		int height = Client.getWindow().getHeight();

		int lineHeight = 26;
		int pad = (lineHeight - 16) / 2;

		int i = 0;
		for (ChatMessage message : this.messages) {
			int x = 6;
			int y = height - 150 - i * lineHeight;

			float transparency = 0.5f * message.getFadeAlpha();
			if (transparency == 0) break;

			int textWidth = GuiContext.FONT_RENDERER.getTextWidth(message.text, 18);

			GuiContext.drawRect(x, y, CHAT_WIDTH + pad * 2, lineHeight, 0, 0, 0, transparency);
			GuiContext.drawText(message.text, x + pad, y + pad, 18, 1, 1, 1);

			i++;
		}
	}


	private static class ChatMessage {
		String text;
		float timestamp;

		public ChatMessage(String text) {
			this.text = text;
			this.timestamp = Scheduler.getClock();
		}

		public float getFadeAlpha() {
			float VISIBLE_TIME = 3f;
			float FADE_TIME = 0.4f;

			float time = Scheduler.getClock() - this.timestamp;

			if (time < VISIBLE_TIME) return 1;
			if (time > VISIBLE_TIME + FADE_TIME) return 0;

			return 1 - (time - VISIBLE_TIME) / FADE_TIME;
		}
	}
}
