package com.ezzenix.hud;

import com.ezzenix.engine.opengl.Texture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class FontRenderer {
    private final Font font;
    private final int fontSize;
    private final Texture texture;
    private HashMap<Character, Glyph> characterMap = new HashMap<>();
    private int width, height, lineHeight;

    public FontRenderer(Font font) {
        this.font = font;
        this.fontSize = font.getSize();
        this.texture = createFontTexture();
    }

    public static FontRenderer fromFile(File fontFile, int fontSize) {
        Font customFont = null;
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
        } catch (FontFormatException | IOException ignored) {
        }
        if (customFont == null) return null;

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

    private Texture createFontTexture() {
        // Create fake image to get font information
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        FontMetrics fontMetrics = g2d.getFontMetrics();

        int estimatedWidth = (int) Math.sqrt(font.getNumGlyphs()) * font.getSize() + 1;
        width = 0;
        height = fontMetrics.getHeight();
        lineHeight = fontMetrics.getHeight();
        int x = 0;
        int y = (int) (fontMetrics.getHeight() * 1.4f);

        int X_SPACING = 6;

        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                // Get the sizes for each codepoint glyph, and update the actual image width and height
                Glyph glyph = new Glyph(x, y, fontMetrics.charWidth(i), fontMetrics.getHeight());
                characterMap.put((char) i, glyph);
                width = Math.max(x + fontMetrics.charWidth(i) + X_SPACING, width);

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
        for (int i = 0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                Glyph info = characterMap.get((char) i);
                info.calculateUVs(width, height);
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
}
