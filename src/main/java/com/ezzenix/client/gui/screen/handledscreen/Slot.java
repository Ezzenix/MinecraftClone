package com.ezzenix.client.gui.screen.handledscreen;

public class Slot {
	public int id;
	public int x;
	public int y;
	public int size;

	public static int SIZE_PX = 64;

	public Slot(int id, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.size = SIZE_PX;
	}

	public boolean isMouseWithin(int mouseX, int mouseY) {
		return (mouseX > this.x && mouseX < this.x + this.size &&
			mouseY > this.y && mouseY < this.y + this.size);
	}
}