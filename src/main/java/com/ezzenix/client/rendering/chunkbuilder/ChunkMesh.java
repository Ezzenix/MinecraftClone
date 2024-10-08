package com.ezzenix.client.rendering.chunkbuilder;

import com.ezzenix.engine.opengl.Mesh;
import com.ezzenix.world.Chunk;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;

public class ChunkMesh {
	private final Chunk chunk;
	private final Matrix4f translationMatrix;
	public Mesh blockMesh;
	public Mesh waterMesh;

	public ChunkMesh(Chunk chunk) {
		this.chunk = chunk;
		this.translationMatrix = new Matrix4f().translate(new Vector3f(chunk.getPos().x * Chunk.CHUNK_WIDTH, 0, chunk.getPos().z * Chunk.CHUNK_WIDTH));
	}

	private Mesh createMesh(FloatBuffer buffer, int length) {
		Mesh mesh = new Mesh(buffer, length);

		int stride = 6 * Float.BYTES;
		glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3 * Float.BYTES);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2, 1, GL_FLOAT, false, stride, 5 * Float.BYTES);
		glEnableVertexAttribArray(2);

		mesh.unbind();
		return mesh;
	}

	public void applyRequest(ChunkBuildRequest request) {
		dispose();

		if (chunk.blockCount > 0) {
			//System.out.print("creating meshes");
			blockMesh = createMesh(request.blockVertexBuffer, request.blockVertexLength);
			waterMesh = createMesh(request.waterVertexBuffer, request.waterVertexLength);
		}
	}

	public Mesh getBlockMesh() {
		return blockMesh;
	}
	public Mesh getWaterMesh() {
		return waterMesh;
	}

	public void renderBlocks() {
		if (blockMesh != null) blockMesh.render();
	}

	public void renderWater() {
		if (waterMesh != null) waterMesh.render();
	}

	public Matrix4f getTranslationMatrix() {
		return translationMatrix;
	}

	public void dispose() {
		if (blockMesh != null) {
			blockMesh.dispose();
			blockMesh = null;
		}
		if (waterMesh != null) {
			waterMesh.dispose();
			waterMesh = null;
		}
		;
	}
}
