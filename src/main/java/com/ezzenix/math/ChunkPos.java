package com.ezzenix.math;

import org.joml.Vector3f;

import java.util.Objects;

public class ChunkPos {
	public int x;
	public int z;

	public ChunkPos(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public ChunkPos(long pos) {
		this.x = (int) pos;
		this.z = (int) (pos >> 32);
	}

	public ChunkPos(BlockPos blockPos) {
		this(blockPos.x >> 4, blockPos.z >> 4);
	}

	public ChunkPos(Vector3f position) {
		this(new BlockPos(position));
	}

	public long toLong() {
		return toLong(this.x, this.z);
	}

	public static long toLong(int chunkX, int chunkZ) {
		return (long) chunkX & 4294967295L | ((long) chunkZ & 4294967295L) << 32;
	}

	public int distanceTo(ChunkPos other) {
		int dx = this.x - other.x;
		int dy = this.z - other.z;
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	public String toString() {
		return "ChunkPos(" + x + " " + z + ")";
	}

	public boolean equals(Object v) {
		if (this == v) return true;
		if (v == null || this.getClass() != v.getClass()) return false;
		ChunkPos other = (ChunkPos) v;
		return this.x == other.x && this.z == other.z;
	}

	public int hashCode() {
		return Objects.hash(x, z);
	}
}
