package com.ezzenix.game;

import com.ezzenix.Game;
import com.ezzenix.rendering.Camera;
import com.ezzenix.utils.BlockPos;
import org.joml.Vector2i;

import java.util.HashMap;

public class World {
    private final HashMap<Vector2i, Chunk> chunks = new HashMap<>();

    public World() {
        for (int x = 0; x < 2; x++) {
            for (int z = 0; z < 2; z++) {
                Chunk chunk = new Chunk(x, z, this);
                chunks.put(new Vector2i(x, z), chunk);
                chunk.generate();
            }
        }
    }

    public Chunk getChunkAtBlockPos(BlockPos blockPos) {
        int chunkX = blockPos.x >> 4; // Divide by chunk size (16)
        int chunkZ = blockPos.z >> 4;
        return chunks.get(new Vector2i(chunkX, chunkZ));
    }

    public BlockType getBlockTypeAt(BlockPos blockPos) {
        Chunk chunk = getChunkAtBlockPos(blockPos);
        if (chunk == null) return null;
        return chunk.getBlockTypeAt(blockPos);
    }

    public HashMap<Vector2i, Chunk> getChunks() {
        return this.chunks;
    }

    private void generateNewChunks() {
        Camera camera = Game.getInstance().getRenderer().getCamera();
        //for (int)
    }
}
