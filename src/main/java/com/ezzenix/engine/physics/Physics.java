package com.ezzenix.engine.physics;

import com.ezzenix.Game;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.game.world.World;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.BoundingBox;
import org.joml.Vector3f;

public class Physics {
	private static float gameSpeed = 1f;

	public static BoundingBox getBlockBoundingBox(BlockPos blockPos) {
		return new BoundingBox(blockPos.x, blockPos.y, blockPos.z, blockPos.x + 1, blockPos.y + 1, blockPos.z + 1);
	}

	private static void stepEntity(Entity entity, float deltaTime) {
		if (!entity.isGrounded) {
			entity.applyImpulse(0, -9.82f * deltaTime, 0);
		}

		World world = entity.getWorld();

		Vector3f[] axisVectors = new Vector3f[]{new Vector3f(0, 1, 0), new Vector3f(1, 0, 0), new Vector3f(0, 0, 1)};

		Vector3f entityPosition = new Vector3f(entity.getPosition());

		boolean isGrounded = false;

		//entity.boundingBox.render();

		for (Vector3f axisVector : axisVectors) {
			Vector3f vel = new Vector3f(entity.getVelocity()).mul(deltaTime).mul(axisVector);
			Vector3f newPosition = new Vector3f(entityPosition).add(vel);
			entity.setPosition(newPosition);

			BlockPos newBlockPos = BlockPos.from(newPosition);

			Vector3f intersection = new Vector3f();
			float highestLength = 0;

			for (int x = newBlockPos.x - 1; x <= newBlockPos.x + 1; x++) {
				for (int y = newBlockPos.y - 1; y <= newBlockPos.y + 2; y++) {
					for (int z = newBlockPos.z - 1; z <= newBlockPos.z + 1; z++) {
						BlockPos blockPos = new BlockPos(x, y, z);
						BlockType blockType = world.getBlock(blockPos);
						if (blockType == null) continue;
						if (!blockType.isSolid()) continue;

						if (blockPos.equals(newBlockPos) && axisVector.y != 1) continue;
						if (blockPos.equals(newBlockPos.add(0, 1, 0)) && axisVector.y != 1) continue;

						Vector3f intersec = entity.boundingBox.getIntersection(getBlockBoundingBox(blockPos)).mul(axisVector);

						//getBlockBoundingBox(blockPos).render(intersec.length() > 0 ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0));

						if (intersec.length() > highestLength) {
							highestLength = intersec.length();
							intersection = intersec;
						}
					}
				}
			}

			if (axisVector.y == 1 && intersection.y > 0 && entity.getVelocity().y < 0) {
				isGrounded = true;
			}

			if (intersection.length() > 0) {
				if (entity.getVelocity().x > 0) intersection.x *= -1;
				if (entity.getVelocity().y > 0) intersection.y *= -1;
				if (entity.getVelocity().z > 0) intersection.z *= -1;

				if (intersection.y != 0) {
					entity.getVelocity().y = 0;
				}
				if (intersection.x != 0) {
					entity.getVelocity().x = 0;
				}
				if (intersection.z != 0) {
					entity.getVelocity().z = 0;
				}

				if (intersection.y < 0.5) {
					newPosition.add(intersection);
				}
			}

			entityPosition.set(newPosition);
		}

		entity.setPosition(entityPosition);
		entity.isGrounded = isGrounded;
	}

	public static void step() {
		float deltaTime = Scheduler.getDeltaTime() * gameSpeed;

		for (Entity entity : Game.getInstance().getEntities()) {
			stepEntity(entity, deltaTime);
		}
	}
}
