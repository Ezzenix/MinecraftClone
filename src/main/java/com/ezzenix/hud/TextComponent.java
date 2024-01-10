package com.ezzenix.hud;

import com.ezzenix.rendering.Mesh;
import com.ezzenix.utils.textures.TextureUV;

import static org.lwjgl.opengl.GL11.*;

public class TextComponent {
    FontRenderer fontRenderer;
    String text;
    int x, y;
    float scale;
    Mesh mesh;

    public TextComponent(FontRenderer fontRenderer, String text, int x, int y, float scale) {
        this.fontRenderer = fontRenderer;
        this.x = x;
        this.y = y;
        this.setText(text);
        this.scale = scale;
    }

    private void setText(String text) {
        this.text = text;
    }



    public void render() {
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, 800, 600, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);

        glLoadIdentity();
        glColor3f(1, 1, 1); // Set color to white

        glBindTexture(GL_TEXTURE_2D, this.fontRenderer.getAtlasTextureId());

        int offsetX = 0;
        for (int i = 0; i < this.text.length(); i++) {
            char c = this.text.charAt(i);

            Glyph glyph = this.fontRenderer.getGlyph(c);

            glBegin(GL_QUADS);

            // top-left
            glTexCoord2f(glyph.textureUV.uv1.x, glyph.textureUV.uv1.y);
            glVertex2f(offsetX + this.x, this.y);

            // bottom-left
            glTexCoord2f(glyph.textureUV.uv2.x, glyph.textureUV.uv2.y);
            glVertex2f(offsetX + this.x, this.y + glyph.height*scale);

            // bottom-right
            glTexCoord2f(glyph.textureUV.uv3.x, glyph.textureUV.uv3.y);
            glVertex2f(offsetX + this.x + glyph.width*scale, this.y + glyph.height*scale);

            // top-right
            glTexCoord2f(glyph.textureUV.uv4.x, glyph.textureUV.uv4.y);
            glVertex2f(offsetX + this.x + glyph.width*scale, this.y);

            offsetX += (int)(glyph.width*scale);

            glEnd();
        }
    }
}
