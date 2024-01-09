package com.ezzenix.utils.textures;

import org.joml.Vector2f;

public class TextureUV {
    public Vector2f uv1;
    public Vector2f uv2;
    public Vector2f uv3;
    public Vector2f uv4;

    public TextureUV(Vector2f uv1, Vector2f uv2, Vector2f uv3, Vector2f uv4) {
        this.uv1 = uv1;
        this.uv2 = uv2;
        this.uv3 = uv3;
        this.uv4 = uv4;
    }
}
