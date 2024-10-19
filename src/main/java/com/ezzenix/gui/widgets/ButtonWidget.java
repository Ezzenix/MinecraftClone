package com.ezzenix.gui.widgets;

import com.ezzenix.gui.Color;
import com.ezzenix.gui.Gui;

public class ButtonWidget extends Widget {
	public String text;
	private final Runnable onClick;

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
		Gui.drawButtonRect(x, y, width, height, this.isHovered());
		Gui.drawCenteredTextWithShadow(this.text, this.x + this.width / 2, this.y + this.height / 2, Color.WHITE);
	}
}
