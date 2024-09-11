package com.ezzenix.physics;

import com.ezzenix.blocks.BlockType;
import com.ezzenix.enums.Direction;
import com.ezzenix.math.BlockPos;
import com.ezzenix.world.World;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class Raycast {
	public BlockPos blockPos;
	public BlockType blockType;
	public Direction hitDirection;

	private Raycast(BlockPos blockPos, BlockType blockType, Direction hitDirection) {
		this.blockPos = blockPos;
		this.blockType = blockType;
		this.hitDirection = hitDirection;
	}

	private static float intBound(float s, float ds) {
		if (ds > 0) {
			return (float) (Math.ceil(s) - s) / ds;
		} else if (ds < 0) {
			return (float) (s - Math.floor(s)) / -ds;
		}
		return Float.POSITIVE_INFINITY;
	}

	public static Raycast create(World world, Vector3f origin, Vector3f direction) {
		float maxDistance = direction.length();

		Vector3f rayOrigin = new Vector3f(origin);
		Vector3f rayDirection = new Vector3f(direction).normalize();

		// Adding a small epsilon to handle boundary conditions
		float epsilon = 1e-6f;

		Vector3i currentVoxel = new Vector3i((int) Math.floor(rayOrigin.x), (int) Math.floor(rayOrigin.y), (int) Math.floor(rayOrigin.z));
		Vector3i previousVoxel = new Vector3i(currentVoxel);
		Vector3f step = new Vector3f(Math.signum(rayDirection.x), Math.signum(rayDirection.y), Math.signum(rayDirection.z));

		Vector3f tMax = new Vector3f(
			intBound(rayOrigin.x + epsilon, rayDirection.x),
			intBound(rayOrigin.y + epsilon, rayDirection.y),
			intBound(rayOrigin.z + epsilon, rayDirection.z)
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
			if (voxelBlockType != BlockType.AIR && voxelBlockType != null && !voxelBlockType.isFluid()) {
				Vector3i normal = currentVoxel.add(previousVoxel.mul(-1)).mul(-1);
				Direction hitDirection = Direction.getFace(normal);
				return new Raycast(voxelBlockPos, voxelBlockType, hitDirection);
			}

			previousVoxel.x = currentVoxel.x;
			previousVoxel.y = currentVoxel.y;
			previousVoxel.z = currentVoxel.z;

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
}
