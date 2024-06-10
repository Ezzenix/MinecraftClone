package com.ezzenix.engine.core.enums;

import org.joml.Vector3f;
import org.joml.Vector3i;

public enum Face {
	TOP(new Vector3i(0, 1, 0)),
	BOTTOM(new Vector3i(0, -1, 0)),
	RIGHT(new Vector3i(1, 0, 0)),
	LEFT(new Vector3i(-1, 0, 0)),
	FRONT(new Vector3i(0, 0, -1)),
	BACK(new Vector3i(0, 0, 1));

	private final Vector3i normal;
	Face(Vector3i normal) {
		this.normal = normal;
	}

	public Vector3i getNormal() {
		return this.normal;
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