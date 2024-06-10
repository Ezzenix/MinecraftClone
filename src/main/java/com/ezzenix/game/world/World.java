package com.ezzenix.game.world;

import com.ezzenix.Game;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.ChunkPos;
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
        int WORLD_SIZE = 1;
        for (int x = 0; x < WORLD_SIZE; x++) {
			for (int z = 0; z < WORLD_SIZE; z++) {
				createChunk(x, z);
			}
        }
    }

    private Chunk createChunk(ChunkPos chunkPos, boolean doNotGenerate) {
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
    private Chunk createChunk(int x, int z) {
        return createChunk(new ChunkPos(x, z));
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
    public Chunk getChunk(int x, int z) {
        return getChunk(new ChunkPos(x, z));
    }
    public Chunk getChunk(BlockPos blockPos) {
        if (blockPos.y < 0) return null;
        if (blockPos.y >= Chunk.CHUNK_HEIGHT) return null;
        return getChunk(ChunkPos.from(blockPos));
    }

    public ConcurrentHashMap<ChunkPos, Chunk> getChunkMap() {
        return this.chunks;
    }

    public void loadNewChunks() {
        ChunkPos chunkPos = ChunkPos.from(Game.getInstance().getPlayer().getBlockPos());

        int renderDistance = 6;

        List<ChunkPos> chunksToShow = new ArrayList<>();

        for (int x = chunkPos.x - renderDistance; x < chunkPos.x + renderDistance; x++) {
            for (int z = chunkPos.z - renderDistance; z < chunkPos.z + renderDistance; z++) {
                ChunkPos pos = new ChunkPos(x, z);
                createChunk(pos);
                chunksToShow.add(pos);
            }
        }

        for (Chunk chunk : chunks.values()) {
            if (!chunksToShow.contains(chunk.getPos())) {
                chunk.dispose();
            }
        }
    }
}
