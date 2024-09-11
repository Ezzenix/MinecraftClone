package com.ezzenix.client.gui.chat;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.Gui;
import com.ezzenix.client.gui.screen.Screen;
import com.ezzenix.client.gui.widgets.TextFieldWidget;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class ChatScreen extends Screen {
	String cachedText = "";
	TextFieldWidget widget;

	private final int FIELD_HEIGHT = 26;
	private final int FIELD_PADDING = 5;
	private final int TEXT_PADDING = (26 - 18) / 2;

	public ChatScreen() {
		super("Chat");
	}

	public void init(int width, int height) {
		this.widget = new TextFieldWidget(cachedText, FIELD_PADDING + TEXT_PADDING, Client.getWindow().getHeight() - FIELD_PADDING - FIELD_HEIGHT + TEXT_PADDING);
		this.widget.setFocused(true);

		this.addWidget(widget);
	}

	public void onRemoved() {
		this.cachedText = "";
		Client.getHud().chatHud.linesScrolled = 0;
	}

	public void keyPressed(int key, int action) {
		super.keyPressed(key, action);
		if (key == GLFW_KEY_ENTER && action == GLFW_PRESS && this.widget != null) {
			String text = this.widget.getText();
			if (text.trim().isEmpty()) return;
			Client.getHud().chatHud.addMessage("<Ezzenix> " + text);
			Client.setScreen(null);
		}
	}

	@Override
	public void scrolled(double x, double y) {
		if (y == 0) return;
		Client.getHud().chatHud.scroll(((int) y) * 3);
	}

	@Override
	public void renderBackground() {
		int width = Client.getWindow().getWidth();
		int height = Client.getWindow().getHeight();

		Gui.drawRect(FIELD_PADDING, height - FIELD_PADDING - FIELD_HEIGHT, width - FIELD_PADDING * 2, FIELD_HEIGHT, 0, 0, 0, 0.5f);
	}
}
