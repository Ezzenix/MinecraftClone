package com.ezzenix.blocks;

import com.ezzenix.enums.Direction;
import com.ezzenix.math.BlockPos;
import com.ezzenix.state.StateManager;
import com.ezzenix.state.property.Property;

public class Block {
	private final String name;

	private BlockTexture texture;
	private boolean transparent = false;
	private boolean isFlower = false;
	private boolean isSolid = true;
	private boolean isFluid = false;
	private float breakTime = 1;

	public final StateManager<BlockState> stateManager;

	private static final BlockTexture FALLBACK_TEXTURE = new BlockTexture().set("stone");

	public Block(String name) {
		this.name = name;
		this.texture = FALLBACK_TEXTURE;
		this.stateManager = new StateManager<>(getProperties(), (properties -> new BlockState(this, properties)));

		Blocks.register(this);
	}

	public String getName() {
		return this.name;
	}

	public Property<?>[] getProperties() {
		return new Property[]{};
	}

	public BlockState getDefaultState() {
		return this.stateManager.getDefaultState();
	}

	public boolean shouldRenderFace(BlockState state, BlockState otherState) {
		if (otherState.getBlock() == Blocks.AIR) return true;
		if (state.getBlock() == otherState.getBlock() && state.getBlock().isFluid()) return false;
		if (otherState.getBlock().isTransparent()) return true;
		return !otherState.getBlock().isSolid();
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

	public Block breakTime(float time) {
		this.breakTime = time;
		return this;
	}

	public Block instantBreak() {
		this.breakTime = 0;
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

	public float getBreakTime() {
		return this.breakTime;
	}

	public BlockTexture getTexture() {
		return this.texture;
	}

	@Override
	public boolean equals(Object v) {
		if (this == v) return true;
		if (v == null || this.getClass() != v.getClass()) return false;
		Block other = (Block) v;
		return this.name.equals(other.name);
	}

	@Override
	public String toString() {
		return "Block{" + getName() + "}";
	}
}
