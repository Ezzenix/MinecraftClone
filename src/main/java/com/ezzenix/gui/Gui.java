package com.ezzenix.gui;

import com.ezzenix.Client;
import com.ezzenix.blocks.Block;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import com.ezzenix.inventory.ItemStack;
import com.ezzenix.item.BlockItem;
import com.ezzenix.rendering.util.BufferBuilder;
import com.ezzenix.rendering.util.RenderLayer;
import com.ezzenix.rendering.util.VertexBuffer;
import com.ezzenix.rendering.util.VertexFormat;
import com.ezzenix.resource.ResourceManager;
import org.joml.Vector2f;

import static org.lwjgl.opengl.GL30.*;

public class Gui {

	public static final FontRenderer FONT_RENDERER = new FontRenderer(ResourceManager.getFile("fonts/minecraft.ttf"), 18);

	private static final Shader RECTANGLE_SHADER = new Shader("gui/frame");
	private static final RenderLayer RECTANGLE_LAYER = new RenderLayer(RECTANGLE_SHADER).format(new VertexFormat(GL_FLOAT, 2, GL_INT, 1)).blend(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA).setExpectedBufferSize(4096);

	private static final Shader TEXTURE_SHADER = new Shader("gui/image");
	private static final RenderLayer TEXTURE_LAYER = new RenderLayer(TEXTURE_SHADER).format(new VertexFormat(GL_FLOAT, 2, GL_FLOAT, 2)).blend(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA).setExpectedBufferSize(4096);

	private static final Shader FONT_SHADER = new Shader("gui/text");
	private static final RenderLayer FONT_LAYER = new RenderLayer(FONT_SHADER).format(new VertexFormat(GL_FLOAT, 2, GL_FLOAT, 2, GL_INT, 1)).blend(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA).setExpectedBufferSize(10000);

	private static final BufferBuilder.Immediate immediate = new BufferBuilder.Immediate();

	static {
		FONT_SHADER.setTexture(0, FONT_RENDERER.getTexture());
	}

	public static void drawRect(int x, int y, int width, int height, int color) {
		BufferBuilder builder = immediate.getBuilder(RECTANGLE_LAYER);

		builder.vertex(x, y).color(color).next();
		builder.vertex(x, y + height).color(color).next();
		builder.vertex(x + width, y + height).color(color).next();
		builder.vertex(x + width, y + height).color(color).next();
		builder.vertex(x + width, y).color(color).next();
		builder.vertex(x, y).color(color).next();

		immediate.draw(RECTANGLE_LAYER);
	}

	public static void drawRect(int x, int y, int width, int height, float r, float g, float b, float a) {
		drawRect(x, y, width, height, Color.pack(r, g, b, a));
	}

	public static void drawRectGradient(int x, int y, int width, int height, float r, float g, float b, float a, float r2, float g2, float b2, float a2) {
		BufferBuilder builder = immediate.getBuilder(RECTANGLE_LAYER);

		int colorTop = Color.pack(r, g, b, a);
		int colorBottom = Color.pack(r2, g2, b2, a2);

		builder.vertex(x, y).color(colorTop).next();
		builder.vertex(x, y + height).color(colorBottom).next();
		builder.vertex(x + width, y + height).color(colorBottom).next();
		builder.vertex(x + width, y + height).color(colorBottom).next();
		builder.vertex(x + width, y).color(colorTop).next();
		builder.vertex(x, y).color(colorTop).next();

		immediate.draw(RECTANGLE_LAYER);
	}

	public static void drawTexture(Texture texture, int x, int y, int width, int height) {
		BufferBuilder builder = immediate.getBuilder(TEXTURE_LAYER);

		builder.vertex(x, y).texture(0, 0).next();
		builder.vertex(x, y + height).texture(0, 1).next();
		builder.vertex(x + width, y + height).texture(1, 1).next();
		builder.vertex(x + width, y + height).texture(1, 1).next();
		builder.vertex(x + width, y).texture(1, 0).next();
		builder.vertex(x, y).texture(0, 0).next();

		TEXTURE_SHADER.setTexture(0, texture);
		immediate.draw(TEXTURE_LAYER);
	}

	public static void drawBlockIcon(Block blockType, int x, int y, int size) {
		BufferBuilder builder = immediate.getBuilder(TEXTURE_LAYER);

		Vector2f[] uv = blockType.getTexture().getSideUV();

		builder.vertex(x, y).texture(uv[0]).next();
		builder.vertex(x, y + size).texture(uv[1]).next();
		builder.vertex(x + size, y + size).texture(uv[2]).next();
		builder.vertex(x + size, y + size).texture(uv[2]).next();
		builder.vertex(x + size, y).texture(uv[3]).next();
		builder.vertex(x, y).texture(uv[0]).next();

		TEXTURE_SHADER.setTexture(0, Client.getTextureManager().blockAtlas.getTexture());
		immediate.draw(TEXTURE_LAYER);
	}

	public static void drawStack(ItemStack stack, int x, int y, int size) {
		if (stack.item instanceof BlockItem) {
			Block blockType = ((BlockItem) stack.item).getBlock();
			Gui.drawBlockIcon(blockType, x, y, size);
			Gui.drawCenteredTextWithShadow(Integer.toString(stack.amount), (int) (x + size * 0.9f), (int) (y + size * 0.9f), Color.WHITE);
		}
	}

	public static void drawButtonRect(int x, int y, int width, int height, boolean hovered) {
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

	public static void drawText(String text, int x, int y, int color, boolean shadow) {
		if (text.trim().isEmpty()) return;
		BufferBuilder builder = immediate.getBuilder(FONT_LAYER);
		FONT_RENDERER.draw(builder, x, y, text, color, shadow);
		immediate.draw(FONT_LAYER);
	}

	public static void drawText(String text, int x, int y, int color) {
		drawText(text, x, y, color, false);
	}

	public static void drawTextWithShadow(String text, int x, int y, int color) {
		drawText(text, x, y, color, true);
	}
}
