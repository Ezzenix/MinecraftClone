package com.ezzenix.game.physics;

import com.ezzenix.engine.core.enums.Face;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.math.BlockPos;

public class RaycastResult {
	public BlockPos blockPos;
	public BlockType blockType;
	public Face hitFace;

	RaycastResult(BlockPos blockPos, BlockType blockType, Face hitFace) {
		this.blockPos = blockPos;
		this.blockType = blockType;
		this.hitFace = hitFace;
	}
}
