package com.ezzenix.utilities;

import org.joml.Vector2f;
import org.joml.Vector2i;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class TextureAtlas {
    private Map<String, List<Vector2f>> nameToUVs;
    private BufferedImage atlasImage;

    public TextureAtlas(String directoryPath) {
        nameToUVs = new HashMap<>();

        try {
            Map<String, BufferedImage> imageMap = loadImageMap(directoryPath);
            if (imageMap == null) return;
            BufferedImage atlas = createTextureAtlas(imageMap);
            this.atlasImage = atlas;

            // Save the atlas to disk
            try {
                System.out.println("Texture atlas created sucessfully!");
                ImageIO.write(atlas, "PNG", new File("textureAtlas.png"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Vector2f> getTextureUVs(String name) {
        return nameToUVs.getOrDefault(name, null);
    }

    public BufferedImage getAtlasImage() {
        return this.atlasImage;
    }

    private Map<String, BufferedImage> loadImageMap(String directoryPath) throws IOException {
        Map<String, BufferedImage> images = new HashMap<>();

        File directory = new File(directoryPath);
        File[] imageFiles = directory.listFiles();
        if (imageFiles == null) return null;

        for (File imageFile : imageFiles) {
            images.put(imageFile.getName().replace(".png", ""), ImageIO.read(imageFile));
        }

        return images;
    }

    private BufferedImage createTextureAtlas(Map<String, BufferedImage> imageMap) {
        int atlasWidth = 0;
        int atlasHeight = 0;

        // Calculate the dimensions of the texture atlas
        for (String imageName : imageMap.keySet()) {
            BufferedImage image = imageMap.get(imageName);
            atlasWidth += image.getWidth();
            atlasHeight = Math.max(atlasHeight, image.getHeight());
        }

        // Create the texture atlas image
        BufferedImage atlas = new BufferedImage(atlasWidth, atlasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = atlas.createGraphics();

        int currentX = 0;

        // Paste individual images onto the texture atlas
        for (String imageName : imageMap.keySet()) {
            BufferedImage image = imageMap.get(imageName);

            List<Vector2f> uvCoords = new ArrayList<>();
            uvCoords.add(new Vector2f(currentX, 0));
            uvCoords.add(new Vector2f(currentX, image.getHeight()));
            uvCoords.add(new Vector2f(currentX+image.getWidth(), image.getHeight()));
            uvCoords.add(new Vector2f(currentX+image.getWidth(), 0));
            this.nameToUVs.put(imageName, uvCoords);

            g.drawImage(image, currentX, 0, null);
            currentX += image.getWidth();
        }

        g.dispose();

        return atlas;
    }
}
