package com.ezzenix.resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.lwjgl.BufferUtils.createByteBuffer;

public class ResourceManager {
	public static String readFile(String path) {
		InputStream inputStream = ResourceManager.class.getClassLoader().getResourceAsStream(path);
		if (inputStream == null) {
			System.err.println("Error reading resource: " + path);
			return null;
		}

		String source;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			source = reader.lines().collect(Collectors.joining("\n"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return source;
	}

	public static BufferedImage loadImage(String path) {
		path = "src/main/resources/" + path;

		File file = new File(path);

		BufferedImage image;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return image;
	}

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

		return buffer.flip();
	}
}
