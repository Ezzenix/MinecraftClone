package com.ezzenix.physics;

import com.ezzenix.blocks.Block;
import com.ezzenix.blocks.Blocks;
import com.ezzenix.Client;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.entities.Entity;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.BoundingBox;
import com.ezzenix.world.World;
import org.joml.Vector3f;

public class Physics {
	private static float gameSpeed = 1f;
	private static float GRAVITY = -27f;
	private static float FRICTION_COEFFICIENT_GROUND = 0.1f;
	private static float FRICTION_COEFFICIENT_AIR = 0.18f;

	public static BoundingBox getBlockBoundingBox(BlockPos blockPos) {
		return new BoundingBox(blockPos.x, blockPos.y, blockPos.z, blockPos.x + 1, blockPos.y + 1, blockPos.z + 1);
	}

	private static void stepEntity(Entity entity, float deltaTime) {
		World world = entity.getWorld();

		Block blockAtEntity = world.getBlockState(new BlockPos(entity.getPosition())).getBlock();

		entity.isInFluid = blockAtEntity != null && blockAtEntity.isFluid();

		// apply gravity
		if (!entity.isFlying) {
			if (!entity.isGrounded) {
				if (entity.isInFluid) {
					entity.applyImpulse(0, GRAVITY * 0.2f * deltaTime, 0);
					if (entity.getVelocity().y < -2f) {
						entity.getVelocity().y = -2f;
					}
					if (entity.getVelocity().y > 3f) {
						entity.getVelocity().y = 3f;
					}
				} else {
					entity.applyImpulse(0, GRAVITY * deltaTime, 0);
				}
			}
		}

		// apply friction
		float frictionCoefficient = entity.isGrounded ? FRICTION_COEFFICIENT_GROUND : FRICTION_COEFFICIENT_AIR;
		Vector3f velocity = entity.getVelocity();
		if (velocity.length() < 2) frictionCoefficient *= 0.02f;
		velocity.x *= (float) Math.pow(frictionCoefficient, deltaTime);
		velocity.z *= (float) Math.pow(frictionCoefficient, deltaTime);
		if (entity.isFlying) {
			velocity.y *= (float) Math.pow(0.03, deltaTime);
		}

		Vector3f[] axisVectors = new Vector3f[]{new Vector3f(0, 1, 0), new Vector3f(1, 0, 0), new Vector3f(0, 0, 1)};
		Vector3f entityPosition = new Vector3f(entity.getPosition());
		boolean isGrounded = false;

		if (Client.getOptions().thirdPerson) {
			entity.boundingBox.render(new Vector3f(1, 1, 1));
		}

		for (Vector3f axisVector : axisVectors) {
			Vector3f vel = new Vector3f(entity.getVelocity()).mul(deltaTime).mul(axisVector);
			Vector3f newPosition = new Vector3f(entityPosition).add(vel);
			entity.setPosition(newPosition);

			BlockPos newBlockPos = new BlockPos(newPosition);

			Vector3f intersection = new Vector3f();
			float highestLength = 0;

			if (!entity.noclip) {
				for (int x = newBlockPos.x - 1; x <= newBlockPos.x + 1; x++) {
					for (int y = newBlockPos.y - 1; y <= newBlockPos.y + 2; y++) {
						for (int z = newBlockPos.z - 1; z <= newBlockPos.z + 1; z++) {
							BlockPos blockPos = new BlockPos(x, y, z);
							Block blockType = world.getBlockState(blockPos).getBlock();
							if (blockType == null || blockType == Blocks.AIR) continue;

							if (blockPos.equals(newBlockPos) && axisVector.y != 1) continue;
							if (blockPos.equals(newBlockPos.add(0, 1, 0)) && axisVector.y != 1) continue;

							if (!blockType.isWalkthrough()) {
								Vector3f intersec = entity.boundingBox.getIntersection(getBlockBoundingBox(blockPos)).mul(axisVector);

								//getBlockBoundingBox(blockPos).render(intersec.length() > 0 ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0));

								if (intersec.length() > highestLength) {
									highestLength = intersec.length();
									intersection = intersec;
								}
							}
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
					if (entity.getVelocity().y < 0) { // when colliding with floor, set velocity to gravity
						entity.getVelocity().y = GRAVITY * deltaTime;
					} else {
						entity.getVelocity().y = 0;
					}
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

		if (entityPosition.y < -50) {
			entityPosition.y = 100;
			entity.getVelocity().y = 0;
		}

		entity.setPosition(entityPosition);
		entity.isGrounded = isGrounded;

		if (entity.isGrounded && entity.isFlying) {
			entity.isFlying = false;
		}
	}

	public static void step() {
		if (Client.isPaused()) return;

		float deltaTime = Scheduler.getDeltaTime() * gameSpeed;

		for (Entity entity : Client.getWorld().getEntities()) {
			stepEntity(entity, deltaTime);
		}
	}
}
