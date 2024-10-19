package com.ezzenix.rendering.chunkbuilder;

import com.ezzenix.enums.Direction;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Map;

public class UnitCube {
	public static Vector3f[] UP = new Vector3f[]{
		new Vector3f(0f, 1f, 0f),
		new Vector3f(0f, 1f, 1f),
		new Vector3f(1f, 1f, 1f),
		new Vector3f(1f, 1f, 0f),
	};

	public static Vector3f[] SOUTH = new Vector3f[]{
		new Vector3f(0f, 1f, 1f),
		new Vector3f(0f, 0f, 1f),
		new Vector3f(1f, 0f, 1f),
		new Vector3f(1f, 1f, 1f),
	};

	public static Vector3f[] WEST = new Vector3f[]{
		new Vector3f(0f, 1f, 0f),
		new Vector3f(0f, 0f, 0f),
		new Vector3f(0f, 0f, 1f),
		new Vector3f(0f, 1f, 1f),
	};

	public static Vector3f[] EAST = new Vector3f[]{
		new Vector3f(1f, 1f, 1f),
		new Vector3f(1f, 0f, 1f),
		new Vector3f(1f, 0f, 0f),
		new Vector3f(1f, 1f, 0f),
	};

	public static Vector3f[] NORTH = new Vector3f[]{
		new Vector3f(1f, 1f, 0f),
		new Vector3f(1f, 0f, 0f),
		new Vector3f(0f, 0f, 0f),
		new Vector3f(0f, 1f, 0f),
	};

	public static Vector3f[] DOWN = new Vector3f[]{
		new Vector3f(0f, 0f, 1f),
		new Vector3f(0f, 0f, 0f),
		new Vector3f(1f, 0f, 0f),
		new Vector3f(1f, 0f, 1f),
	};

	private static final Map<Direction, Vector3f[]> MAP = new EnumMap<>(Direction.class);

	static {
		MAP.put(Direction.UP, UP);
		MAP.put(Direction.SOUTH, SOUTH);
		MAP.put(Direction.WEST, WEST);
		MAP.put(Direction.EAST, EAST);
		MAP.put(Direction.NORTH, NORTH);
		MAP.put(Direction.DOWN, DOWN);
	}

	public static Vector3f[] getFace(Direction direction) {
		return MAP.get(direction);
	}
}