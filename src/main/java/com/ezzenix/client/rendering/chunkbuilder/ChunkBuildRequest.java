package com.ezzenix.client.rendering.chunkbuilder;

import com.ezzenix.game.world.Chunk;

import java.nio.FloatBuffer;

public class ChunkBuildRequest {
	public Chunk chunk;

	public FloatBuffer blockVertexBuffer;
	public int blockVertexLength;

	public FloatBuffer waterVertexBuffer;
	public int waterVertexLength;

	public ChunkBuildRequest(Chunk chunk) {
		this.chunk = chunk;
	}
}