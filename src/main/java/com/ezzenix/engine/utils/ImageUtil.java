package com.ezzenix.engine.utils;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.BufferUtils.createByteBuffer;

public class ImageUtil {
    public static ByteBuffer parseBufferedImage(BufferedImage image) {
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
