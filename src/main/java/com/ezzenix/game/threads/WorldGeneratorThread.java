package com.ezzenix.game.threads;

import com.ezzenix.game.BlockPos;
import com.ezzenix.engine.core.WorkerThread;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.game.world.World;
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

        public BlockType getBlock(BlockPos blockPos) {
            return this.blocks.getOrDefault(blockPos, BlockType.AIR);
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
                    //World world = output.chunk.getWorld();
                    for (BlockPos blockPos : output.blocks.keySet()) {
                        BlockType blockType = output.blocks.get(blockPos);
                        output.chunk.setBlock(blockPos, blockType);
                        //world.setBlock(blockPos, blockType);
                    }
                    output.chunk.flagMeshForUpdate(false);
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