package com.ezzenix.item;

import com.ezzenix.blocks.BlockType;
import com.ezzenix.client.Client;
import com.ezzenix.entities.Entity;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.BoundingBox;
import com.ezzenix.physics.Physics;
import com.ezzenix.physics.Raycast;
import org.joml.Vector3i;

public class BlockItem extends Item {
	private final BlockType blockType;

	public BlockItem(BlockType blockType) {
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
					if (entity.boundingBox.getIntersection(blockBoundingBox).length() > 0) return;
				}

				Client.getWorld().setBlock(blockPos, this.blockType);
			}
		}
	}

	public void attack() {
	}

	public BlockType getBlockType() {
		return this.blockType;
	}
}
