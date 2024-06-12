package com.ezzenix.engine.gui;

public class UDim2 {
	public float scaleX;
	public float offsetX;
	public float scaleY;
	public float offsetY;

	public UDim2(float scaleX, float offsetX, float scaleY, float offsetY) {
		this.scaleX = scaleX;
		this.offsetX = offsetX;
		this.scaleY = scaleY;
		this.offsetY = offsetY;
	}

	public UDim2() {
		this(0, 0, 0, 0);
	}

	public static UDim2 fromScale(float scaleX, float scaleY) {
		return new UDim2(scaleX, 0, scaleY, 0);
	}

	public static UDim2 fromOffset(float offsetX, float offsetY) {
		return new UDim2(0, offsetX, 0, offsetY);
	}
}
