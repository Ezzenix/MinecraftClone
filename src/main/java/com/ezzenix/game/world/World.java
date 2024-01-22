package com.ezzenix.game.world;

import com.ezzenix.game.BlockPos;
import com.ezzenix.game.ChunkPos;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.threads.WorldGeneratorThread;

import java.util.HashMap;

public class World {
    private final HashMap<ChunkPos, Chunk> chunks = new HashMap<>();

    public World() {
        loadInitialChunks();
    }

    private void loadInitialChunks() {
        int WORLD_SIZE = 4;
        int WORLD_HEIGHT = 3;
        for (int x = 0; x < WORLD_SIZE; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                for (int z = 0; z < WORLD_SIZE; z++) {
                    loadChunk(x, y, z);
                }
            }
        }
    }

    private Chunk loadChunk(ChunkPos chunkPos) {
        if (chunkPos.y < 0) return null;
        if (chunks.get(chunkPos) != null) return null;
        Chunk chunk = new Chunk(chunkPos, this);
        chunks.put(chunkPos, chunk);
        WorldGeneratorThread.scheduleChunkForWorldGeneration(chunk);
        return chunk;
    }
    private Chunk loadChunk(int x, int y, int z) {
        return loadChunk(new ChunkPos(x, y, z));
    }

    public synchronized void setBlock(BlockPos blockPos, BlockType blockType) {
        ChunkPos chunkPos = ChunkPos.from(blockPos);
        Chunk chunk = getChunk(chunkPos);
        if (chunk == null) {
            chunk = loadChunk(chunkPos);
            if (chunk == null) return;
        };
        chunk.setBlock(blockPos, blockType);
        chunk.flagMeshForUpdate(true);
    }

    public synchronized BlockType getBlock(BlockPos blockPos) {
        Chunk chunk = getChunk(blockPos);
        if (chunk == null) return BlockType.AIR;
        return chunk.getBlock(blockPos);
    }

    public Chunk getChunk(ChunkPos chunkPos) {
        return chunks.get(chunkPos);
    }
    public Chunk getChunk(int x, int y, int z) {
        return getChunk(new ChunkPos(x, y, z));
    }
    public Chunk getChunk(BlockPos blockPos) {
        return getChunk(ChunkPos.from(blockPos));
    }

    public HashMap<ChunkPos, Chunk> getChunkMap() {
        return this.chunks;
    }

    /*
    public void loadNewChunks() {
        Vector3f position = Game.getInstance().getPlayer().getPosition();
        int chunkX = ((int) position.x >> 5);
        int chunkY = ((int) position.y >> 5);
        int chunkZ = ((int) position.z >> 5);

        int renderDistance = 6;

        for (int x = chunkX - renderDistance; x < chunkX + renderDistance; x++) {
            for (int y = chunkY - renderDistance; y < chunkY + renderDistance; y++) {
                for (int z = chunkZ - renderDistance; z < chunkZ + renderDistance; z++) {
                    //loadChunk(x, y, z);
                }
            }
        }
    }
     */
}
