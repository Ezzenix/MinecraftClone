package com.ezzenix.game;

public class BlockTypes {
    public static final BlockType STONE = new BlockType("Stone").setTexture("stone");
    public static final BlockType GRASS = new BlockType("Grass").setTextureTop("grass_block_top").setTextureSides("grass_block_side").setTextureBottom("dirt");
    public static final BlockType DIRT = new BlockType("Dirt").setTexture("dirt");
    public static final BlockType OAK_PLANKS = new BlockType("Oak Planks").setTexture("oak_planks");
}
