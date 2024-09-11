package com.ezzenix.client.gui.widgets;

import com.ezzenix.client.Client;

public class Widget {
	public int x;
	public int y;
	public int width;
	public int height;

	public Widget(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void mouseClicked(int x, int y) {

	}

	public void mouseDown(int x, int y) {

	}

	public void mouseMoved(int x, int y) {

	}

	public void mouseUp() {

	}

	public void keyPressed(int key, int action) {

	}

	public void charTyped(int codePoint) {

	}

	public boolean isWithin(int x, int y) {
		return x >= this.x && x <= this.x + this.width
			&& y >= this.y && y <= this.y + this.height;
	}

	public boolean isHovered() {
		return isWithin(Client.getMouse().getX(), Client.getMouse().getY());
	}

	public void render() {

	}

	public void dispose() {
	}
}
