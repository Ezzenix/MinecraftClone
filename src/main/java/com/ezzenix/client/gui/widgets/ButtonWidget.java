package com.ezzenix.client.gui.widgets;

import com.ezzenix.client.gui.GuiContext;

public class ButtonWidget extends Widget {
	public String text;
	private Runnable onClick;

	public ButtonWidget(String text, int x, int y, int width, int height, Runnable onClick) {
		super(x, y, width, height);
		this.text = text;
		this.onClick = onClick;
	}

	@Override
	public void mouseClicked(int x, int y) {
		super.mouseClicked(x, y);
		this.onClick.run();
	}

	@Override
	public void render() {
		float color = this.isHovered() ? 0.25f : 0f;

		GuiContext.drawRect(this.x, this.y, this.width, this.height, color, color, color, 0.7f);
		GuiContext.drawCenteredText(this.text, this.x + this.width / 2, this.y + this.height / 2, 18, 1, 1, 1);
	}
}
