package com.ezzenix.blocks;

import com.ezzenix.state.property.IntProperty;
import com.ezzenix.state.property.Property;

public class FluidBlock extends Block {
	public static final IntProperty LEVEL = new IntProperty("level", 1, 8);

	public FluidBlock(String name) {
		super(name);

		this.setDefaultState(this.getDefaultState().with(LEVEL, 8));
	}

	@Override
	public Property<?>[] getProperties() {
		return new Property[]{LEVEL};
	}
}
