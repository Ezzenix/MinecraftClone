package com.ezzenix.utils;

import org.lwjgl.system.MemoryStack;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.system.MemoryUtil.memFree;

public class ImageUtil {
    public static int loadTexture(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        ByteBuffer imageBuffer = loadImage(image);

        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Upload image data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageBuffer);

        // Generate mipmaps
        glGenerateMipmap(GL_TEXTURE_2D);

        // Release image buffer
        memFree(imageBuffer);

        return textureID;
    }

    private static ByteBuffer loadImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Get image pixels
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        // Convert ARGB to RGBA
        ByteBuffer buffer = createByteBuffer(width * height * 4);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
                buffer.put((byte) (pixel & 0xFF));         // Blue
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
            }
        }
        buffer.flip(); // Flip the buffer to prepare for reading

        return buffer;
    }
}
