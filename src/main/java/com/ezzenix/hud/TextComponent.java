package com.ezzenix.hud;

import com.ezzenix.rendering.Mesh;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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

        List<Float> vertexList = new ArrayList<>();

        addVertex(vertexList, new Vector2f(-1f, -1f));
        addVertex(vertexList, new Vector2f(-1f, 0f));
        addVertex(vertexList, new Vector2f(1f, 0f));

        int offsetX = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            CharInfo glyph = this.fontRenderer.getGlyph(c);

            Vector2f vertTopLeft = new Vector2f(offsetX + this.x, this.y);
            Vector2f vertBottomLeft = new Vector2f(offsetX + this.x, this.y + glyph.height);
            Vector2f vertBottomRight = new Vector2f(offsetX + this.x + glyph.width, this.y + glyph.height);
            Vector2f vertTopRight = new Vector2f(offsetX + this.x + glyph.width, this.y);

            /*
            addVertex(vertexList, vertTopLeft);
            addVertex(vertexList, vertBottomLeft);
            addVertex(vertexList, vertBottomRight);
            addVertex(vertexList, vertTopRight);
            addVertex(vertexList, vertTopLeft);
            addVertex(vertexList, vertBottomRight);
            */

            // top-left
            //addVertex(vertexList, new Vector2f(offsetX + this.x, this.y));
           // buffer.put(offsetX + this.x).put(this.y);//.put(glyph.textureUV.uv1.x).put(glyph.textureUV.uv1.y);
            //glTexCoord2f(glyph.textureUV.uv1.x, glyph.textureUV.uv1.y);
            //glVertex2f(offsetX + this.x, this.y);

            // bottom-left
            //addVertex(vertexList, new Vector2f(offsetX + this.x, this.y + glyph.height));
            //buffer.put(offsetX + this.x).put(this.y + glyph.height * scale);//.put(glyph.textureUV.uv2.x).put(glyph.textureUV.uv2.y);
            //glTexCoord2f(glyph.textureUV.uv2.x, glyph.textureUV.uv2.y);
            //glVertex2f(offsetX + this.x, this.y + glyph.height*scale);

            // bottom-right
            //addVertex(vertexList, new Vector2f(offsetX + this.x + glyph.width, this.y + glyph.height));
            //buffer.put(offsetX + this.x + glyph.width * scale).put(this.y + glyph.height*scale);//.put(glyph.textureUV.uv3.x).put(glyph.textureUV.uv3.y);
            //glTexCoord2f(glyph.textureUV.uv3.x, glyph.textureUV.uv3.y);
            //glVertex2f(offsetX + this.x + glyph.width*scale, this.y + glyph.height*scale);

            // top-right
            //addVertex(vertexList, new Vector2f(offsetX + this.x + glyph.width, this.y));
            //buffer.put(offsetX + this.x + glyph.width * scale).put(this.y);//.put(glyph.textureUV.uv4.x).put(glyph.textureUV.uv4.y);
            //glTexCoord2f(glyph.textureUV.uv4.x, glyph.textureUV.uv4.y);
            //glVertex2f(offsetX + this.x + glyph.width*scale, this.y);

            offsetX += glyph.width;
        }

        float[] vertexArray = new float[vertexList.size()];
        for (int i = 0; i < vertexList.size(); i++) {
            vertexArray[i] = vertexList.get(i);
        }
        FloatBuffer vertexBuffer = createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray);
        vertexBuffer.flip();

        this.mesh = new Mesh(vertexBuffer, vertexList.size()/2);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        //glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        //glEnableVertexAttribArray(1);

        this.mesh.unbind();

        System.out.println("TEXT UPDATED!");
    }

    void addVertex(List<Float> vertexList, Vector2f position) {
        vertexList.add(position.x);
        vertexList.add(position.y);
    }

    public void render() {
        if (this.mesh == null) return;

        //glBindTexture(GL_TEXTURE_2D, this.fontRenderer.getAtlasTextureId());
        this.mesh.render();
    }
}
