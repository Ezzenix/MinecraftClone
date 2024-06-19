package com.ezzenix.client.resource;

public class TextureManager {
	public TextureAtlas<String> blockAtlas;

	public TextureManager() {
		this.blockAtlas = TextureAtlas.fromDirectory("src/main/resources/textures");
	}

	public void getTexture(String path) {
		
	}
}
