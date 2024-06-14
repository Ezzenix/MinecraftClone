package com.ezzenix.game.enums;

import org.joml.Vector3f;
import org.joml.Vector3i;

public enum Direction {
	UP("up", 0, 1, 0),
	DOWN("down", 0, -1, 0),
	EAST("east", 1, 0, 0),
	WEST("west", -1, 0, 0),
	NORTH("north", 0, 0, -1),
	SOUTH("south", 0, 0, 1);

	private final String name;
	private final Vector3i normal;

	Direction(String name, int x, int y, int z) {
		this.name = name;
		this.normal = new Vector3i(x, y, z);
	}

	public String getName() {
		return name;
	}

	public Vector3i getNormal() {
		return this.normal;
	}

	public static Direction getFace(Vector3i normal) {
		for (Direction direction : Direction.values()) {
			if (direction.normal.equals(normal)) return direction;
		}
		return null;
	}

	public static Direction getClosestFromNormal(Vector3f normal) {
		normal = normal.normalize();

		Direction closestDirection = null;
		float highestDot = -1;

		for (Direction direction : Direction.values()) {
			Vector3f faceNormal = new Vector3f(direction.getNormal());

			float dot = faceNormal.dot(normal);
			if (dot > highestDot) {
				highestDot = dot;
				closestDirection = direction;
			}
		}

		return closestDirection;
	}

	public static Direction fromYaw(float yaw) {
		if (yaw < -135 || yaw >= 135) {
			return Direction.NORTH;
		} else if (yaw > -135 && yaw < -45) {
			return Direction.WEST;
		} else if (yaw >= -45 && yaw <= 45) {
			return Direction.SOUTH;
		} else {
			return Direction.EAST;
		}
	}
}