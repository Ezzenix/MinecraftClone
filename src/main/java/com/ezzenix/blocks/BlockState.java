package com.ezzenix.blocks;

import com.ezzenix.state.State;
import com.ezzenix.state.property.Property;

import java.util.Map;

public class BlockState extends State {
	private final Block block;

	public BlockState(Block block, Map<Property<?>, Comparable<?>> properties) {
		super(properties);

		this.block = block;
	}

	public Block getBlock() {
		return this.block;
	}
}
