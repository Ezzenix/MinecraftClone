package com.ezzenix.hud;

import com.ezzenix.utils.textures.TextureUV;
import org.joml.Vector2f;

public class CharInfo {
    public TextureUV textureUV;
    public int x, y, width, height;

    public CharInfo(int x, int y, int width, int height, int textureWidth, int textureHeight) {
        float x0 = (float) x / (float) textureWidth;
        float x1 = (float) (x + width) / (float) textureWidth;
        float y0 = (float) (y - height) / (float) textureHeight;
        float y1 = (float) (y) / (float) textureHeight;

        this.textureUV = new TextureUV(new Vector2f(x1, y0), new Vector2f(x1, y1), new Vector2f(x0, y1), new Vector2f(x0, y0));
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;


    }
}