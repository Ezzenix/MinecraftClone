package com.ezzenix.gui;

import com.ezzenix.blocks.Block;
import com.ezzenix.rendering.Renderer;
import com.ezzenix.rendering.util.VertexBuffer;
import com.ezzenix.rendering.util.VertexFormat;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import com.ezzenix.inventory.ItemStack;
import com.ezzenix.item.BlockItem;
import org.joml.Vector2f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Gui {

	//public static final FontRenderer FONT_RENDERER = FontRenderer.fromFile(new File("src/main/resources/fonts/minecraft.ttf"), 18);
	public static final FontRenderer FONT_RENDERER = new FontRenderer(new File("src/main/resources/fonts/minecraft.ttf"), 18);


	private static final VertexBuffer rectangleBuffer = new VertexBuffer(new Shader("gui/frame"), new VertexFormat(GL_FLOAT, 2, GL_INT, 1), VertexBuffer.Usage.DYNAMIC);
	private static final VertexBuffer fontBuffer = new VertexBuffer(new Shader("gui/text"), new VertexFormat(GL_FLOAT, 2, GL_FLOAT, 2, GL_INT, 1), VertexBuffer.Usage.DYNAMIC);
	private static final VertexBuffer textureBuffer = new VertexBuffer(new Shader("gui/image"), new VertexFormat(GL_FLOAT, 2, GL_FLOAT, 2), VertexBuffer.Usage.DYNAMIC);

	private static boolean shouldRecomputeThisFrame = false;
	private static long lastGuiRecompute = 0;
	private static final long GUI_RENDER_INTERVAL = (long) (0.0166 * 1E9); // 60 FPS

	private static final List<Runnable> renderTasks = new ArrayList<>();

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

		for (Runnable task : renderTasks) {
			task.run();
		}
		renderTasks.clear();

		FONT_RENDERER.getTexture().bind();
		uploadAndDraw(fontBuffer);

		glUseProgram(0);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
	}

	public static void drawRect(int x, int y, int width, int height, int color) {
		if (!shouldRecomputeThisFrame) return;

		rectangleBuffer.vertex(x, y).color(color).next();
		rectangleBuffer.vertex(x, y + height).color(color).next();
		rectangleBuffer.vertex(x + width, y + height).color(color).next();

		rectangleBuffer.vertex(x + width, y + height).color(color).next();
		rectangleBuffer.vertex(x + width, y).color(color).next();
		rectangleBuffer.vertex(x, y).color(color).next();
	}

	public static void drawRect(int x, int y, int width, int height, float r, float g, float b, float a) {
		if (!shouldRecomputeThisFrame) return;

		drawRect(x, y, width, height, Color.pack(r, g, b, a));
	}

	public static void drawRectGradient(int x, int y, int width, int height, float r, float g, float b, float a, float r2, float g2, float b2, float a2) {
		if (!shouldRecomputeThisFrame) return;

		int colorTop = Color.pack(r, g, b, a);
		int colorBottom = Color.pack(r2, g2, b2, a2);

		rectangleBuffer.vertex(x, y).color(colorTop).next();
		rectangleBuffer.vertex(x, y + height).color(colorBottom).next();
		rectangleBuffer.vertex(x + width, y + height).color(colorBottom).next();

		rectangleBuffer.vertex(x + width, y + height).color(colorBottom).next();
		rectangleBuffer.vertex(x + width, y).color(colorTop).next();
		rectangleBuffer.vertex(x, y).color(colorTop).next();
	}

	public static void drawTexture(Texture texture, int x, int y, int width, int height) {
		renderTasks.add(() -> {
			textureBuffer.shader.setTexture(0, texture);

			textureBuffer.vertex(x, y).texture(0, 0).next();
			textureBuffer.vertex(x, y + height).texture(0, 1).next();
			textureBuffer.vertex(x + width, y + height).texture(1, 1).next();

			textureBuffer.vertex(x + width, y + height).texture(1, 1).next();
			textureBuffer.vertex(x + width, y).texture(1, 0).next();
			textureBuffer.vertex(x, y).texture(0, 0).next();

			textureBuffer.upload();
			textureBuffer.draw();
		});
	}

	public static void drawBlockIcon(Block blockType, int x, int y, int size) {
		Texture texture = Renderer.getWorldRenderer().blockTexture;
		Vector2f[] uv = blockType.getTexture().getSideUV();

		renderTasks.add(() -> {
			textureBuffer.shader.setTexture(0, texture);

			textureBuffer.vertex(x, y).texture(uv[0]).next();
			textureBuffer.vertex(x, y + size).texture(uv[1]).next();
			textureBuffer.vertex(x + size, y + size).texture(uv[2]).next();

			textureBuffer.vertex(x + size, y + size).texture(uv[2]).next();
			textureBuffer.vertex(x + size, y).texture(uv[3]).next();
			textureBuffer.vertex(x, y).texture(uv[0]).next();

			textureBuffer.upload();
			textureBuffer.draw();
		});
	}

	public static void drawStack(ItemStack stack, int x, int y, int size) {
		if (stack.item instanceof BlockItem) {
			Block blockType = ((BlockItem) stack.item).getBlock();
			Gui.drawBlockIcon(blockType, x, y, size);
			Gui.drawCenteredTextWithShadow(Integer.toString(stack.amount), (int) (x + size * 0.9f), (int) (y + size * 0.9f), Color.WHITE);
		}
	}

	public static void drawButtonRect(int x, int y, int width, int height, boolean hovered) {
		if (!shouldRecomputeThisFrame) return;

		int frameColor = !hovered ? Color.pack(111, 111, 111, 255) : Color.pack(122, 122, 122, 255);
		int frameDarkerColor = Color.pack(0, 0, 0, 30);
		int borderColor = !hovered ? Color.pack(0, 0, 0, 155) : Color.pack(255, 255, 255, 255);
		int shineColor = Color.pack(200, 200, 200, 125);

		//int frameColor = Color.packColor(111, 111, 111, 255);

		int borderSize = 3;

		drawRect(x - borderSize, y - borderSize, width + borderSize * 2, height + borderSize * 2, borderColor);
		drawRect(x, y, width, height, frameColor);
		drawRect(x, y + height - 6, width, 6, frameDarkerColor);

		drawRect(x, y, 3, height, shineColor);
		drawRect(x + 3, y, width - 3, 3, shineColor);
	}

	public static void drawCenteredText(String text, int centerX, int centerY, int color) {
		int textWidth = FONT_RENDERER.getWidth(text);
		drawText(text, centerX - textWidth / 2, centerY - FONT_RENDERER.fontSize / 2, color);
	}

	public static void drawCenteredTextWithShadow(String text, int centerX, int centerY, int color) {
		int textWidth = FONT_RENDERER.getWidth(text);
		drawTextWithShadow(text, centerX - textWidth / 2, centerY - FONT_RENDERER.fontSize / 2, color);
	}

	public static void drawText(String text, int x, int y, int color) {
		if (!shouldRecomputeThisFrame) return;
		FONT_RENDERER.draw(fontBuffer, x, y, text, color, false);
	}

	public static void drawTextWithShadow(String text, int x, int y, int color) {
		if (!shouldRecomputeThisFrame) return;
		FONT_RENDERER.draw(fontBuffer, x, y, text, color, true);
	}
}
