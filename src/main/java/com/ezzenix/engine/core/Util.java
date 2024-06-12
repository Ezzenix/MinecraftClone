package com.ezzenix.engine.core;

import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Util {
	public static Vector3f getLookVector(float yaw, float pitch) {
		Vector3f lookVector = new Vector3f(0.0f, 0.0f, -1.0f);
		Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);
		Quaternionf orientation = new Quaternionf()
			.rotateAxis(Math.toRadians(yaw + 180), upVector)
			.rotateAxis(Math.toRadians(pitch), new Vector3f(1.0f, 0.0f, 0.0f));
		lookVector.set(0.0f, 0.0f, -1.0f).rotate(orientation);
		upVector.set(0.0f, 1.0f, 0.0f);
		Vector3f rightVector = new Vector3f();
		lookVector.cross(upVector, rightVector).normalize();
		return lookVector;
	}
}
