package com.ezzenix.game.threads;

import com.ezzenix.engine.utils.BlockPos;
import com.ezzenix.engine.utils.WorkerThread;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.chunk.Chunk;
import com.ezzenix.game.world.WorldGenerator;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.HashMap;

public class WorldGeneratorThread {
    public static class WorldGeneratorOutput {
        public Chunk chunk;
        public HashMap<BlockPos, BlockType> blocks;

        public WorldGeneratorOutput(Chunk chunk) {
            this.chunk = chunk;
            this.blocks = new HashMap<>();
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
                    //System.out.println(output);
                    for (BlockPos blockPos : output.blocks.keySet()) {
                        BlockType blockType = output.blocks.get(blockPos);
                        output.chunk.setBlock(blockPos, blockType);
                    }
                    output.chunk.scheduleForRemesh();
                    return null;
                }
        );
    }

    public static synchronized void scheduleChunkForWorldGeneration(Chunk chunk) {
        if (chunk.hasGenerated) return;
        workerThread.add(chunk);
    }
}