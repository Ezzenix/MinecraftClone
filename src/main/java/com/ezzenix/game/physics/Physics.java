package com.ezzenix.game.physics;

import com.ezzenix.engine.scheduler.Scheduler;
import com.ezzenix.game.core.BlockPos;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.game.world.World;
import com.ezzenix.hud.Debug;
import org.joml.Vector3f;

import java.text.DecimalFormat;

public class Physics {
	private static float gameSpeed = 1f;

	private static void stepEntity(Entity entity) {
		float deltaTime = Scheduler.getDeltaTime();

		// gravity
		if (!entity.isGrounded) {
			entity.getVelocity().add(0, -9.82f * deltaTime, 0);
		}

		// get the position player will be next frame based on velocity
		Vector3f nextPosition = new Vector3f(
				entity.getPosition()
		).add(
				new Vector3f(entity.getVelocity()).mul(deltaTime).mul(gameSpeed)
		);

		World world = entity.getWorld();
		BlockPos nextBlockPos = BlockPos.fromVector3f(nextPosition);


		boolean isColliding = false;


		for (int x = nextBlockPos.x - 1; x <= nextBlockPos.x + 1; x++) {
			for (int y = nextBlockPos.y - 1; y <= nextBlockPos.y + 2; y++) {
				for (int z = nextBlockPos.z - 1; z <= nextBlockPos.z + 1; z++) {
					BlockPos blockPos = new BlockPos(x, y, z);
					BlockType blockType = world.getBlockTypeAt(blockPos);
					if (!blockType.isSolid()) continue;
					if (blockPos == nextBlockPos) continue;

					Vector3f intersection = entity.aabb.getCollision(entity.getPosition(), blockPos, blockType);


					if (intersection.y > 0) {
						entity.getVelocity().set(0, 0, 0);
						entity.isGrounded = true;
					} else {
						entity.isGrounded = false;
					}


					if (intersection.length() > 0) {
						System.out.println(intersection.toString(new DecimalFormat("#.##")));
						Debug.highlightVoxel(new Vector3f(blockPos.x, blockPos.y, blockPos.z), new Vector3f(1, 0, 0));
						//isColliding = true;
						//nextPosition.add(0, intersection.y, 0);
					}
				}
			}
		}

		// apply velocity to position
		if (!isColliding) {
			entity.getPosition().set(nextPosition);
		}
	}

	public static void step() {
		//for (Entity entity : Game.getInstance().getEntities()) {
		//	stepEntity(entity);
		//}
	}
}
