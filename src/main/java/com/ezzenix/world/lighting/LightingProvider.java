package com.ezzenix.world.lighting;

import com.ezzenix.math.BlockPos;
import com.ezzenix.world.World;

public abstract class LightingProvider {
	private final World world;

	public LightingProvider(World world) {
		this.world = world;
	}

	public abstract int getValue(BlockPos blockPos);
}
