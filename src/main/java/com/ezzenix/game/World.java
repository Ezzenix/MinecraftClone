package com.ezzenix.game;

import com.ezzenix.Game;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.rendering.Camera;
import com.ezzenix.utils.BlockPos;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.util.HashMap;

public class World {
    private final HashMap<Vector3i, Chunk> chunks = new HashMap<>();

    public World() {
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                for (int z = 0; z < 10; z++) {
                    loadChunk(x, y, z);
                }
            }
        }
    }

    private void loadChunk(int x, int y, int z) {
        Chunk chunk = new Chunk(x, y, z, this);
        chunks.put(new Vector3i(x, y, z), chunk);
        chunk.generate();
    }

    public Chunk getChunkAtBlockPos(BlockPos blockPos) {
        int chunkX = blockPos.x >> 4; // Divide by chunk size (16)
        int chunkY = blockPos.y >> 4; // Divide by chunk size (16)
        int chunkZ = blockPos.z >> 4; // Divide by chunk size (16)
        return chunks.get(new Vector3i(chunkX, chunkY, chunkZ));
    }

    public BlockType getBlockTypeAt(BlockPos blockPos) {
        Chunk chunk = getChunkAtBlockPos(blockPos);
        if (chunk == null) return null;
        return chunk.getBlockTypeAt(blockPos);
    }

    public Chunk getChunk(int x, int y, int z) {
        return chunks.get(new Vector3i(x, y, z));
    }

    public HashMap<Vector3i, Chunk> getChunks() {
        return this.chunks;
    }
}
