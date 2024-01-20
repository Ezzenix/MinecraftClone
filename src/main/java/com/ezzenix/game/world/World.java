package com.ezzenix.game.world;

import com.ezzenix.Game;
import com.ezzenix.engine.utils.BlockPos;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.chunk.Chunk;
import com.ezzenix.game.threads.WorldGeneratorThread;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.HashMap;

import static com.ezzenix.engine.utils.MathUtil.log2;

public class World {
    private final HashMap<Vector3i, Chunk> chunks = new HashMap<>();

    public World() {
        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 32; z++) {
                    loadChunk(x, y, z);
                }
            }
        }
    }

    private void loadChunk(int x, int y, int z) {
        if (y < 0) return;
        if (chunks.get(new Vector3i(x, y, z)) != null) return;
        Chunk chunk = new Chunk(x, y, z, this);
        chunks.put(new Vector3i(x, y, z), chunk);
        //chunk.generate();
        WorldGeneratorThread.scheduleChunkForWorldGeneration(chunk);
    }

    public void setBlock(BlockPos blockPos, BlockType blockType) {
        Chunk chunk = getChunkAtBlockPos(blockPos);
        if (chunk == null) return;
        chunk.setBlock(blockPos, blockType);
        chunk.updateMesh(true);
    }

    public Chunk getChunkAtBlockPos(BlockPos blockPos) {
        int chunkX = blockPos.x >> log2(Chunk.CHUNK_SIZE); // Divide by chunk size (16)
        int chunkY = blockPos.y >> log2(Chunk.CHUNK_SIZE); // Divide by chunk size (16)
        int chunkZ = blockPos.z >> log2(Chunk.CHUNK_SIZE); // Divide by chunk size (16)
        return chunks.get(new Vector3i(chunkX, chunkY, chunkZ));
    }

    public BlockType getBlockTypeAt(BlockPos blockPos) {
        Chunk chunk = getChunkAtBlockPos(blockPos);
        if (chunk == null) return BlockType.AIR;
        return chunk.getBlockTypeAt(blockPos);
    }

    public Chunk getChunk(int x, int y, int z) {
        return chunks.get(new Vector3i(x, y, z));
    }

    public HashMap<Vector3i, Chunk> getChunks() {
        return this.chunks;
    }

    public void loadNewChunks() {
        Vector3f position = Game.getInstance().getPlayer().getPosition();
        int chunkX = ((int) position.x >> log2(Chunk.CHUNK_SIZE));
        int chunkY = ((int) position.y >> log2(Chunk.CHUNK_SIZE));
        int chunkZ = ((int) position.z >> log2(Chunk.CHUNK_SIZE));

        int renderDistance = 6;

        for (int x = chunkX - renderDistance; x < chunkX + renderDistance; x++) {
            for (int y = chunkY - renderDistance; y < chunkY + renderDistance; y++) {
                for (int z = chunkZ - renderDistance; z < chunkZ + renderDistance; z++) {
                    //loadChunk(x, y, z);
                }
            }
        }
    }
}
