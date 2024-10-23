package com.ezzenix.item;

import com.ezzenix.Client;
import com.ezzenix.blocks.Block;
import com.ezzenix.entities.Entity;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.BoundingBox;
import com.ezzenix.physics.Physics;
import com.ezzenix.physics.Raycast;
import org.joml.Vector3i;

public class BlockItem extends Item {
	private final Block blockType;

	public BlockItem(Block blockType) {
		super(blockType.getName());
		this.blockType = blockType;
	}

	public void use() {
		Raycast result = Client.getPlayer().raycast();
		if (result != null && result.hitDirection != null) {
			Vector3i faceNormal = result.hitDirection.getNormal();
			BlockPos blockPos = result.blockPos.add(faceNormal.x, faceNormal.y, faceNormal.z);
			if (blockPos.isValid()) {

				BoundingBox blockBoundingBox = Physics.getBlockBoundingBox(blockPos);
				for (Entity entity : Client.getWorld().getEntities()) {
					if (entity.getDimensions().getBoxAt(entity.getPos()).getIntersection(blockBoundingBox).length() > 0)
						return;
				}

				Client.getWorld().setBlockState(blockPos, this.blockType.getDefaultState());
			}
		}
	}

	public void attack() {
	}

	public Block getBlock() {
		return this.blockType;
	}
}
