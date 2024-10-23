package com.ezzenix.rendering;

import static org.lwjgl.opengl.GL11.*;

public class RenderSystem {

	private static final Capability cullingCapability = new Capability(GL_CULL_FACE);
	private static final Capability depthTestCapability = new Capability(GL_DEPTH_TEST);
	private static final Capability blendCapability = new Capability(GL_BLEND);

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

	public static void enableCulling() {
		cullingCapability.enable();
	}

	public static void disableCulling() {
		cullingCapability.disable();
	}

	public static void enableDepthTest() {
		depthTestCapability.enable();
	}

	public static void disableDepthTest() {
		depthTestCapability.disable();
	}

	public static void enableBlend() {
		blendCapability.enable();
	}

	public static void disableBlend() {
		blendCapability.disable();
	}


	private static class Capability {
		private final int id;
		private boolean state;

		public Capability(int id) {
			this.id = id;
			this.state = glIsEnabled(this.id);
		}

		public void disable() {
			this.setState(false);
		}

		public void enable() {
			this.setState(true);
		}

		private void setState(boolean enabled) {
			if (this.state != enabled) {
				this.state = enabled;
				if (enabled) {
					glEnable(this.id);
				} else {
					glDisable(this.id);
				}
			}
		}
	}
}
