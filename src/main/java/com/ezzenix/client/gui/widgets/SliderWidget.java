package com.ezzenix.client.gui.widgets;

import com.ezzenix.client.gui.Color;
import com.ezzenix.client.gui.Gui;
import org.joml.Math;

import java.util.function.Consumer;
import java.util.function.Function;

public class SliderWidget extends Widget {
	private static final int BAR_WIDTH = 5;

	private int value;
	private int minValue;
	private int maxValue;
	private int increment;

	private boolean isDragging = false;
	private int dragStartValue;

	private String text;

	private Consumer<Integer> onChange;
	private Function<Integer, String> stringFormatter;

	public SliderWidget(String text, int x, int y, int width, int height, int value, int minValue, int maxValue, int increment) {
		super(x, y, width, height);

		this.text = text;

		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.increment = increment;
	}

	public void setCallback(Consumer<Integer> consumer) {
		this.onChange = consumer;
	}

	public void setStringFormatter(Function<Integer, String> formatter) {
		this.stringFormatter = formatter;
	}

	public int getLeftX() {
		return this.x + BAR_WIDTH / 2;
	}
	public int getRightX() {
		return this.x + this.width - BAR_WIDTH / 2;
	}

	private void setValue(int value) {
		this.value = Math.round((float) value / (float) this.increment) * this.increment;
		if (this.onChange != null) {
			this.onChange.accept(this.value);
		}
	}

	@Override
	public void mouseMoved(int x, int y) {
		super.mouseMoved(x, y);

		if (isDragging) {
			int leftX = this.getLeftX();
			int rightX = this.getRightX();
			float alpha = Math.clamp(0, 1, (float) (x - leftX) / (float) (rightX - leftX));
			setValue((int) Math.lerp(this.minValue, this.maxValue, alpha));
		}
	}

	@Override
	public void mouseDown(int x, int y) {
		super.mouseDown(x, y);
		isDragging = true;
		dragStartValue = this.value;
	}

	@Override
	public void mouseUp() {
		super.mouseUp();
		isDragging = false;
		//if (dragStartValue != this.value && this.onChange != null) {
		//	this.onChange.accept(this.value);
		//}
	}

	private float getAlpha() {
		return ((float) (this.value - this.minValue)) / ((float) (this.maxValue - this.minValue));
	}

	@Override
	public void render() {
		Gui.drawButtonRect(this.x, this.y, this.width, this.height, false);

		String text = this.text + ": " + this.stringFormatter.apply(this.value);
		Gui.drawCenteredText(text, this.x + this.width / 2, this.y + this.height / 2, Color.WHITE);

		int x = (int) (Math.lerp(this.getLeftX(), this.getRightX(), this.getAlpha()));
		Gui.drawRect(x - BAR_WIDTH / 2, this.y, BAR_WIDTH, this.height, 1, 1, 1, 1f);
	}
}
