package com.ezzenix.client.gui;

import com.ezzenix.client.rendering.Renderer;
import com.ezzenix.client.rendering.util.VertexBuffer;
import com.ezzenix.client.rendering.util.VertexFormat;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import com.ezzenix.game.blocks.BlockType;
import org.joml.Vector2f;

import java.io.File;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class GuiContext {

	public static final FontRenderer FONT_RENDERER = FontRenderer.fromFile(new File("src/main/resources/fonts/minecraft.ttf"), 18);


	private static final VertexBuffer rectangleBuffer = new VertexBuffer(new Shader("gui/frame"), new VertexFormat(GL_FLOAT, 2, GL_FLOAT, 4), VertexBuffer.Usage.DYNAMIC);
	private static final VertexBuffer fontBuffer = new VertexBuffer(new Shader("gui/text"), new VertexFormat(GL_FLOAT, 2, GL_FLOAT, 2, GL_FLOAT, 3), VertexBuffer.Usage.DYNAMIC);
	private static final VertexBuffer textureBuffer = new VertexBuffer(new Shader("gui/image"), new VertexFormat(GL_FLOAT, 2, GL_FLOAT, 2), VertexBuffer.Usage.DYNAMIC);

	private static boolean shouldRecomputeThisFrame = false;
	private static long lastGuiRecompute = 0;
	private static final long GUI_RENDER_INTERVAL = (long) (0.0166 * 1E9); // 60 FPS

	static {
		Scheduler.bindToUpdate(() -> {
			long now = System.nanoTime();
			shouldRecomputeThisFrame = (now - lastGuiRecompute > GUI_RENDER_INTERVAL);
			if (shouldRecomputeThisFrame) {
				lastGuiRecompute = now;
			}
		});
	}

	private static void uploadAndDraw(VertexBuffer buffer) {
		if (shouldRecomputeThisFrame) {
			buffer.upload();
		}
		buffer.draw();
	}

	public static void renderBatch() {
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		uploadAndDraw(rectangleBuffer);

		FONT_RENDERER.getAtlasTexture().bind();
		uploadAndDraw(fontBuffer);

		glUseProgram(0);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}

	public static void drawRect(int x, int y, int width, int height, float r, float g, float b, float a) {
		if (!shouldRecomputeThisFrame) return;

		rectangleBuffer.vertex(x, y).color(r, g, b, a).next();
		rectangleBuffer.vertex(x, y + height).color(r, g, b, a).next();
		rectangleBuffer.vertex(x + width, y + height).color(r, g, b, a).next();

		rectangleBuffer.vertex(x + width, y + height).color(r, g, b, a).next();
		rectangleBuffer.vertex(x + width, y).color(r, g, b, a).next();
		rectangleBuffer.vertex(x, y).color(r, g, b, a).next();
	}

	public static void drawRectGradient(int x, int y, int width, int height, float r, float g, float b, float a, float r2, float g2, float b2, float a2) {
		if (!shouldRecomputeThisFrame) return;

		rectangleBuffer.vertex(x, y).color(r, g, b, a).next();
		rectangleBuffer.vertex(x, y + height).color(r2, g2, b2, a2).next();
		rectangleBuffer.vertex(x + width, y + height).color(r2, g2, b2, a2).next();

		rectangleBuffer.vertex(x + width, y + height).color(r2, g2, b2, a2).next();
		rectangleBuffer.vertex(x + width, y).color(r, g, b, a).next();
		rectangleBuffer.vertex(x, y).color(r, g, b, a).next();
	}

	private static void drawBatchedTexture(Texture texture) {
		textureBuffer.upload();

		textureBuffer.shader.setTexture(0, texture);

		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		textureBuffer.draw();
		glUseProgram(0);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}

	public static void drawTexture(Texture texture, int x, int y, int width, int height) {
		textureBuffer.vertex(x, y).texture(0, 0).next();
		textureBuffer.vertex(x, y + height).texture(0, 1).next();
		textureBuffer.vertex(x + width, y + height).texture(1, 1).next();

		textureBuffer.vertex(x + width, y + height).texture(1, 1).next();
		textureBuffer.vertex(x + width, y).texture(1, 0).next();
		textureBuffer.vertex(x, y).texture(0, 0).next();

		drawBatchedTexture(texture);
	}

	public static void drawBlockIcon(BlockType blockType, int x, int y, int size) {
		Texture texture = Renderer.getWorldRenderer().blockTexture;
		Vector2f[] uv = blockType.textureUVSides;

		textureBuffer.vertex(x, y).texture(uv[0]).next();
		textureBuffer.vertex(x, y + size).texture(uv[1]).next();
		textureBuffer.vertex(x + size, y + size).texture(uv[2]).next();

		textureBuffer.vertex(x + size, y + size).texture(uv[2]).next();
		textureBuffer.vertex(x + size, y).texture(uv[3]).next();
		textureBuffer.vertex(x, y).texture(uv[0]).next();

		drawBatchedTexture(texture);
	}

	public static void drawCenteredText(String text, int centerX, int centerY, int fontSize, float r, float g, float b) {
		int textWidth = FONT_RENDERER.getTextWidth(text, fontSize);
		drawText(text, centerX - textWidth / 2, centerY - fontSize / 2, fontSize, r, g, b);
	}

	public static void drawText(String text, int x, int y, int fontSize, float r, float g, float b) {
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
