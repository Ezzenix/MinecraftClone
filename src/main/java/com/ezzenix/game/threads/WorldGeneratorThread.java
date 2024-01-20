package com.ezzenix.game.threads;

import com.ezzenix.engine.core.BlockPos;
import com.ezzenix.engine.core.WorkerThread;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.chunk.Chunk;
import com.ezzenix.game.world.WorldGenerator;

import java.util.HashMap;

public class WorldGeneratorThread {
    public static class WorldGeneratorOutput {
        public Chunk chunk;
        public HashMap<BlockPos, BlockType> blocks;

        public WorldGeneratorOutput(Chunk chunk) {
            this.chunk = chunk;
            this.blocks = new HashMap<>();
        }

        public void setBlock(BlockPos blockPos, BlockType blockType) {
            this.blocks.put(blockPos, blockType);
        }
    }

    private static WorkerThread<Chunk, WorldGeneratorOutput> workerThread;

    static {
        workerThread = new WorkerThread<>(
                1,
                50,
                30,
                WorldGenerator::generateChunk,
                (output) -> {
                    for (BlockPos blockPos : output.blocks.keySet()) {
                        BlockType blockType = output.blocks.get(blockPos);
                        output.chunk.setBlock(blockPos, blockType);
                    }
                    output.chunk.updateMesh(false);
                    output.blocks.clear(); // free memory (maybe)
                    return null;
                }
        );
    }

    public static synchronized void scheduleChunkForWorldGeneration(Chunk chunk) {
        if (chunk.hasGenerated) return;
        workerThread.add(chunk);
    }
}