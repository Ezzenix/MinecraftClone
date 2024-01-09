package com.ezzenix.game;

import com.ezzenix.Game;
import com.ezzenix.utils.textures.TextureUV;

public class BlockType {
    private String name;

    public TextureUV textureUVTop;
    public TextureUV textureUVSides;
    public TextureUV textureUVBottom;

    public BlockType(String name) {
        this.name = name;
        this.setTexture("stone");
    }

    public String getName() { return this.name; }

    public BlockType setTextureTop(String textureName) {
        this.textureUVTop = Game.getInstance().blockTextures.getTextureUVs(textureName);
        return this;
    }
    public BlockType setTextureSides(String textureName) {
        this.textureUVSides = Game.getInstance().blockTextures.getTextureUVs(textureName);
        return this;
    }
    public BlockType setTextureBottom(String textureName) {
        this.textureUVBottom = Game.getInstance().blockTextures.getTextureUVs(textureName);
        return this;
    }
    public BlockType setTexture(String textureName) {
        this.setTextureTop(textureName);
        this.setTextureSides(textureName);
        this.setTextureBottom(textureName);
        return this;
    }
}
