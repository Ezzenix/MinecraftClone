package com.ezzenix.game.world.chunk;

import com.ezzenix.game.core.BlockPos;
import org.joml.Vector3i;

public class ChunkUtil {
    public static Vector3i getLocalPosition(Chunk chunk, BlockPos blockPos) {
        return new Vector3i(blockPos.x - chunk.x * Chunk.CHUNK_SIZE, blockPos.y - chunk.y * Chunk.CHUNK_SIZE, blockPos.z - chunk.z * Chunk.CHUNK_SIZE);
    }

    public static int genIndexFromLocalPosition(int x, int y, int z) {
        int shift = 5;
        return x | y << shift | z << (2 * shift);
    }
    public static int getIndexFromLocalPosition(Vector3i localPos) {
        return genIndexFromLocalPosition(localPos.x, localPos.y, localPos.z);
    }

    public static Vector3i getLocalPositionFromIndex(int index) {
        int shift = 5;
        int mask = Chunk.CHUNK_SIZE - 1; // This is (chunkSize - 1) in decimal, representing the lowest bits
        int x = index & mask;
        int y = (index >> shift) & mask;
        int z = (index >> (2 * shift)) & mask;
        return new Vector3i(x, y, z);
    }
}
