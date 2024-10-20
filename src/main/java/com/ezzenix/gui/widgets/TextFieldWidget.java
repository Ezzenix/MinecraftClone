package com.ezzenix.gui.widgets;

import com.ezzenix.Client;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.gui.Color;
import com.ezzenix.gui.FontRenderer;
import com.ezzenix.gui.Gui;

import static org.lwjgl.glfw.GLFW.*;

public class TextFieldWidget extends Widget {
	private String text = "";
	private boolean focused = false;
	private int cursor = 0;
	private int selectionStart;
	private int selectionEnd;
	private FontRenderer fontRenderer = Gui.FONT_RENDERER;
	private float focusedAt;

	public TextFieldWidget(String text, int x, int y) {
		super(x, y, 0, 0);

		this.setText(text);
		this.cursor = text.length();
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public void setFocused(boolean focused) {
		if (this.isFocused() && !focused) {
			Client.focusedTextField = null;
		} else if (!this.isFocused() && focused) {
			if (Client.focusedTextField != null) {
				Client.focusedTextField.setFocused(false);
			}
			Client.focusedTextField = this;
			this.focusedAt = Scheduler.getClock();
		}
	}

	public boolean isFocused() {
		return Client.focusedTextField == this;
	}

	private void write(String text) {
		String textBeforeCursor = this.text.substring(0, cursor);
		String textAfterCursor = this.text.substring(cursor);

		this.setText(textBeforeCursor + text + textAfterCursor);
		moveCursor(1);
	}

	private void erase() {
		String textBeforeCursor = this.text.substring(0, cursor);
		String textAfterCursor = this.text.substring(cursor);

		if (cursor == 0) return;

		this.setText(textBeforeCursor.substring(0, textBeforeCursor.length() - 1) + textAfterCursor);

		moveCursor(-1);
	}

	@Override
	public void charTyped(int codePoint) {
		if (Scheduler.getClock() - this.focusedAt < 0.05f) return;
		this.write(String.valueOf((char) codePoint));
	}

	private void setCursor(int pos) {
		this.cursor = pos;
	}

	private void moveCursor(int offset) {
		int pos = this.cursor + offset;
		if (pos < 0 || pos > this.getText().length()) return;
		setCursor(pos);
	}

	@Override
	public void keyPressed(int key, int action) {
		if (action != GLFW_PRESS) return;

		switch (key) {
			case GLFW_KEY_BACKSPACE -> {
				erase();
			}
			case GLFW_KEY_LEFT -> {
				moveCursor(-1);
			}
			case GLFW_KEY_RIGHT -> {
				moveCursor(1);
			}
		}
	}

	@Override
	public void dispose() {
		this.setFocused(false);
	}

	@Override
	public void render() {
		Gui.drawText(this.getText(), this.x, this.y, Color.WHITE);

		if (Math.floor(Scheduler.getClock() * 2) % 2 == 0) {
			int cursorInset = fontRenderer.getWidth(text.substring(0, cursor));
			Gui.drawRect(this.x + cursorInset, this.y, 2, 18, 1, 1, 1, 1);
		}
	}
}
