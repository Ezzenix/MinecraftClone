package com.ezzenix.client.gui.screen;

import com.ezzenix.client.Client;
import com.ezzenix.client.gui.Gui;
import com.ezzenix.client.gui.widgets.Widget;

import java.util.ArrayList;
import java.util.List;

public class Screen {
	private final String title;
	private final List<Widget> widgets;
	public Screen parent;

	public Screen(String title) {
		this.title = title;
		this.widgets = new ArrayList<>();
	}

	public void init(int width, int height) {

	}

	public void onDisplayed() {
	}


	public void render() {
		this.renderBackground();
		for (Widget widget : this.widgets) {
			widget.render();
		}
	}

	public void renderBackground() {
		float alpha1 = 0.4f;
		float alpha2 = 0.6f;
		Gui.drawRectGradient(0, 0, Client.getWindow().getWidth(), Client.getWindow().getHeight(), 0, 0, 0, alpha1, 0, 0, 0, alpha2);
	}

	public boolean shouldPauseGame() {
		return false;
	}


	public void addWidget(Widget widget) {
		this.widgets.add(widget);
	}

	public List<Widget> getWidgets() {
		return this.widgets;
	}


	public void mouseClicked(int x, int y) {
		for (Widget widget : this.widgets.stream().toList()) {
			if (widget.isWithin(x, y)) {
				widget.mouseClicked(x, y);
			}
		}
	}

	public void mouseDown(int x, int y) {
		for (Widget widget : this.widgets) {
			if (widget.isWithin(x, y)) {
				widget.mouseDown(x, y);
			}
		}
	}

	public void mouseMoved(int x, int y) {
		for (Widget widget : this.widgets) {
			widget.mouseMoved(x, y);
		}
	}

	public void mouseUp() {
		for (Widget widget : this.widgets) {
			widget.mouseUp();
		}
	}

	public void keyPressed(int key, int action) {
		for (Widget widget : this.widgets) {
			widget.keyPressed(key, action);
		}
	}

	public void charTyped(int codePoint) {
		for (Widget widget : this.widgets) {
			widget.charTyped(codePoint);
		}
	}

	public void scrolled(double x, double y) {

	}

	public void onRemoved() {
	}

	public void mouse2Down(int x, int y) {

	}
	public void mouse2Up(int x, int y) {

	}

	public void dispose() {
		for (Widget widget : this.widgets) {
			widget.dispose();
		}
		this.widgets.clear();
	}
}