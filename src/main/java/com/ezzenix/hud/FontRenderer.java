package com.ezzenix.hud;

import com.ezzenix.utils.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

public class FontRenderer {
    private final int fontSize;
    private final String fontPath;
    private final int textureId;
    private HashMap<Character, CharInfo> characterMap = new HashMap<>();
    private int width, height, lineHeight;

    public FontRenderer(Font font) {
        this.fontPath = "C:/Windows/Fonts/Arial.ttf";
        this.fontSize = 64;

        this.textureId = createFontTexture();
    }

    public int getAtlasTextureId() {
        return this.textureId;
    }

    public CharInfo getGlyph(char c) {
        return characterMap.get(c);
    }

    private int createFontTexture() {
        Font font = new Font(fontPath, Font.PLAIN, fontSize);

        // Create fake image to get font information
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        FontMetrics fontMetrics = g2d.getFontMetrics();

        int estimatedWidth = (int)Math.sqrt(font.getNumGlyphs()) * font.getSize() + 1;
        width = 0;
        height = fontMetrics.getHeight();
        lineHeight = fontMetrics.getHeight();
        int x = 0;
        int y = (int)(fontMetrics.getHeight() * 1.4f);

        for (int i=0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                // Get the sizes for each codepoint glyph, and update the actual image width and height
                CharInfo charInfo = new CharInfo(x, y, fontMetrics.charWidth(i), fontMetrics.getHeight(), width, height);
                characterMap.put((char)i, charInfo);
                width = Math.max(x + fontMetrics.charWidth(i), width);

                x += charInfo.width;
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
        for (int i=0; i < font.getNumGlyphs(); i++) {
            if (font.canDisplay(i)) {
                CharInfo info = characterMap.get((char)i);
                //info.calculateTextureCoordinates(width, height);
                g2d.drawString("" + (char)i, info.x, info.y);
            }
        }
        g2d.dispose();

        try {
            ImageIO.write(img, "PNG", new File("fontAtlas.png"));
        } catch (IOException ignored) {}

        int textureId = ImageUtil.loadTexture(img);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        return textureId;
    }
}
