package com.ezzenix.resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.BufferUtils.createByteBuffer;

public class ResourceManager {
	static {
		String resourceDir = "textures/blocks";

		try {
			List<String> paths = getResourceFiles(resourceDir);
			for (String path : paths) {
				System.out.println("Path: " + path);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static List<String> getResourceFiles(String path) throws IOException {
		List<String> filenames = new ArrayList<>();

		try (
			InputStream in = getResourceAsStream(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String resource;

			while ((resource = br.readLine()) != null) {
				filenames.add(resource);
			}
		}

		return filenames;
	}

	private static InputStream getResourceAsStream(String resource) {
		final InputStream in
			= getContextClassLoader().getResourceAsStream(resource);

		return in == null ? ResourceManager.class.getResourceAsStream(resource) : in;
	}

	private static ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public static File getFile(String path) {
		return new File("src/main/resources/" + path);
	}

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

	public static String getFileResourcePath(File file) {
		String resourceBasePath = new File("src/main/resources").getAbsolutePath();

		String absolutePath = file.getAbsolutePath();

		if (!absolutePath.startsWith(resourceBasePath)) {
			throw new IllegalArgumentException("File is not within the resources directory.");
		}

		String relativePath = absolutePath.substring(resourceBasePath.length() + 1); // +1 to remove the file separator
		return relativePath.replace(File.separator, "/"); // Use forward slashes for resource paths
	}

	public static BufferedImage loadImage(File file) {
		String path = getFileResourcePath(file);
		return loadImage(path);
	}

	public static BufferedImage loadImage(String path) {
		System.out.println(path);
		InputStream inputStream = ResourceManager.class.getClassLoader().getResourceAsStream(path);
		if (inputStream == null) {
			System.err.println("Error loading resource: " + path);
			throw new RuntimeException("Resource not found: " + path);
		}
		try {
			return ImageIO.read(inputStream);
		} catch (IOException e) {
			System.err.println("Failed to read image: " + path);
			throw new RuntimeException(e);
		}
		//return loadImage(new File("src/main/resources/" + path));
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
