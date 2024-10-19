package com.ezzenix.blocks;

import com.ezzenix.Client;
import org.joml.Vector2f;

public class BlockTexture {
	private Vector2f[] topUV;
	private Vector2f[] sideUV;
	private Vector2f[] bottomUV;

	public BlockTexture() {
		this.set("stone");
	}

	private Vector2f[] getUV(String textureName) {
		return Client.getTextureManager().blockAtlas.getUV(textureName);
	}

	public BlockTexture top(String textureName) {
		this.topUV = getUV(textureName);
		return this;
	}

	public BlockTexture topBottom(String textureName) {
		this.top(textureName);
		this.bottom(textureName);
		return this;
	}

	public BlockTexture side(String textureName) {
		this.sideUV = getUV(textureName);
		return this;
	}

	public BlockTexture bottom(String textureName) {
		this.bottomUV = getUV(textureName);
		return this;
	}

	public BlockTexture set(String textureName) {
		this.top(textureName);
		this.side(textureName);
		this.bottom(textureName);
		return this;
	}

	public Vector2f[] getTopUV() {
		return this.topUV;
	}

	public Vector2f[] getSideUV() {
		return this.sideUV;
	}

	public Vector2f[] getBottomUV() {
		return this.bottomUV;
	}
}
