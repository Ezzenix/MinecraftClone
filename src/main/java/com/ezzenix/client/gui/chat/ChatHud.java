package com.ezzenix.client.gui.chat;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.Color;
import com.ezzenix.client.gui.Gui;
import com.ezzenix.engine.Scheduler;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

public class ChatHud {

	private static final int MAX_LINES_VISIBLE = 9;
	private static final float MESSAGE_VISIBLE_TIME = 3f;
	private static final float MESSAGE_FADE_TIME = 0.4f;
	private static final int LINE_HEIGHT = 26;
	private static final int PADDING = (LINE_HEIGHT - 16) / 2;

	private final List<ChatMessage> messages = new ArrayList<>();
	private float lastMessageReceived = 0;
	public int linesScrolled = 0;

	public ChatHud() {

	}

	public void addMessage(String text) {
		ChatMessage message = new ChatMessage(text);
		lastMessageReceived = Scheduler.getClock();

		if (linesScrolled != 0) linesScrolled++;

		messages.add(0, message);
	}

	public float getSmoothAnimationAlpha() {
		if (linesScrolled != 0) return 0;
		float TRANSITION_TIME = 0.08f;
		float time = Scheduler.getClock() - lastMessageReceived;
		if (time > TRANSITION_TIME) return 0;
		return 1 - (time / TRANSITION_TIME);
	}

	private boolean shouldShow(int index) {
		int startIndex = this.linesScrolled;
		int endIndex = startIndex + MAX_LINES_VISIBLE;
		return index >= startIndex && index < endIndex;
	}

	public void render() {
		if (getMessageCount() == 0) return;

		int width = Client.getWindow().getWidth();
		int height = Client.getWindow().getHeight();

		boolean isChatOpen = Client.getScreen() != null && (Client.getScreen() instanceof ChatScreen);

		int chatWidth = (int) (width * 0.35);
		int offsetY = (int) ((float) (LINE_HEIGHT) * getSmoothAnimationAlpha() * 0.4f);
		int bottomY = height - 150 + offsetY;

		int y = bottomY;
		for (int i = this.linesScrolled; i < this.linesScrolled + Math.min(MAX_LINES_VISIBLE, getMessageCount()); i++) {
			ChatMessage message = messages.get(i);

			float alpha = isChatOpen ? 1 : message.getFadeAlpha();
			if (alpha == 0) break;

			Gui.drawRect(0, y, chatWidth + PADDING * 2, LINE_HEIGHT, 0, 0, 0, alpha * 0.5f);
			Gui.drawText(message.text, 3 + PADDING, y + PADDING, Color.pack(1f, 1f, 1f, alpha));

			y -= LINE_HEIGHT;
		}

		// scrollbar
		if (this.getMessageCount() > MAX_LINES_VISIBLE && isChatOpen) {
			int barWidth = 3;
			int chatHeight = MAX_LINES_VISIBLE * LINE_HEIGHT;
			int barHeight = (int) (((float) MAX_LINES_VISIBLE / (float) getMessageCount()) * chatHeight);
			int yy = (int) (((1 - ((float) linesScrolled / (float) (getMessageCount() - MAX_LINES_VISIBLE))) * (chatHeight - barHeight)));
			Gui.drawRect(chatWidth + PADDING * 2 - barWidth, height - 150 + offsetY - chatHeight + LINE_HEIGHT + yy, barWidth, barHeight, 0.75f, 0.75f, 0.75f, 0.9f);
		}
	}

	public void scroll(int offset) {
		if (this.getMessageCount() <= MAX_LINES_VISIBLE) return;
		this.linesScrolled = Math.clamp(0, this.getMessageCount() - MAX_LINES_VISIBLE, this.linesScrolled + offset);
	}

	public int getMessageCount() {
		return this.messages.size();
	}

	private static class ChatMessage {
		String text;
		float timestamp;

		public ChatMessage(String text) {
			this.text = text;
			this.timestamp = Scheduler.getClock();
		}

		public float getFadeAlpha() {
			float time = Scheduler.getClock() - this.timestamp;

			if (time < MESSAGE_VISIBLE_TIME) return 1;
			if (time > MESSAGE_VISIBLE_TIME + MESSAGE_FADE_TIME) return 0;

			return 1 - (time - MESSAGE_VISIBLE_TIME) / MESSAGE_FADE_TIME;
		}
	}
}
