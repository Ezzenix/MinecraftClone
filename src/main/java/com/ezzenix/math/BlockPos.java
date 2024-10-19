package com.ezzenix.math;

import com.ezzenix.world.chunk.Chunk;
import org.joml.Vector3f;

import java.util.Objects;

public class BlockPos {
	public final int x;
	public final int y;
	public final int z;

	public BlockPos(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockPos(Vector3f vec) {
		this((int) Math.floor(vec.x), (int) Math.floor(vec.y), (int) Math.floor(vec.z));
	}

	public BlockPos add(BlockPos v) {
		return new BlockPos(x + v.x, y + v.y, z + v.z);
	}
	public BlockPos add(int x, int y, int z) {
		return new BlockPos(this.x + x, this.y + y, this.z + z);
	}

	public Vector3f toVector3f() {
		return new Vector3f(x + 0.5f, y + 0.5f, z + 0.5f);
	}

	public int manhattanDistance(BlockPos other) {
		return (Math.abs(this.x - other.x) + Math.abs(this.y - other.y) + Math.abs(this.z - other.z));
	}

	public boolean isValid() {
		return this.y >= 0 && this.y <= Chunk.CHUNK_HEIGHT;
	}

	public String toString() {
		return "BlockPos(" + x + " " + y + " " + z + ")";
	}

	public boolean equals(Object v) {
		if (this == v) return true;
		if (v == null || this.getClass() != v.getClass()) return false;
		BlockPos other = (BlockPos) v;
		return this.x == other.x && this.y == other.y && this.z == other.z;
	}

	public int hashCode() {
		return Objects.hash(x, y, z);
	}
}