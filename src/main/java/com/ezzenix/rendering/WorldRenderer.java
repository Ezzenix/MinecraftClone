package com.ezzenix.rendering;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import com.ezzenix.game.chunkbuilder.ChunkMesh;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.game.world.World;
import com.ezzenix.hud.Debug;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.Collection;

import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer {
	private final Shader worldShader = new Shader("world.vert", "world.frag");
	private final Shader waterShader = new Shader("water.vert", "water.frag");
	private final Texture blockTexture;

	private final Vector2f textureAtlasSize;

	public boolean drawChunkBorders = false;

	public WorldRenderer() {
		blockTexture = new Texture(Game.getInstance().blockTextures.getAtlasImage());
		textureAtlasSize = new Vector2f(
			Game.getInstance().blockTextures.getAtlasImage().getWidth(),
			Game.getInstance().blockTextures.getAtlasImage().getHeight()
		);
		//blockTexture.generateMipmap();
		//blockTexture.setParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		blockTexture.setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	}

	public void render(long window) {
		World world = Game.getInstance().getWorld();
		if (world == null) return;

		if (drawChunkBorders) {
			Debug.drawChunkBorders();
		}

		blockTexture.bind();

		Camera camera = Game.getInstance().getCamera();
		Matrix4f projectionMatrix = camera.getProjectionMatrix();
		Matrix4f viewMatrix = camera.getViewMatrix();
		Matrix4f viewProjectionMatrix = camera.getViewProjectionMatrix();

		Collection<Chunk> chunks = world.getChunks().values();

		// Frustum culling
		//for (Chunk chunk : world.getChunks().values()) {
		//    chunk.frustumBoundingBox.isShown = chunk.frustumBoundingBox.isInsideFrustum(viewProjectionMatrix);
		//}

		worldShader.use();
		worldShader.uploadMat4f("projectionMatrix", projectionMatrix);
		worldShader.uploadMat4f("viewMatrix", viewMatrix);
		worldShader.uploadVec2f("textureAtlasSize", textureAtlasSize);
		for (Chunk chunk : chunks) {
			//if (!chunk.frustumBoundingBox.isShown) continue;
			ChunkMesh chunkMesh = chunk.getChunkMesh();
			worldShader.uploadMat4f("chunkPosition", chunkMesh.getTranslationMatrix());
			chunkMesh.renderBlocks();
		}

		waterShader.use();
		waterShader.uploadMat4f("projectionMatrix", projectionMatrix);
		waterShader.uploadMat4f("viewMatrix", viewMatrix);
		waterShader.uploadVec2f("textureAtlasSize", textureAtlasSize);
		//long timestamp = System.currentTimeMillis();
		//waterShader.uploadFloat("timestamp", (float) timestamp);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDepthMask(false);
		for (Chunk chunk : chunks) {
			//if (!chunk.frustumBoundingBox.isShown) continue;
			ChunkMesh chunkMesh = chunk.getChunkMesh();
			waterShader.uploadMat4f("chunkPosition", chunkMesh.getTranslationMatrix());
			chunkMesh.renderWater();
		}
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDepthMask(true);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		glEnable(GL_CULL_FACE);
	}
}
