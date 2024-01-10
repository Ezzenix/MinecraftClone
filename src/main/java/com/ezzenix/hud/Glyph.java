package com.ezzenix.hud;

import com.ezzenix.utils.textures.TextureUV;

public class Glyph {
    public TextureUV textureUV;
    public int width, height;

    public Glyph(TextureUV textureUV, int width, int height) {
        this.textureUV = textureUV;
        this.width = width;
        this.height = height;
    }
}