package com.ezzenix.blocks;

import com.ezzenix.state.State;
import com.ezzenix.state.property.Property;

import java.util.Map;
import java.util.Objects;

public class BlockState extends State<BlockState> {
	private final Block block;

	public BlockState(Block block, Map<Property<?>, Comparable<?>> properties) {
		super(properties);

		this.block = block;
	}

	public Block getBlock() {
		return this.block;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BlockState other = (BlockState) o;
		return this.block == other.block && getProperties().equals(other.getProperties());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getBlock().getName(), getProperties());
	}

	@Override
	public String toString() {
		return "BlockState(" + this.getBlock().getName() + ")";
	}
}
