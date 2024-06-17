package com.ezzenix.client.gui.library.components;

import com.ezzenix.client.rendering.Renderer;
import com.ezzenix.game.blocks.BlockType;

public class GuiBlockIcon extends GuiImage {
	public BlockType blockType = BlockType.STONE;

	public GuiBlockIcon() {
		super();

		this.texture = Renderer.getWorldRenderer().blockTexture;
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void rebuild() {
		this.uvCoords = blockType.textureUVSides;
		super.rebuild();
	}
}
