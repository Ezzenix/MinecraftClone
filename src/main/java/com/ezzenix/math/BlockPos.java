package com.ezzenix.math;

import com.ezzenix.game.world.Chunk;
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

	public BlockPos add(BlockPos v) {
		return new BlockPos(x + v.x, y + v.y, z + v.z);
	}
	public BlockPos add(int x, int y, int z) {
		return new BlockPos(this.x + x, this.y + y, this.z + z);
	}

	public Vector3f toVector3f(BlockPos v) {
		return new Vector3f(v.x + 0.5f, v.y + 0.5f, v.z + 0.5f);
	}

	public static BlockPos from(Vector3f vec) {
		return new BlockPos(
			(int) Math.floor(vec.x),
			(int) Math.floor(vec.y),
			(int) Math.floor(vec.z)
		);
	}

	public static BlockPos from(Chunk chunk, LocalPosition localPosition) {
		return new BlockPos(chunk.getPos().x * Chunk.CHUNK_WIDTH + localPosition.x, localPosition.y, chunk.getPos().z * Chunk.CHUNK_WIDTH + localPosition.z);
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