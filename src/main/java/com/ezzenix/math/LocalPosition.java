package com.ezzenix.math;

import com.ezzenix.game.world.Chunk;
import org.joml.Vector3f;

import java.util.Objects;

/*
    A position relative to a chunk
*/
public class LocalPosition {
	public final int x;
	public final int y;
	public final int z;

	public LocalPosition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public LocalPosition add(LocalPosition v) {
		return new LocalPosition(x + v.x, y + v.y, z + v.z);
	}
	public LocalPosition add(int x, int y, int z) {
		return new LocalPosition(this.x + x, this.y + y, this.z + z);
	}

	public static LocalPosition fromIndex(int index) {
		int x = index & 0xF; // 4 bits for x
		int y = (index >> 4) & 0xFF; // 8 bits for y
		int z = (index >> 12) & 0xF; // 4 bits for z
		return new LocalPosition(x, y, z);
	}

	public int toIndex() {
		if (x < 0 || x >= 16 || y < 0 || y > 255 || z < 0 || z >= 16)
			return -1; // validate
		return (x & 0xF) | ((y & 0xFF) << 4) | ((z & 0xF) << 12);
	}

	public static LocalPosition from(Vector3f vec) {
		return new LocalPosition(
				(int) Math.floor(vec.x),
				(int) Math.floor(vec.y),
				(int) Math.floor(vec.z)
		);
	}

	public BlockPos toWorldPosition(Chunk chunk) {
		return new BlockPos(chunk.getPos().x * Chunk.CHUNK_WIDTH + x, y, chunk.getPos().z * Chunk.CHUNK_WIDTH + z);
	}

	public String toString() {
		return "LocalPosition(" + x + " " + y + " " + z + ")";
	}

	public boolean equals(Object v) {
		if (this == v) return true;
		if (v == null || this.getClass() != v.getClass()) return false;
		LocalPosition other = (LocalPosition) v;
		return this.x == other.x && this.y == other.y && this.z == other.z;
	}

	public int hashCode() {
		return Objects.hash(x, y, z);
	}
}