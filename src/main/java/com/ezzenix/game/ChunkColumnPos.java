package com.ezzenix.game;

import org.joml.Vector3f;

import java.util.Objects;

public class ChunkColumnPos {
	public int x;
	public int z;

	public ChunkColumnPos(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public static ChunkColumnPos from(BlockPos blockPos) {
		int chunkX = blockPos.x >> 5; // Divide by chunk size (32)
		int chunkZ = blockPos.z >> 5; // Divide by chunk size (32)
		return new ChunkColumnPos(chunkX, chunkZ);
	}

	public static ChunkColumnPos from(Vector3f position) {
		return ChunkColumnPos.from(BlockPos.from(position));
	}

	public String toString() {
		return "ChunkColumnPos(" + x + " " + z + ")";
	}

	public boolean equals(Object v) {
		if (this == v) return true;
		if (v == null || this.getClass() != v.getClass()) return false;
		ChunkColumnPos other = (ChunkColumnPos) v;
		return this.x == other.x && this.z == other.z;
	}

	public int hashCode() {
		return Objects.hash(x, z);
	}
}
