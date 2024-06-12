package com.ezzenix.game.enums;

import org.joml.Vector3f;
import org.joml.Vector3i;

public enum Face {
	TOP(new Vector3i(0, 1, 0)),
	BOTTOM(new Vector3i(0, -1, 0)),
	EAST(new Vector3i(1, 0, 0)),
	WEST(new Vector3i(-1, 0, 0)),
	NORTH(new Vector3i(0, 0, -1)),
	SOUTH(new Vector3i(0, 0, 1));

	private final Vector3i normal;
	Face(Vector3i normal) {
		this.normal = normal;
	}

	public Vector3i getNormal() {
		return this.normal;
	}

	public static Face getFace(Vector3i normal) {
		for (Face face : Face.values()) {
			if (face.normal.equals(normal)) return face;
		}
		return null;
	}

	public static Face getClosestFromNormal(Vector3f normal) {
		normal = normal.normalize();

		Face closestFace = null;
		float highestDot = -1;

		for (Face face : Face.values()) {
			Vector3f faceNormal = new Vector3f(face.getNormal());

			float dot = faceNormal.dot(normal);
			if (dot > highestDot) {
				highestDot = dot;
				closestFace = face;
			}
		}

		return closestFace;
	}
}