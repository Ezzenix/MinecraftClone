package com.ezzenix.client.gui.widgets;

import com.ezzenix.client.gui.GuiContext;
import org.joml.Math;

public class SliderWidget extends Widget {
	private static int BAR_WIDTH = 10;

	private float minValue = 2f;
	private float maxValue = 10f;
	private float value = 5f;

	private boolean isDragging = false;

	public SliderWidget(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public int getLeftX() {
		return this.x + BAR_WIDTH / 2;
	}
	public int getRightX() {
		return this.x + this.width - BAR_WIDTH / 2;
	}

	@Override
	public void mouseMoved(int x, int y) {
		super.mouseMoved(x, y);

		if (isDragging) {
			int leftX = this.getLeftX();
			int rightX = this.getRightX();
			float alpha = Math.clamp(0, 1, (float) (x - leftX) / (float) (rightX - leftX));
			this.value = Math.lerp(this.minValue, this.maxValue, alpha);
		}
	}

	@Override
	public void mouseDown(int x, int y) {
		super.mouseDown(x, y);
		isDragging = true;
	}

	@Override
	public void mouseUp() {
		super.mouseUp();
		isDragging = false;
	}

	private float getAlpha() {
		return (this.value - this.minValue) / (this.maxValue - this.minValue);
	}

	@Override
	public void render() {
		GuiContext.drawRect(this.x, this.y, this.width, this.height, 0, 0, 0, 0.7f);

		String text = String.format("%.1f", this.value);
		GuiContext.drawCenteredText(text, this.x + this.width / 2, this.y + this.height / 2, 18, 1, 1, 1);

		int x = (int) (Math.lerp(this.getLeftX(), this.getRightX(), this.getAlpha()));
		GuiContext.drawRect(x - BAR_WIDTH / 2, this.y, BAR_WIDTH, this.height, 1, 0, 0, 1f);
	}
}
