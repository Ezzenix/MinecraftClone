package com.ezzenix.game.chunk;

import com.ezzenix.engine.utils.BlockPos;
import org.joml.Vector3i;

public class ChunkUtil {
    public static Vector3i getLocalPosition(Chunk chunk, BlockPos blockPos) {
        return new Vector3i(blockPos.x - chunk.x * 16, blockPos.y - chunk.y * 16, blockPos.z - chunk.z * 16);
    }

    public static int genIndexFromLocalPosition(int x, int y, int z) {
        return x | y << 4 | z << 8;
    }
    public static int getIndexFromLocalPosition(Vector3i localPos) {
        return genIndexFromLocalPosition(localPos.x, localPos.y, localPos.z);
    }

    public static Vector3i getLocalPositionFromIndex(int index) {
        int mask = 0xF; // This is 15 in decimal, representing the lowest 4 bits
        int x = index & mask;
        int y = (index >> 4) & mask;
        int z = (index >> 8) & mask;
        return new Vector3i(x, y, z);
    }
}
