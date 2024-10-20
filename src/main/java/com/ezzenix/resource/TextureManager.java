package com.ezzenix.resource;

import com.ezzenix.engine.opengl.Texture;
import com.ezzenix.util.Identifier;
import com.google.common.collect.Maps;

import java.awt.image.BufferedImage;
import java.util.Map;

import static org.lwjgl.opengl.GL30.GL_NEAREST;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_MAG_FILTER;

public class TextureManager {
	public TextureAtlas<String> blockAtlas;

	public Map<Identifier, Texture> textures = Maps.newHashMap();
	public Map<Identifier, TextureAtlas<String>> atlases = Maps.newHashMap();

	public TextureManager() {
		this.blockAtlas = getAtlas(Identifier.of("blocks"));
		this.blockAtlas.getTexture().setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	}

	private Texture loadTexture(Identifier id) {
		BufferedImage bufferedImage = ResourceManager.loadImage("textures/" + id.getPath() + ".png");
		return new Texture(bufferedImage);
	}

	public Texture getTexture(Identifier id) {
		return textures.computeIfAbsent(id, v -> loadTexture(id));
	}

	public TextureAtlas<String> getAtlas(Identifier id) {
		return atlases.computeIfAbsent(id, v -> TextureAtlas.fromDirectory(id.getPath()));
	}
}
