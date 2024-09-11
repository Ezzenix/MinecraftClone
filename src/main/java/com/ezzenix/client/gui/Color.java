package com.ezzenix.client.gui;

public class Color {
	public static int WHITE = pack(1f, 1f, 1f, 1f);

	public static int pack(int r, int g, int b, int a) {
		return (r << 24) | (g << 16) | (b << 8) | a;
	}

	public static int pack(float r, float g, float b, float a) {
		return pack((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
	}

	public static int pack(int r, int g, int b, float a) {
		return pack(r, g, b, (int) (a * 255));
	}

	public static float[] unpack(int packed) {
		float r = (float) ((packed >> 24) & 0xFF) / 255f;
		float g = (float) ((packed >> 16) & 0xFF) / 255f;
		float b = (float) ((packed >> 8) & 0xFF) / 255f;
		float a = (float) (packed & 0xFF) / 255f;
		return new float[]{r, g, b, a};
	}
}
