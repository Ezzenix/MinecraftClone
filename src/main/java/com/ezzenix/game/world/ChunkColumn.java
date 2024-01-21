package com.ezzenix.game.world;

import com.ezzenix.game.world.chunk.Chunk;
import com.ezzenix.game.core.ChunkPos;

public class ChunkColumn {
	public static int HEIGHT = 256 / Chunk.CHUNK_SIZE;

	private Chunk[] chunks;

	public boolean hasGenerated = false;

	public Chunk getChunk(ChunkPos chunkPos) {
		return chunks[chunkPos.getY()];
	}
}
