package com.ezzenix.game.physics;

import com.ezzenix.engine.core.enums.Face;
import com.ezzenix.engine.scheduler.Scheduler;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.game.world.World;
import com.ezzenix.hud.Debug;
import com.ezzenix.math.BlockPos;
import org.joml.Vector3f;
import org.joml.Vector3i;

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
		BlockPos nextBlockPos = BlockPos.from(nextPosition);


		boolean isColliding = false;


		for (int x = nextBlockPos.x - 1; x <= nextBlockPos.x + 1; x++) {
			for (int y = nextBlockPos.y - 1; y <= nextBlockPos.y + 2; y++) {
				for (int z = nextBlockPos.z - 1; z <= nextBlockPos.z + 1; z++) {
					BlockPos blockPos = new BlockPos(x, y, z);
					BlockType blockType = world.getBlock(blockPos);
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

	public static RaycastResult raycast(World world, Vector3f origin, Vector3f direction) {
		float maxDistance = direction.length();

		Vector3f rayOrigin = new Vector3f(origin);
		Vector3f rayDirection = new Vector3f(direction).normalize();

		Vector3i currentVoxel = new Vector3i((int) Math.floor(rayOrigin.x), (int) Math.floor(rayOrigin.y), (int) Math.floor(rayOrigin.z));
		Vector3f step = new Vector3f(Math.signum(rayDirection.x), Math.signum(rayDirection.y), Math.signum(rayDirection.z));

		Vector3f tMax = new Vector3f(
				intBound(rayOrigin.x, rayDirection.x),
				intBound(rayOrigin.y, rayDirection.y),
				intBound(rayOrigin.z, rayDirection.z)
		);
		Vector3f tDelta = new Vector3f(
				step.x / rayDirection.x,
				step.y / rayDirection.y,
				step.z / rayDirection.z
		);

		float distance = 0.0f;

		while (distance < maxDistance) {
			BlockPos voxelBlockPos = new BlockPos(currentVoxel.x, currentVoxel.y, currentVoxel.z);
			BlockType voxelBlockType = world.getBlock(voxelBlockPos);
			if (voxelBlockType != BlockType.AIR) {
				Face hitFace = Face.getClosestFromNormal(direction.mul(-1));
				return new RaycastResult(voxelBlockPos, voxelBlockType, hitFace);
			}

			if (tMax.x < tMax.y) {
				if (tMax.x < tMax.z) {
					currentVoxel.x += step.x;
					distance = tMax.x;
					tMax.x += tDelta.x;
				} else {
					currentVoxel.z += step.z;
					distance = tMax.z;
					tMax.z += tDelta.z;
				}
			} else {
				if (tMax.y < tMax.z) {
					currentVoxel.y += step.y;
					distance = tMax.y;
					tMax.y += tDelta.y;
				} else {
					currentVoxel.z += step.z;
					distance = tMax.z;
					tMax.z += tDelta.z;
				}
			}
		}

		return null;
	}

	private static float intBound(float s, float ds) {
		if (ds > 0) {
			return (float) (Math.ceil(s) - s) / ds;
		} else if (ds < 0) {
			return (float) (s - Math.floor(s)) / -ds;
		}
		return Float.POSITIVE_INFINITY;
	}

	private static Face getHitFace(Vector3f rayDirection, Vector3f hitPosition, BlockPos blockPos) {
		// Calculate the fractional parts of hitPosition
		float fx = hitPosition.x - blockPos.x;
		float fy = hitPosition.y - blockPos.y;
		float fz = hitPosition.z - blockPos.z;

		// Determine which face was hit based on the largest component of the direction vector
		if (Math.abs(rayDirection.x) > Math.abs(rayDirection.y) && Math.abs(rayDirection.x) > Math.abs(rayDirection.z)) {
			return (rayDirection.x > 0) ? Face.LEFT : Face.RIGHT;
		} else if (Math.abs(rayDirection.y) > Math.abs(rayDirection.z)) {
			return (rayDirection.y > 0) ? Face.BOTTOM : Face.TOP;
		} else {
			return (rayDirection.z > 0) ? Face.FRONT : Face.BACK;
		}
	}
}
