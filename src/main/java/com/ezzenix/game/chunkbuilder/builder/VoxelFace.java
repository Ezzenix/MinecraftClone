package com.ezzenix.game.chunkbuilder.builder;

import com.ezzenix.engine.core.enums.Face;
import com.ezzenix.game.blocks.BlockRegistry;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.LocalPosition;
import org.joml.Vector3i;

public class VoxelFace {
	public Face face;
	public byte blockId;

	public LocalPosition localPosition;

	public float ao1 = 0;
	public float ao2 = 0;
	public float ao3 = 0;
	public float ao4 = 0;

	public VoxelFace(LocalPosition localPosition, Face face, byte blockId) {
		this.localPosition = localPosition;
		this.face = face;
		this.blockId = blockId;
	}

	private int isBlockAt(Chunk chunk, Face face, Vector3i offset) {
		applyOffsetRotation(face, offset);

		BlockPos worldPos = BlockPos.from(chunk, localPosition.add(offset.x, offset.y, offset.z));
		BlockType blockType = chunk.getWorld().getBlock(worldPos);

		if (blockType == null) return 0;
		return blockType == BlockType.AIR || !blockType.isSolid() ? 0 : 1;
	}

	private void applyOffsetRotation(Face face, Vector3i offset) {
		switch (face) {
			case BOTTOM -> offset.set(-offset.x, -offset.y, -offset.z);
			case FRONT -> offset.set(offset.x, offset.z, -offset.y);
			case BACK -> offset.set(-offset.x, -offset.z, offset.y);
			case RIGHT -> offset.set(offset.y, -offset.x, offset.z);
			case LEFT -> offset.set(-offset.y, offset.x, -offset.z);
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
		BlockType type = BlockRegistry.getBlockFromId(blockId);
		if (type.isTransparent()) return;

		int W = isBlockAt(chunk, this.face, new Vector3i(-1, 1, 0));
		int NW = isBlockAt(chunk, this.face, new Vector3i(-1, 1, -1));
		int N = isBlockAt(chunk, this.face, new Vector3i(0, 1, -1));
		int NE = isBlockAt(chunk, this.face, new Vector3i(1, 1, -1));
		int E = isBlockAt(chunk, this.face, new Vector3i(1, 1, 0));
		int SE = isBlockAt(chunk, this.face, new Vector3i(1, 1, 1));
		int S = isBlockAt(chunk, this.face, new Vector3i(0, 1, 1));
		int SW = isBlockAt(chunk, this.face, new Vector3i(-1, 1, 1));

		float ao1 = solveAO(W, N, NW);
		float ao2 = solveAO(W, S, SW);
		float ao3 = solveAO(E, S, SE);
		float ao4 = solveAO(E, N, NE);

		switch (face) {
			case TOP:
				this.ao1 = ao1;
				this.ao2 = ao2;
				this.ao3 = ao3;
				this.ao4 = ao4;
				break;
			case BOTTOM:
				this.ao1 = ao4;
				this.ao2 = ao3;
				this.ao3 = ao2;
				this.ao4 = ao1;
				break;
			case FRONT:
				this.ao1 = ao3;
				this.ao2 = ao4;
				this.ao3 = ao1;
				this.ao4 = ao2;
				break;
			case BACK:
				this.ao1 = ao4;
				this.ao2 = ao3;
				this.ao3 = ao2;
				this.ao4 = ao1;
				break;
			case RIGHT:
				this.ao1 = ao2;
				this.ao2 = ao3;
				this.ao3 = ao4;
				this.ao4 = ao1;
				break;
			case LEFT:
				this.ao1 = ao3;
				this.ao2 = ao2;
				this.ao3 = ao1;
				this.ao4 = ao4;
				break;
		}
	}
}