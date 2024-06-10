package com.ezzenix.math;

import com.ezzenix.game.world.Chunk;
import org.joml.Vector3f;

import java.util.Objects;

public class ChunkPos {
	public int x;
	public int z;

	public ChunkPos(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public static ChunkPos from(BlockPos blockPos) {
		int chunkX = blockPos.x >> 4; // Divide by chunk size (16)
		int chunkZ = blockPos.z >> 4; // Divide by chunk size (16)
		return new ChunkPos(chunkX, chunkZ);
	}

	public static ChunkPos from(Vector3f position) {
		return ChunkPos.from(BlockPos.from(position));
	}

	public LocalPosition toLocalPosition(BlockPos blockPos) {
		return new LocalPosition(blockPos.x - this.x * Chunk.CHUNK_WIDTH, blockPos.y, blockPos.z - this.z * Chunk.CHUNK_WIDTH);
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
