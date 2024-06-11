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

import java.util.ArrayList;
import java.util.List;

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

		List<Chunk> chunks = new ArrayList<>(world.getChunks().values().stream().toList());
		chunks.sort((a, b) -> {
			float dA = camera.getPosition().distance(a.getWorldPos().add((float) Chunk.CHUNK_WIDTH / 2, 0, (float) Chunk.CHUNK_WIDTH / 2));
			float dB = camera.getPosition().distance(b.getWorldPos().add((float) Chunk.CHUNK_WIDTH / 2, 0, (float) Chunk.CHUNK_WIDTH / 2));
			return Float.compare(dB, dA);
		});

		worldShader.use();
		worldShader.setUniform("projectionMatrix", projectionMatrix);
		worldShader.setUniform("viewMatrix", viewMatrix);
		worldShader.setUniform("textureAtlasSize", textureAtlasSize);
		for (Chunk chunk : chunks) {
			//if (!chunk.frustumBoundingBox.isShown) continue;
			ChunkMesh chunkMesh = chunk.getChunkMesh();
			worldShader.setUniform("chunkPosition", chunkMesh.getTranslationMatrix());
			chunkMesh.renderBlocks();
		}

		waterShader.use();
		waterShader.setUniform("projectionMatrix", projectionMatrix);
		waterShader.setUniform("viewMatrix", viewMatrix);
		waterShader.setUniform("textureAtlasSize", textureAtlasSize);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDepthFunc(GL_LESS);
		glDepthMask(false);
		for (Chunk chunk : chunks) {
			//if (!chunk.frustumBoundingBox.isShown) continue;
			ChunkMesh chunkMesh = chunk.getChunkMesh();
			waterShader.setUniform("chunkPosition", chunkMesh.getTranslationMatrix());
			chunkMesh.renderWater();
		}
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDepthMask(true);
	}
}
