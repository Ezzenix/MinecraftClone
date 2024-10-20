package com.ezzenix.resource;

import com.ezzenix.engine.opengl.Texture;
import org.joml.Vector2f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TextureAtlas<KeyType> {
	private final HashMap<KeyType, Vector2f[]> uvMap;
	private final BufferedImage atlasImage;
	private final Texture texture;

	private TextureAtlas(Map<KeyType, BufferedImage> imageMap) {
		this.uvMap = new HashMap<>();
		this.atlasImage = createTextureAtlas(imageMap);
		this.texture = new Texture(this.atlasImage);
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

	public Texture getTexture() {
		return this.texture;
	}

	private static TextureAtlas<String> fromFiles(File[] files) {
		return new TextureAtlas<>(Arrays.stream(files).collect(Collectors.toMap(
			file -> file.getName().replace(".png", ""),
			ResourceManager::loadImage
		)));
	}

	public static TextureAtlas<String> fromDirectory(String directoryPath) {
		File directory = new File("src/main/resources/textures/" + directoryPath);
		File[] imageFiles = directory.listFiles();
		if (imageFiles == null)
			throw new RuntimeException("Failed to read directory " + directoryPath);

		return TextureAtlas.fromFiles(imageFiles);
	}

	public static TextureAtlas<String> fromFilePaths(List<String> filePaths) {
		File[] imageFiles = new File[filePaths.size()];
		for (int i = 0; i < filePaths.size(); i++) {
			String path = filePaths.get(i);
			imageFiles[i] = new File("src/main/resources/textures/" + path);
		}

		return TextureAtlas.fromFiles(imageFiles);
	}
}
