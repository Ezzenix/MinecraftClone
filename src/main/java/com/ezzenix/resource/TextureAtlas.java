package com.ezzenix.resource;

import org.joml.Vector2f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextureAtlas<KeyType> {
	private final HashMap<KeyType, Vector2f[]> uvMap;
	private final BufferedImage atlasImage;

	private TextureAtlas(HashMap<KeyType, BufferedImage> imageMap) {
		this.uvMap = new HashMap<>();
		this.atlasImage = createTextureAtlas(imageMap);
	}

	private BufferedImage createTextureAtlas(Map<KeyType, BufferedImage> imageMap) {
		int atlasWidth = 0;
		int atlasHeight = 0;

		// Calculate the dimensions of the texture atlas
		for (KeyType key : imageMap.keySet()) {
			BufferedImage image = imageMap.get(key);
			atlasWidth += image.getWidth();
			atlasHeight = Math.max(atlasHeight, image.getHeight());
		}

		// Create the texture atlas image
		BufferedImage atlas = new BufferedImage(atlasWidth, atlasHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = atlas.createGraphics();

		int currentX = 0;

		// Paste individual images onto the texture atlas
		for (KeyType key : imageMap.keySet()) {
			BufferedImage image = imageMap.get(key);

			Vector2f[] uvCoords = new Vector2f[4];
			uvCoords[0] = new Vector2f((float) currentX / (float) atlasWidth, 0);
			uvCoords[1] = new Vector2f((float) currentX / (float) atlasWidth, (float) image.getHeight() / (float) atlasHeight);
			uvCoords[2] = new Vector2f((float) (currentX + image.getWidth()) / (float) atlasWidth, (float) image.getHeight() / (float) atlasHeight);
			uvCoords[3] = new Vector2f((float) (currentX + image.getWidth()) / (float) atlasWidth, 0);

			uvMap.put(key, uvCoords);

			g.drawImage(image, currentX, 0, null);
			currentX += image.getWidth();
		}

		g.dispose();

		return atlas;
	}

	public Vector2f[] getUV(KeyType key) {
		return this.uvMap.get(key);
	}

	public BufferedImage getAtlasImage() {
		return this.atlasImage;
	}

	public static TextureAtlas<String> fromDirectory(String directoryPath) {
		HashMap<String, BufferedImage> imageMap = new HashMap<>();

		File directory = new File(directoryPath);
		File[] imageFiles = directory.listFiles();
		if (imageFiles == null) {
			System.err.println("TextureAtlas failed to read directory " + directoryPath);
			return null;
		}

		for (File imageFile : imageFiles) {
			try {
				imageMap.put(imageFile.getName().replace(".png", ""), ImageIO.read(imageFile));
			} catch (IOException e) {
				System.err.println("Failed to read image: " + imageFile.getAbsolutePath());
				e.printStackTrace();
			}
		}

		return new TextureAtlas<>(imageMap);
	}
}
