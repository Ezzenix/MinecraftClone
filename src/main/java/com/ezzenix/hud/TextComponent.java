package com.ezzenix.hud;

import com.ezzenix.rendering.Mesh;
import com.ezzenix.utils.textures.TextureUV;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

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
        this.scale = scale;
        this.setText(text);
    }

    private void setText(String text) {
        if (this.text != null && this.text.equals(text)) return;
        this.text = text;

        int vertexCount = text.length() * 4;
        FloatBuffer buffer = createFloatBuffer(vertexCount * 4);

        int offsetX = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            Glyph glyph = this.fontRenderer.getGlyph(c);

            // top-left
            buffer.put(offsetX + this.x).put(this.y);//.put(glyph.textureUV.uv1.x).put(glyph.textureUV.uv1.y);
            //glTexCoord2f(glyph.textureUV.uv1.x, glyph.textureUV.uv1.y);
            //glVertex2f(offsetX + this.x, this.y);

            // bottom-left
            buffer.put(offsetX + this.x).put(this.y + glyph.height * scale);//.put(glyph.textureUV.uv2.x).put(glyph.textureUV.uv2.y);
            //glTexCoord2f(glyph.textureUV.uv2.x, glyph.textureUV.uv2.y);
            //glVertex2f(offsetX + this.x, this.y + glyph.height*scale);

            // bottom-right
            buffer.put(offsetX + this.x + glyph.width * scale).put(this.y + glyph.height*scale);//.put(glyph.textureUV.uv3.x).put(glyph.textureUV.uv3.y);
            //glTexCoord2f(glyph.textureUV.uv3.x, glyph.textureUV.uv3.y);
            //glVertex2f(offsetX + this.x + glyph.width*scale, this.y + glyph.height*scale);

            // top-right
            buffer.put(offsetX + this.x + glyph.width * scale).put(this.y);//.put(glyph.textureUV.uv4.x).put(glyph.textureUV.uv4.y);
            //glTexCoord2f(glyph.textureUV.uv4.x, glyph.textureUV.uv4.y);
            //glVertex2f(offsetX + this.x + glyph.width*scale, this.y);

            offsetX += (int) (glyph.width * scale);
        }
        buffer.flip();

        this.mesh = new Mesh(buffer, vertexCount, GL_QUADS);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        //glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        //glEnableVertexAttribArray(1);

        this.mesh.unbind();
    }

    public void render() {
        if (this.mesh == null) return;

        glBindTexture(GL_TEXTURE_2D, this.fontRenderer.getAtlasTextureId());
        this.mesh.render();
    }
}
