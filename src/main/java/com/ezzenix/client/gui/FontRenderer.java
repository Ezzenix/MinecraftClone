package com.ezzenix.client.gui;

import com.ezzenix.engine.opengl.Texture;
import org.joml.Math;
import org.joml.Vector2f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class FontRenderer {
	private final Font font;
	public final int fontSize;
	private final Texture texture;
	private final HashMap<Character, Glyph> characterMap = new HashMap<>();
	private int width, height, lineHeight;

	public FontMetrics fontMetrics;

	public FontRenderer(Font font) {
		this.font = font;
		this.fontSize = font.getSize();
		this.texture = createFontTexture();
	}

	public static FontRenderer fromFile(File fontFile, int fontSize) {
		Font customFont = null;
		try {
			customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		if (customFont == null)
			throw new RuntimeException("Failed to load font " + fontFile.getAbsolutePath());

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(customFont);
		Font font = new Font(customFont.getFontName(), Font.PLAIN, fontSize);
		return new FontRenderer(font);
	}

	public Texture getAtlasTexture() {
		return this.texture;
	}

	public Glyph getGlyph(char c) {
		return characterMap.get(c);
	}

	public HashMap<Character, Glyph> getGlyphs() {
		return characterMap;
	}

	private Texture createFontTexture() {
		// Create fake image to get font information
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setFont(font);
		FontMetrics fontMetrics = g2d.getFontMetrics();
		this.fontMetrics = fontMetrics;

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
				characterMap.put((char) i, glyph);
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
		g2d.dispose();

		// Create the real texture
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(font);
		g2d.setColor(Color.WHITE);
		for (int i = 0; i <= 254; i++) {
			if (font.canDisplay(i)) {
				Glyph info = characterMap.get((char) i);
				if (info == null) continue;

				info.calculateUVs(width, height, fontMetrics);
				//info.calculateTextureCoordinates(width, height);
				g2d.drawString("" + (char) i, info.x, info.y);
			}
		}
		g2d.dispose();

		try {
			ImageIO.write(img, "PNG", new File("fontAtlas.png"));
		} catch (IOException ignored) {
		}

		Texture texture = new Texture(img);
		texture.setParameter(GL_TEXTURE_WRAP_S, GL_REPEAT);
		texture.setParameter(GL_TEXTURE_WRAP_T, GL_REPEAT);
		texture.setParameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		texture.setParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		return texture;
	}

	public int getTextWidth(String text, int fontSize) {
		float TEXT_SCALE = (float) fontSize / this.fontSize;

		int width = 0;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			FontRenderer.Glyph glyph = this.getGlyph(c);
			if (glyph == null) continue;

			if (c == ' ') {
				width += (int) (glyph.width * 2 * TEXT_SCALE);
				continue;
			}

			width += (int) (glyph.width * TEXT_SCALE);
		}

		return width;
	}

	public static class Glyph {
		public Vector2f[] uvCoords;
		public int x, y, width, height;

		public Glyph(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public void calculateUVs(int textureWidth, int textureHeight, FontMetrics fontMetrics) {
			int offsetY = fontMetrics.getDescent() + fontMetrics.getLeading();

			float x0 = (float) x / (float) textureWidth;
			float x1 = (float) (x + width) / (float) textureWidth;
			float y0 = (float) (y + offsetY - height) / (float) textureHeight;
			float y1 = (float) (y + offsetY) / (float) textureHeight;

			this.uvCoords = new Vector2f[4];
			this.uvCoords[0] = new Vector2f(x0, y1);
			this.uvCoords[1] = new Vector2f(x0, y0);
			this.uvCoords[2] = new Vector2f(x1, y0);
			this.uvCoords[3] = new Vector2f(x1, y1);
		}
	}
}
