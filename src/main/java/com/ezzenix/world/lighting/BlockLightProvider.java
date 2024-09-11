package com.ezzenix.world.lighting;

import com.ezzenix.math.BlockPos;
import com.ezzenix.world.World;

public class BlockLightProvider extends LightingProvider {
	public BlockLightProvider(World world) {
		super(world);
	}

	@Override
	public int getValue(BlockPos blockPos) {
		return 0;
	}
}
