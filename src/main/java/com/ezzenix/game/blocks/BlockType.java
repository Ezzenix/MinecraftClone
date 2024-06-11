package com.ezzenix.game.blocks;

import com.ezzenix.Game;
import org.joml.Vector2f;

public class BlockType {
	// Register blocks
	public static final BlockType AIR = new BlockType("Air").notSolid();
	public static final BlockType STONE = new BlockType("Stone").setTexture("stone");
	public static final BlockType GRASS_BLOCK = new BlockType("Grass Block").setTextureTop("grass_block_top").setTextureSides("grass_block_side").setTextureBottom("dirt");
	public static final BlockType DIRT = new BlockType("Dirt").setTexture("dirt");
	public static final BlockType OAK_PLANKS = new BlockType("Oak Planks").setTexture("oak_planks");
	public static final BlockType WATER = new BlockType("Water").setTexture("water").transparent().notSolid().fluid();
	public static final BlockType SAND = new BlockType("Sand").setTexture("sand");
	public static final BlockType OAK_LEAVES = new BlockType("Oak Leaves").setTexture("oak_leaves").transparent().notSolid();
	public static final BlockType OAK_LOG = new BlockType("Oak Log").setTexture("oak_log").setTextureTop("oak_log_top").setTextureBottom("oak_log_top");
	public static final BlockType GRASS = new BlockType("Grass").setTexture("grass").transparent().flower();
	public static final BlockType POPPY = new BlockType("Poppy").setTexture("poppy").transparent().flower();
	public static final BlockType GLASS = new BlockType("Glass").setTexture("glass").transparent();

	//
	private final String name;
	private final byte id;
	private boolean transparent;
	private boolean isFlower;
	private boolean isSolid;
	private boolean isFluid;

	public Vector2f[] textureUVTop;
	public Vector2f[] textureUVSides;
	public Vector2f[] textureUVBottom;

	public BlockType(String name) {
		this.name = name;
		this.transparent = false;
		this.isFlower = false;
		this.isSolid = true;
		this.isFluid = false;
		this.setTexture("stone");
		this.id = BlockRegistry.registerBlock(this);
	}

	public String getName() {
		return this.name;
	}

	// Configuration
	private BlockType setTextureTop(String textureName) {
		this.textureUVTop = Game.getInstance().blockTextures.getUV(textureName);
		return this;
	}

	private BlockType setTextureSides(String textureName) {
		this.textureUVSides = Game.getInstance().blockTextures.getUV(textureName);
		return this;
	}

	private BlockType setTextureBottom(String textureName) {
		this.textureUVBottom = Game.getInstance().blockTextures.getUV(textureName);
		return this;
	}

	private BlockType setTexture(String textureName) {
		this.setTextureTop(textureName);
		this.setTextureSides(textureName);
		this.setTextureBottom(textureName);
		return this;
	}

	private BlockType notSolid() {
		this.isSolid = false;
		return this;
	}

	private BlockType fluid() {
		this.isFluid = true;
		return this;
	}

	private BlockType flower() {
		this.isFlower = true;
		this.isSolid = false;
		return this;
	}

	private BlockType transparent() {
		this.transparent = true;
		return this;
	}

	// Getters
	public byte getId() {
		return id;
	}

	public String toString() {
		return "BlockType{" + getName() + "}";
	}

	public boolean isSolid() {
		if (this.transparent) return false;
		if (this == BlockType.AIR) return false;
		return this.isSolid;
	}

	public boolean isTransparent() {
		return this.transparent;
	}

	public boolean isFlower() {
		return this.isFlower;
	}

	public boolean isFluid() {
		return this.isFluid;
	}

	public boolean equals(Object v) {
		if (this == v) return true;
		if (v == null || this.getClass() != v.getClass()) return false;
		BlockType other = (BlockType) v;
		return this.id == other.id;
	}
}
