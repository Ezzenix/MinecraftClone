package com.ezzenix.gui;

import com.ezzenix.engine.opengl.Texture;
import com.ezzenix.rendering.util.BufferBuilder;
import com.ezzenix.rendering.util.VertexBuffer;
import org.joml.Math;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.lwjgl.opengl.GL30.*;

public class FontRenderer {
	private final Font font;
	public final int fontSize;
	private Texture texture;
	private final HashMap<Character, Glyph> glyphs = new HashMap<>();
	private int width, height, lineHeight;

	public FontRenderer(Font font) {
		this.font = font;
		this.fontSize = font.getSize();

		this.setup();
	}

	public FontRenderer(File file, int fontSize) {
		this(fontFromFile(file, fontSize));
	}

	private static Font fontFromFile(File file, int fontSize) {
		Font customFont = null;
		try {
			customFont = Font.createFont(Font.TRUETYPE_FONT, file);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		if (customFont == null)
			throw new RuntimeException("Failed to load font " + file.getAbsolutePath());

		GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
		return new Font(customFont.getFontName(), Font.PLAIN, fontSize);
	}

	public Texture getTexture() {
		return this.texture;
	}

	public Glyph getGlyph(char c) {
		return glyphs.get(c);
	}

	public HashMap<Character, Glyph> getGlyphs() {
		return glyphs;
	}

	private void setup() {
		// Get font metrics
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setFont(font);
		FontMetrics fontMetrics = g2d.getFontMetrics();
		g2d.dispose();

		// Setup glyphs and calculate texture size
		int estimatedWidth = (int) Math.sqrt(font.getNumGlyphs()) * font.getSize() + 1;
		width = 0;
		height = fontMetrics.getHeight();
		lineHeight = fontMetrics.getHeight();
		int x = 0;
		int y = (int) (fontMetrics.getHeight() * 1.4f);

		int X_SPACING = 6;

		for (int i = 0; i <= 254; i++) {
			if (font.canDisplay(i)) {
				int charWidth = fontMetrics.charWidth(i);
				int charHeight = fontMetrics.getHeight();

				if (charWidth == 0 || charHeight == 0) continue;


				// Get the sizes for each codepoint glyph, and update the actual image width and height
				Glyph glyph = new Glyph(x, y, charWidth, charHeight);
				glyphs.put((char) i, glyph);
				width = Math.max(x + charWidth + X_SPACING, width);

				x += glyph.width + X_SPACING;
				if (x > estimatedWidth) {
					x = 0;
					y += (int) (fontMetrics.getHeight() * 1.4f);
					height += (int) (fontMetrics.getHeight() * 1.4f);
				}
			}
		}
		height += (int) (fontMetrics.getHeight() * 1.4f);

		// Create texture
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(font);
		g2d.setColor(java.awt.Color.WHITE);
		for (int i = 0; i <= 254; i++) {
			if (font.canDisplay(i)) {
				Glyph info = glyphs.get((char) i);
				if (info == null) continue;
				info.calculateUVs(width, height, fontMetrics);
				g2d.drawString("" + (char) i, info.x, info.y);
			}
		}
		g2d.dispose();

		try {
			ImageIO.write(img, "PNG", new File("temp_fontAtlas.png"));
		} catch (IOException ignored) {
		}

		this.texture = new Texture(img);
		this.texture.setParameter(GL_TEXTURE_WRAP_S, GL_REPEAT);
		this.texture.setParameter(GL_TEXTURE_WRAP_T, GL_REPEAT);
		this.texture.setParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		this.texture.setParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
	}

	public int getWidth(String text) {
		int width = 0;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			FontRenderer.Glyph glyph = this.getGlyph(c);
			if (glyph == null) continue;

			if (c == ' ') {
				width += glyph.width * 2;
				continue;
			}

			width += glyph.width;
		}

		return width;
	}

	public void draw(BufferBuilder builder, int x, int y, String text, int color, boolean shadow) {
		if (shadow) {
			float a = Color.unpack(color)[3];
			draw(builder, x - 1, y + 2, text, Color.pack(0.03f, 0.03f, 0.03f, 0.4f * a), false);
		}

		y -= 5; // TODO: Make this a better way, font offset to align top of text

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			FontRenderer.Glyph glyph = getGlyph(c);
			if (glyph == null) continue;

			if (c == ' ') {
				x += glyph.width * 2; // space doesn't have to be actually rendered
				continue;
			}

			int width = glyph.width;
			int height = glyph.height;

			builder.vertex(x, y).texture(glyph.minU, glyph.minV).color(color);
			builder.vertex(x, y + height).texture(glyph.minU, glyph.maxV).color(color);
			builder.vertex(x + width, y + height).texture(glyph.maxU, glyph.maxV).color(color);

			builder.vertex(x + width, y + height).texture(glyph.maxU, glyph.maxV).color(color);
			builder.vertex(x + width, y).texture(glyph.maxU, glyph.minV).color(color);
			builder.vertex(x, y).texture(glyph.minU, glyph.minV).color(color);

			x += width;
		}
	}

	public static class Glyph {
		public int x, y, width, height;
		public float minU, maxU, minV, maxV;

		public Glyph(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public void calculateUVs(int textureWidth, int textureHeight, FontMetrics fontMetrics) {
			int offsetY = fontMetrics.getDescent() + fontMetrics.getLeading();

			this.minU = (float) x / (float) textureWidth;
			this.maxU = (float) (x + width) / (float) textureWidth;
			this.minV = (float) (y + offsetY - height) / (float) textureHeight;
			this.maxV = (float) (y + offsetY) / (float) textureHeight;
		}
	}
}
