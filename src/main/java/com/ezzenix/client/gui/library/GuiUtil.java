package com.ezzenix.client.gui.library;

import com.ezzenix.client.Client;
import com.ezzenix.engine.opengl.Window;
import org.joml.Vector2f;

public class GuiUtil {
	/*
	 * Converts pixel coordinates to normalized device coordinates where pixel (0, 0) is top-left corner
	 */
	public static float toNormalizedDeviceCoordinateX(float x) {
		return (x / Client.getWindow().getWidth()) * 2 - 1;
	}

	public static float toNormalizedDeviceCoordinateY(float y) {
		return 1 - (y / Client.getWindow().getHeight()) * 2;
	}
	public static Vector2f toNormalizedDeviceCoordinates(float x, float y) {
		return new Vector2f(toNormalizedDeviceCoordinateX(x), toNormalizedDeviceCoordinateY(y));
	}
}
