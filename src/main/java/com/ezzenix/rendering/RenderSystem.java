package com.ezzenix.rendering;

public class RenderSystem {

	private static float shaderFogStart;
	private static float shaderFogEnd;
	private static int shaderFogColor;

	public static void setShaderFogStartEnd(float start, float end) {
		shaderFogStart = start;
		shaderFogEnd = end;
	}

	public static void setShaderFogColor(int color) {
		shaderFogColor = color;
	}

	public static float getShaderFogStart() {
		return shaderFogStart;
	}

	public static float getShaderFogEnd() {
		return shaderFogEnd;
	}

	public static int getShaderFogColor() {
		return shaderFogColor;
	}
}
