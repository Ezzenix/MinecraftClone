package com.ezzenix.blocks;

public class Block {
	private final String name;
	private final byte id;

	private BlockTexture texture;

	private boolean transparent = false;
	private boolean isFlower = false;
	private boolean isSolid = true;
	private boolean isFluid = false;

	private static final BlockTexture FALLBACK_TEXTURE = new BlockTexture().set("stone");

	public Block(String name) {
		this.name = name;
		this.id = Blocks.register(this);
		this.texture = FALLBACK_TEXTURE;
	}

	public String getName() {
		return this.name;
	}

	// Configuration
	public Block notSolid() {
		this.isSolid = false;
		return this;
	}

	public Block fluid() {
		this.isFluid = true;
		return this;
	}

	public Block flower() {
		this.isFlower = true;
		this.isSolid = false;
		return this;
	}

	public Block transparent() {
		this.transparent = true;
		return this;
	}

	public Block setTexture(BlockTexture texture) {
		this.texture = texture;
		return this;
	}

	public Block setTexture(String textureName) {
		this.texture = new BlockTexture().set(textureName);
		return this;
	}

	// Getters
	public byte getId() {
		return id;
	}

	public String toString() {
		return "Block{" + getName() + "}";
	}

	public boolean isSolid() {
		if (this.transparent) return false;
		if (this == Blocks.AIR) return false;
		return this.isSolid;
	}

	public boolean isWalkthrough() {
		if (this == Blocks.AIR) return true;
		if (this.isFluid()) return true;
		if (this.isFlower) return true;
		return false;
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

	public BlockTexture getTexture() {
		return this.texture;
	}

	public boolean equals(Object v) {
		if (this == v) return true;
		if (v == null || this.getClass() != v.getClass()) return false;
		Block other = (Block) v;
		return this.id == other.id;
	}
}
