package com.ezzenix.game.blocks;

import com.ezzenix.Game;
import org.joml.Vector2f;

public class BlockType {
    // Register blocks
    public static final BlockType AIR = new BlockType("Air");
    public static final BlockType STONE = new BlockType("Stone").setTexture("stone");
    public static final BlockType GRASS = new BlockType("Grass").setTextureTop("grass_block_top").setTextureSides("grass_block_side").setTextureBottom("dirt");
    public static final BlockType DIRT = new BlockType("Dirt").setTexture("dirt");
    public static final BlockType OAK_PLANKS = new BlockType("Oak Planks").setTexture("oak_planks");
    public static final BlockType WATER = new BlockType("Water").setTexture("water").setTransparent(true);
    public static final BlockType SAND = new BlockType("Sand").setTexture("sand");
    public static final BlockType OAK_LEAVES = new BlockType("Oak Leaves").setTexture("oak_leaves");
    public static final BlockType OAK_LOG = new BlockType("Oak Log").setTexture("oak_log").setTextureTop("oak_log_top").setTextureBottom("oak_log_top");

    //
    private final String name;
    private final byte id;
    private boolean transparent;

    public Vector2f[] textureUVTop;
    public Vector2f[] textureUVSides;
    public Vector2f[] textureUVBottom;

    public BlockType(String name) {
        this.id = BlockRegistry.registerBlock(this);
        this.name = name;
        this.transparent = false;
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

    public String toString() {
        return "BlockType{"+getName()+"}";
    }

    public boolean isSolid() {
        return this != BlockType.AIR;
    }

    public boolean isTransparent() {return this.transparent; }

    public BlockType setTransparent(boolean value) {
        this.transparent = value;
        return this;
    }
}
