package com.ezzenix.client.rendering.chunkbuilder.builder;

import com.ezzenix.blocks.Block;
import com.ezzenix.blocks.Blocks;
import com.ezzenix.enums.Direction;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.LocalPosition;
import com.ezzenix.world.Chunk;
import org.joml.Vector3i;

public class VoxelFace {
	public Direction direction;
	public byte blockId;

	public LocalPosition localPosition;

	public float ao1 = 0;
	public float ao2 = 0;
	public float ao3 = 0;
	public float ao4 = 0;

	public VoxelFace(LocalPosition localPosition, Direction direction, byte blockId) {
		this.localPosition = localPosition;
		this.direction = direction;
		this.blockId = blockId;
	}

	private int isBlockAt(Chunk chunk, Direction direction, Vector3i offset) {
		applyOffsetRotation(direction, offset);

		BlockPos worldPos = BlockPos.from(chunk, localPosition.add(offset.x, offset.y, offset.z));
		Block blockType = chunk.getBlock(worldPos);

		if (blockType == null) return 0;
		return blockType == Blocks.AIR || !blockType.isSolid() ? 0 : 1;
	}

	private void applyOffsetRotation(Direction direction, Vector3i offset) {
		switch (direction) {
			case DOWN -> offset.set(-offset.x, -offset.y, -offset.z);
			case NORTH -> offset.set(offset.x, offset.z, -offset.y);
			case SOUTH -> offset.set(-offset.x, -offset.z, offset.y);
			case EAST -> offset.set(offset.y, -offset.x, offset.z);
			case WEST -> offset.set(-offset.y, offset.x, -offset.z);
		}
		;
	}

	private float solveAO(int side1, int side2, int corner) {
		if (side1 == 1 && side2 == 1) {
			return 1;
		}
		return 1 - ((float) (3 - (side1 + side2 + corner)) / 3);
	}

	public void calculateAO(Chunk chunk) {
		Block type = Blocks.getBlockFromId(blockId);
		if (type.isTransparent()) return;

		int W = isBlockAt(chunk, this.direction, new Vector3i(-1, 1, 0));
		int NW = isBlockAt(chunk, this.direction, new Vector3i(-1, 1, -1));
		int N = isBlockAt(chunk, this.direction, new Vector3i(0, 1, -1));
		int NE = isBlockAt(chunk, this.direction, new Vector3i(1, 1, -1));
		int E = isBlockAt(chunk, this.direction, new Vector3i(1, 1, 0));
		int SE = isBlockAt(chunk, this.direction, new Vector3i(1, 1, 1));
		int S = isBlockAt(chunk, this.direction, new Vector3i(0, 1, 1));
		int SW = isBlockAt(chunk, this.direction, new Vector3i(-1, 1, 1));

		float ao1 = solveAO(W, N, NW);
		float ao2 = solveAO(W, S, SW);
		float ao3 = solveAO(E, S, SE);
		float ao4 = solveAO(E, N, NE);

		switch (direction) {
			case UP:
				this.ao1 = ao1;
				this.ao2 = ao2;
				this.ao3 = ao3;
				this.ao4 = ao4;
				break;
			case DOWN:
				this.ao1 = ao4;
				this.ao2 = ao3;
				this.ao3 = ao2;
				this.ao4 = ao1;
				break;
			case NORTH:
				this.ao1 = ao3;
				this.ao2 = ao4;
				this.ao3 = ao1;
				this.ao4 = ao2;
				break;
			case SOUTH:
				this.ao1 = ao4;
				this.ao2 = ao3;
				this.ao3 = ao2;
				this.ao4 = ao1;
				break;
			case EAST:
				this.ao1 = ao2;
				this.ao2 = ao3;
				this.ao3 = ao4;
				this.ao4 = ao1;
				break;
			case WEST:
				this.ao1 = ao3;
				this.ao2 = ao2;
				this.ao3 = ao1;
				this.ao4 = ao4;
				break;
		}
	}
}