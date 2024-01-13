package com.ezzenix.hud;

import org.joml.Vector2f;

public class CharInfo {
    public Vector2f[] uvCoords;
    public int x, y, width, height;

    public CharInfo(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void calculateUVs(int textureWidth, int textureHeight) {
        float x0 = (float) x / (float) textureWidth;
        float x1 = (float) (x + width) / (float) textureWidth;
        float y0 = (float) (y - height) / (float) textureHeight;
        float y1 = (float) (y) / (float) textureHeight;

        this.uvCoords = new Vector2f[4];
        this.uvCoords[0] = new Vector2f(x0, y1);
        this.uvCoords[1] = new Vector2f(x0, y0);
        this.uvCoords[2] = new Vector2f(x1, y0);
        this.uvCoords[3] = new Vector2f(x1, y1);
    }
}