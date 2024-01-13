package com.ezzenix.game.blocks;

import com.ezzenix.Game;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class BlockType {
    // Register blocks
    public static final BlockType AIR = new BlockType("Air");
    public static final BlockType STONE = new BlockType("Stone").setTexture("stone");
    public static final BlockType GRASS = new BlockType("Grass").setTextureTop("grass_block_top").setTextureSides("grass_block_side").setTextureBottom("dirt");
    public static final BlockType DIRT = new BlockType("Dirt").setTexture("dirt");
    public static final BlockType OAK_PLANKS = new BlockType("Oak Planks").setTexture("oak_planks");

    //
    private final String name;
    private final byte id;

    public Vector2f[] textureUVTop;
    public Vector2f[] textureUVSides;
    public Vector2f[] textureUVBottom;

    public BlockType(String name) {
        this.id = BlockRegistry.registerBlock(this);
        this.name = name;
        this.setTexture("stone");
    }

    public String getName() {
        return this.name;
    }

    public BlockType setTextureTop(String textureName) {
        this.textureUVTop = Game.getInstance().blockTextures.getUV(textureName);
        return this;
    }

    public BlockType setTextureSides(String textureName) {
        this.textureUVSides = Game.getInstance().blockTextures.getUV(textureName);
        return this;
    }

    public BlockType setTextureBottom(String textureName) {
        this.textureUVBottom = Game.getInstance().blockTextures.getUV(textureName);
        return this;
    }

    public BlockType setTexture(String textureName) {
        this.setTextureTop(textureName);
        this.setTextureSides(textureName);
        this.setTextureBottom(textureName);
        return this;
    }

    public byte getId() {
        return id;
    }
}