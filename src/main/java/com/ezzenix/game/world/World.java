package com.ezzenix.game.world;

import com.ezzenix.Game;
import com.ezzenix.game.BlockPos;
import com.ezzenix.game.ChunkPos;
import com.ezzenix.game.blocks.BlockType;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class World {
    private final ConcurrentHashMap<ChunkPos, Chunk> chunks = new ConcurrentHashMap<>();

    public World() {
        //loadInitialChunks();
    }

    private void loadInitialChunks() {
        int WORLD_SIZE = 12;
        int WORLD_HEIGHT = 3;
        for (int x = 0; x < WORLD_SIZE; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                for (int z = 0; z < WORLD_SIZE; z++) {
                    createChunk(x, y, z);
                }
            }
        }
    }

    private Chunk createChunk(ChunkPos chunkPos, boolean doNotGenerate) {
        if (chunkPos.y < 0) return null;
        Chunk chunk = chunks.get(chunkPos);
        if (chunk != null) { // already exists
            if (!chunk.hasGenerated && !doNotGenerate && !chunk.isGenerating) {
                chunk.generate();
            }
            return null;
        };
        chunk = new Chunk(chunkPos, this);
        chunks.put(chunkPos, chunk);
        if (!doNotGenerate) {
            chunk.generate();
        }
        return chunk;
    }
    private Chunk createChunk(ChunkPos chunkPos) {
        return createChunk(chunkPos, false);
    }
    private Chunk createChunk(int x, int y, int z) {
        return createChunk(new ChunkPos(x, y, z));
    }

    public void setBlock(BlockPos blockPos, BlockType blockType) {
        ChunkPos chunkPos = ChunkPos.from(blockPos);
        Chunk chunk = getChunk(chunkPos);
        if (chunk == null) {
            chunk = createChunk(chunkPos, true);
            if (chunk == null) return;
        };
        chunk.setBlock(blockPos, blockType);
        //chunk.flagMeshForUpdate(true);
    }

    public BlockType getBlock(BlockPos blockPos) {
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

    public ConcurrentHashMap<ChunkPos, Chunk> getChunkMap() {
        return this.chunks;
    }

    public void loadNewChunks() {
        Vector3f position = Game.getInstance().getPlayer().getPosition();
        int chunkX = ((int) position.x >> 5);
        int chunkY = ((int) position.y >> 5);
        int chunkZ = ((int) position.z >> 5);

        int renderDistance = 6;

        List<ChunkPos> chunksToShow = new ArrayList<>();

        for (int x = chunkX - renderDistance; x < chunkX + renderDistance; x++) {
            for (int y = chunkY - renderDistance; y < chunkY + renderDistance; y++) {
                for (int z = chunkZ - renderDistance; z < chunkZ + renderDistance; z++) {
                    ChunkPos chunkPos = new ChunkPos(x, y, z);
                    createChunk(chunkPos);
                    chunksToShow.add(chunkPos);
                }
            }
        }

        for (Chunk chunk : chunks.values()) {
            if (!chunksToShow.contains(chunk.getPos())) {
                chunk.dispose();
            }
        }
    }
}
