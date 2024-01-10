package com.ezzenix.utils.textures;

import org.joml.Vector2f;
import org.joml.Vector2i;

import java.text.DecimalFormat;

public class TextureUV {
    public final Vector2f uv1;
    public final Vector2f uv2;
    public final Vector2f uv3;
    public final Vector2f uv4;

    public TextureUV(Vector2f uv1, Vector2f uv2, Vector2f uv3, Vector2f uv4) {
        this.uv1 = uv1;
        this.uv2 = uv2;
        this.uv3 = uv3;
        this.uv4 = uv4;
    }

    public TextureUV(Vector2i uv1, Vector2i uv2, Vector2i uv3, Vector2i uv4, int totalWidth, int totalHeight) {
        this(
                new Vector2f((float) uv1.x / totalWidth, (float) uv1.y / totalHeight),
                new Vector2f((float) uv2.x / totalWidth, (float) uv2.y / totalHeight),
                new Vector2f((float) uv3.x / totalWidth, (float) uv3.y / totalHeight),
                new Vector2f((float) uv4.x / totalWidth, (float) uv4.y / totalHeight)
        );
    }

    public String toString() {
        return "UV1: " + this.uv1.toString(new DecimalFormat("#.##"))
                + "  UV2: " + this.uv2.toString(new DecimalFormat("#.##"))
                + "  UV3: " + this.uv3.toString(new DecimalFormat("#.##"))
                + "  UV4: " + this.uv4.toString(new DecimalFormat("#.##"));
    }
}
