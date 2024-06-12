package com.ezzenix.rendering;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import com.ezzenix.rendering.chunkbuilder.ChunkMesh;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.game.world.World;
import com.ezzenix.hud.LineRenderer;
import com.ezzenix.math.ChunkPos;
import org.joml.FrustumIntersection;
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

		blockTexture.bind();

		Camera camera = Game.getInstance().getCamera();
		ChunkPos cameraChunkPos = ChunkPos.from(camera.getPosition());

		Matrix4f projectionMatrix = camera.getProjectionMatrix();
		Matrix4f viewMatrix = camera.getViewMatrix();

		FrustumIntersection frustumIntersection = new FrustumIntersection().set(new Matrix4f().set(projectionMatrix).mul(viewMatrix));

		// Get chunks in frustum and sort them by distance to camera
		List<Chunk> chunks = new ArrayList<>(world.getChunks().values().stream().filter(chunk -> chunk.getBoundingBox().checkFrustum(frustumIntersection)).toList());
		chunks.sort((a, b) -> Float.compare(cameraChunkPos.distanceTo(b.getPos()), cameraChunkPos.distanceTo(a.getPos())));

		worldShader.bind();
		worldShader.setUniform("projectionMatrix", projectionMatrix);
		worldShader.setUniform("viewMatrix", viewMatrix);
		worldShader.setUniform("textureAtlasSize", textureAtlasSize);
		for (Chunk chunk : chunks) {
			ChunkMesh chunkMesh = chunk.getChunkMesh();
			worldShader.setUniform("chunkPosition", chunkMesh.getTranslationMatrix());
			chunkMesh.renderBlocks();
		}

		waterShader.bind();
		waterShader.setUniform("projectionMatrix", projectionMatrix);
		waterShader.setUniform("viewMatrix", viewMatrix);
		waterShader.setUniform("textureAtlasSize", textureAtlasSize);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		glDepthMask(false);
		for (Chunk chunk : chunks) {
			ChunkMesh chunkMesh = chunk.getChunkMesh();
			waterShader.setUniform("chunkPosition", chunkMesh.getTranslationMatrix());
			chunkMesh.renderWater();
		}
		//glDisable(GL_BLEND);
		//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDepthMask(true);
	}

	public void reloadAllChunks() {
		System.out.println("Reloading all chunks!");
		World world = Game.getInstance().getWorld();
		for (Chunk chunk : world.getChunks().values()) {
			chunk.getChunkMesh().dispose();
			chunk.flagMeshForUpdate();
		}
	}
}
