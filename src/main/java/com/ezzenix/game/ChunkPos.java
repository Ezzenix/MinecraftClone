package com.ezzenix.game;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Objects;

public class ChunkPos {
	public int x;
	public int y;
	public int z;

	public ChunkPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static ChunkPos from(BlockPos blockPos) {
		int chunkX = blockPos.x >> 5; // Divide by chunk size (32)
		int chunkY = blockPos.y >> 5; // Divide by chunk size (32)
		int chunkZ = blockPos.z >> 5; // Divide by chunk size (32)
		return new ChunkPos(chunkX, chunkY, chunkZ);
	}

	public static ChunkPos from(Vector3f position) {
		return ChunkPos.from(BlockPos.from(position));
	}

	public String toString() {
		return "ChunkPos(" + x + " " + y + " " + z + ")";
	}

	public boolean equals(Object v) {
		if (this == v) return true;
		if (v == null || this.getClass() != v.getClass()) return false;
		ChunkPos other = (ChunkPos) v;
		return this.x == other.x && this.y == other.y && this.z == other.z;
	}

	public int hashCode() {
		return Objects.hash(x, y, z);
	}
}
