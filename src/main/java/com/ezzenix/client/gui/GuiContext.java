package com.ezzenix.client.gui;

import com.ezzenix.client.gui.library.FontRenderer;
import com.ezzenix.client.rendering.util.VertexBuffer;
import com.ezzenix.client.rendering.util.VertexFormat;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.engine.opengl.Shader;
import org.joml.Vector2f;

import java.io.File;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class GuiContext {

	public static final FontRenderer FONT_RENDERER = FontRenderer.fromFile(new File("src/main/resources/fonts/minecraft.ttf"), 18);

	private static final VertexBuffer rectangleBuffer = new VertexBuffer(new Shader("gui/frame"), new VertexFormat(GL_FLOAT, 2, GL_FLOAT, 4), VertexBuffer.Usage.DYNAMIC);
	private static final VertexBuffer fontBuffer = new VertexBuffer(new Shader("gui/text"), new VertexFormat(GL_FLOAT, 2, GL_FLOAT, 2, GL_FLOAT, 3), VertexBuffer.Usage.DYNAMIC);

	private static boolean shouldRecomputeThisFrame = false;
	private static long lastGuiRecompute = 0;
	private static final long GUI_RENDER_INTERVAL = (long) (0.0166 * 2 * 1E9); // 30 FPS

	static {
		Scheduler.bindToUpdate(() -> {
			long now = System.nanoTime();
			shouldRecomputeThisFrame = (now - lastGuiRecompute > GUI_RENDER_INTERVAL);
			if (shouldRecomputeThisFrame) {
				lastGuiRecompute = now;
			}
		});
	}

	public static void renderBatch() {
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		rectangleBuffer.draw(shouldRecomputeThisFrame);

		FONT_RENDERER.getAtlasTexture().bind();
		fontBuffer.draw(shouldRecomputeThisFrame);

		glUseProgram(0);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}

	public static void drawRect(int x, int y, int width, int height, int r, int g, int b, int a) {
		if (!shouldRecomputeThisFrame) return;

		rectangleBuffer.vertex(x, y).color(r, g, b, a).next();
		rectangleBuffer.vertex(x, y + height).color(r, g, b, a).next();
		rectangleBuffer.vertex(x + width, y + height).color(r, g, b, a).next();

		rectangleBuffer.vertex(x + width, y + height).color(r, g, b, a).next();
		rectangleBuffer.vertex(x + width, y).color(r, g, b, a).next();
		rectangleBuffer.vertex(x, y).color(r, g, b, a).next();
	}

	public static void drawText(String text, int x, int y, int fontSize, int r, int g, int b) {
		if (!shouldRecomputeThisFrame) return;

		float TEXT_SCALE = (float) fontSize / FONT_RENDERER.fontSize;

		y -= 5; // TODO: Make this a better way, font offset to align top of text

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			FontRenderer.Glyph glyph = FONT_RENDERER.getGlyph(c);
			if (glyph == null) continue;

			if (c == ' ') {
				x += (int) (glyph.width * 2 * TEXT_SCALE); // space doesn't have to be actually rendered
				continue;
			}

			int width = (int) (glyph.width * TEXT_SCALE);
			int height = (int) (glyph.height * TEXT_SCALE);

			Vector2f[] uv = glyph.uvCoords;

			fontBuffer.vertex(x, y).texture(uv[1]).color(r, g, b).next();
			fontBuffer.vertex(x, y + height).texture(uv[0]).color(r, g, b).next();
			fontBuffer.vertex(x + width, y + height).texture(uv[3]).color(r, g, b).next();

			fontBuffer.vertex(x + width, y).texture(uv[2]).color(r, g, b).next();
			fontBuffer.vertex(x, y).texture(uv[1]).color(r, g, b).next();
			fontBuffer.vertex(x + width, y + height).texture(uv[3]).color(r, g, b).next();

			x += width;
		}
	}
}
