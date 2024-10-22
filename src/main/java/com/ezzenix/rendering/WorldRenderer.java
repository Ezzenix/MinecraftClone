package com.ezzenix.rendering;

import com.ezzenix.Client;
import com.ezzenix.Debug;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.rendering.chunk.BuiltChunkStorage;
import com.ezzenix.rendering.chunk.ChunkBuilder;
import com.ezzenix.rendering.util.RenderLayer;
import com.ezzenix.rendering.util.VertexBuffer;
import com.ezzenix.rendering.util.VertexFormat;
import com.ezzenix.util.Identifier;
import com.ezzenix.world.World;
import com.ezzenix.world.chunk.Chunk;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class WorldRenderer {
	public final Shader worldShader = new Shader("world.vert", "world.frag");
	public final Shader waterShader = new Shader("water.vert", "water.frag");

	private final Shader blockOverlayShader;
	//private final VertexBuffer blockOverlayBuffer;

	public int chunksRenderedCount = 0;

	private BuiltChunkStorage builtChunkStorage;

	public WorldRenderer() {
		this.blockOverlayShader = new Shader("block_overlay");
		this.blockOverlayShader.setTexture(0, Client.getTextureManager().getTexture(Identifier.of("break_overlay")));
		//this.blockOverlayBuffer = new VertexBuffer(new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2), VertexBuffer.Usage.DYNAMIC);

		this.builtChunkStorage = new BuiltChunkStorage();
	}

	public void render() {
		/*
		World world = Client.getWorld();
		if (world == null) return;

		boolean renderWireframe = Debug.wireframeMode;
		if (renderWireframe) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		RenderSystem.setShaderFogColor(-1);
		RenderSystem.setShaderFogStartEnd(100, 900);

		this.builtChunkStorage.update();
		ChunkBuilder.pollQueue();

		Client.getTextureManager().blockAtlas.getTexture().bind();

		Camera camera = Client.getCamera();
		ChunkPos cameraChunkPos = new ChunkPos(camera.getPosition());

		Matrix4f projectionMatrix = camera.getProjectionMatrix();
		Matrix4f viewMatrix = camera.getViewMatrix();

		FrustumIntersection frustumIntersection = new FrustumIntersection().set(new Matrix4f().set(projectionMatrix).mul(viewMatrix));

		// Get chunks in frustum and sort them by distance to camera
		List<Chunk> chunks = new ArrayList<>(world.getChunkManager().getChunks().stream().filter(chunk -> chunk.getBoundingBox().checkFrustum(frustumIntersection)).toList());
		chunks.sort((a, b) -> Integer.compare(cameraChunkPos.distanceTo(b.getPos()), cameraChunkPos.distanceTo(a.getPos())));
		chunksRenderedCount = chunks.size();

		renderChunkLayer(RenderLayer.SOLID, chunks);
		renderChunkLayer(RenderLayer.TRANSLUCENT, chunks);

		if (Client.getInteractionManager().getBreakingPos() != null) {
			renderDamage(Client.getInteractionManager().getBreakingPos(), Client.getInteractionManager().getBreakingProgress());
		}

		if (renderWireframe) glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		 */
	}

	public void renderChunkLayer(RenderLayer layer, List<Chunk> chunks) {
		layer.apply();
		for (Chunk chunk : chunks) {
			ChunkBuilder.BuiltChunk builtChunk = getBuiltChunkStorage().getBuiltChunk(chunk);
			if (builtChunk != null) {
				layer.getShader().setModelMatrix(builtChunk.getTranslationMatrix());
				VertexBuffer vertexBuffer = builtChunk.buffers.get(layer);
				vertexBuffer.draw();
			}
		}
		layer.unapply();
	}

	public void renderDamage(BlockPos blockPos, float progress) {
		/*
		float x = blockPos.x;
		float y = blockPos.y;
		float z = blockPos.z;

		for (Direction direction : Direction.values()) {

			Vector3f[] face = UnitCube.getFace(direction);

			Vector3f v0 = new Vector3f(blockPos.x, blockPos.y, blockPos.z).add(face[0]);
			Vector3f v1 = new Vector3f(blockPos.x, blockPos.y, blockPos.z).add(face[1]);
			Vector3f v2 = new Vector3f(blockPos.x, blockPos.y, blockPos.z).add(face[2]);
			Vector3f v3 = new Vector3f(blockPos.x, blockPos.y, blockPos.z).add(face[3]);

			blockOverlayBuffer.vertex(v0).texture(0, 0).next();
			blockOverlayBuffer.vertex(v1).texture(0, 1).next();
			blockOverlayBuffer.vertex(v2).texture(1, 1).next();

			blockOverlayBuffer.vertex(v2).texture(1, 1).next();
			blockOverlayBuffer.vertex(v3).texture(1, 0).next();
			blockOverlayBuffer.vertex(v0).texture(0, 0).next();

		}

		blockOverlayBuffer.upload();

		blockOverlayShader.bind();
		blockOverlayShader.setUniforms();
		blockOverlayBuffer.draw();
		 */
	}

	public void reloadAllChunks() {
		System.out.println("Reloading all chunks!");
		Client.getHud().chatHud.addMessage("Reloading all chunks");
		World world = Client.getWorld();
		for (Chunk chunk : world.getChunkManager().getChunks()) {
			ChunkBuilder.BuiltChunk builtChunk = chunk.getBuiltChunk();
			if (builtChunk != null) {
				builtChunk.clear();
				builtChunk.scheduleRebuild();
			}
		}
	}

	public BuiltChunkStorage getBuiltChunkStorage() {
		return this.builtChunkStorage;
	}
}
