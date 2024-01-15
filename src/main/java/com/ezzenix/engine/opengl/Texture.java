package com.ezzenix.engine.opengl;

import com.ezzenix.engine.utils.ImageUtil;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL45.glGenerateTextureMipmap;

public class Texture {
    private final int id;

    public Texture(ByteBuffer data, int width, int height) {
        id = glGenTextures();
        bind();
        setParameter(GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        setParameter(GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
        setParameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
    }

    public Texture(BufferedImage image) {
        this(ImageUtil.parseBufferedImage(image), image.getWidth(), image.getHeight());
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void setParameter(int name, int value) {
        glTexParameteri(GL_TEXTURE_2D, name, value);
    }

    public void delete() {
        glDeleteTextures(id);
    }

    public int getId() {
        return id;
    }

    public void generateMipmap() {
        glGenerateTextureMipmap(id);
    }
}
