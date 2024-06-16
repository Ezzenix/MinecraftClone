package com.ezzenix.client.gui.library;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Window;
import org.joml.Vector2f;

public class GuiUtil {
	private static int windowWidth;
	private static int windowHeight;

	static {
		Window window = Game.getInstance().getWindow();
		windowWidth = window.getWidth();
		windowHeight = window.getHeight();
		window.sizeChanged.connect(() -> {
			windowWidth = window.getWidth();
			windowHeight = window.getHeight();
		});
	}

	/*
	 * Converts pixel coordinates to normalized device coordinates where pixel (0, 0) is top-left corner
	 */
	public static float toNormalizedDeviceCoordinateX(float x) {
		return (x / windowWidth) * 2 - 1;
	}

	public static float toNormalizedDeviceCoordinateY(float y) {
		return 1 - (y / windowHeight) * 2;
	}
	public static Vector2f toNormalizedDeviceCoordinates(float x, float y) {
		return new Vector2f(toNormalizedDeviceCoordinateX(x), toNormalizedDeviceCoordinateY(y));
	}
}
