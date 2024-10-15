package com.ezzenix.client.gui;

import com.ezzenix.client.Client;
import org.joml.Vector2f;

public class GuiUtil {
	/*
	 * Converts pixel x-coordinate to normalized device coordinate
	 */
	public static float toNormalizedDeviceCoordinateX(float x) {
		return (x / Client.getWindow().getWidth()) * 2 - 1;
	}

	/*
	 * Converts pixel y-coordinate to normalized device coordinate
	 */
	public static float toNormalizedDeviceCoordinateY(float y) {
		return 1 - (y / Client.getWindow().getHeight()) * 2;
	}

	/*
	 * Converts pixel coordinates to normalized device coordinates where pixel (0, 0) is top-left corner
	 */
	public static Vector2f toNormalizedDeviceCoordinates(float x, float y) {
		return new Vector2f(toNormalizedDeviceCoordinateX(x), toNormalizedDeviceCoordinateY(y));
	}
}