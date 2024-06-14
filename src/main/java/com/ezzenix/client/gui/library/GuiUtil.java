package com.ezzenix.client.gui.library;

import com.ezzenix.Game;
import org.joml.Vector2f;

public class GuiUtil {
	/**
	 * Converts pixel coordinates to normalized device coordinates where pixel (0, 0) is top-left corner
	 */
	public static Vector2f toNormalizedDeviceCoordinates(float x, float y) {
		int width = Game.getInstance().getWindow().getWidth();
		int height = Game.getInstance().getWindow().getHeight();

		float normalizedX = (x / width) * 2 - 1;
		float normalizedY = 1 - (y / height) * 2;

		return new Vector2f(normalizedX, normalizedY);
	}
	public static Vector2f toNormalizedDeviceCoordinates(Vector2f pixelCoordinates) {
		return toNormalizedDeviceCoordinates(pixelCoordinates.x, pixelCoordinates.y);
	}
}
