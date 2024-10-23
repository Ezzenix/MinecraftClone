package com.ezzenix.util;

import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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

	public static <T, R> Function<T, R> memoize(final Function<T, R> function) {
		return new Function<T, R>() {
			private final Map<T, R> cache = new ConcurrentHashMap<>();

			public R apply(T object) {
				return this.cache.computeIfAbsent(object, function);
			}

			public String toString() {
				String var10000 = String.valueOf(function);
				return "memoize/1[function=" + var10000 + ", size=" + this.cache.size() + "]";
			}
		};
	}
}
