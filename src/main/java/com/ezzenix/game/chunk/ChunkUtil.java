package com.ezzenix.game.chunk;

import com.ezzenix.engine.utils.BlockPos;
import org.joml.Vector3i;

import static com.ezzenix.engine.utils.MathUtil.*;

public class ChunkUtil {
    public static Vector3i getLocalPosition(Chunk chunk, BlockPos blockPos) {
        return new Vector3i(blockPos.x - chunk.x * Chunk.CHUNK_SIZE, blockPos.y - chunk.y * Chunk.CHUNK_SIZE, blockPos.z - chunk.z * Chunk.CHUNK_SIZE);
    }

    public static int genIndexFromLocalPosition(int x, int y, int z) {
        int shift = log2(Chunk.CHUNK_SIZE);
        return x | y << shift | z << (2 * shift);
    }
    public static int getIndexFromLocalPosition(Vector3i localPos) {
        return genIndexFromLocalPosition(localPos.x, localPos.y, localPos.z);
    }

    public static Vector3i getLocalPositionFromIndex(int index) {
        int shift = log2(Chunk.CHUNK_SIZE);
        int mask = Chunk.CHUNK_SIZE - 1; // This is (chunkSize - 1) in decimal, representing the lowest bits
        int x = index & mask;
        int y = (index >> shift) & mask;
        int z = (index >> (2 * shift)) & mask;
        return new Vector3i(x, y, z);
    }
}
