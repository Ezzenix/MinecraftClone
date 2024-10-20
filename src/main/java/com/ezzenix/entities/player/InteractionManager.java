package com.ezzenix.entities.player;

import com.ezzenix.Client;
import com.ezzenix.blocks.BlockState;
import com.ezzenix.blocks.Blocks;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.math.BlockPos;
import com.ezzenix.physics.Raycast;

public class InteractionManager {
	private boolean isBreaking = false;
	private BlockPos breakingPos = null;
	private float breakingStarted = -1f;

	public InteractionManager() {

	}

	public void setBreakingPos(BlockPos blockPos) {
		if (blockPos == null && isBreaking) {
			isBreaking = false;
			breakingPos = null;
			breakingStarted = -1;
		} else if (blockPos != null) {
			if (blockPos.equals(breakingPos)) return;
			isBreaking = true;
			breakingPos = blockPos;
			breakingStarted = Scheduler.getClock();
		}
	}

	public BlockPos getBreakingPos() {
		return breakingPos;
	}

	public float getBreakingProgress() {
		if (breakingStarted == -1) return 0f;

		BlockState blockState = Client.getWorld().getBlockState(breakingPos);
		if (blockState == null) return 0f;

		float breakTime = blockState.getBlock().getBreakTime();
		float passed = Scheduler.getClock() - breakingStarted;
		return Math.clamp(passed / breakTime, 0, 1);
	}

	public void update() {
		boolean leftMb = Client.getMouse().isMouseButton1Down();
		if (leftMb) {
			Raycast raycast = Client.getPlayer().raycast();
			BlockPos targetPos = raycast != null ? raycast.blockPos : null;
			setBreakingPos(targetPos);
		} else {
			setBreakingPos(null);
		}

		if (breakingPos != null && getBreakingProgress() == 1) {
			Client.getWorld().setBlockState(breakingPos, Blocks.AIR.getDefaultState());
		}
	}
}

