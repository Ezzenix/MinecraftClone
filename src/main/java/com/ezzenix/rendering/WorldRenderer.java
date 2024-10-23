package com.ezzenix.rendering;

import com.ezzenix.Client;
import com.ezzenix.Debug;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.enums.Direction;
import com.ezzenix.enums.SubmersionType;
import com.ezzenix.gui.Color;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.rendering.chunk.BuiltChunkStorage;
import com.ezzenix.rendering.chunk.ChunkBuilder;
import com.ezzenix.rendering.chunk.UnitCube;
import com.ezzenix.rendering.util.BufferBuilder;
import com.ezzenix.rendering.util.RenderLayer;
import com.ezzenix.rendering.util.VertexBuffer;
import com.ezzenix.rendering.util.VertexFormat;
import com.ezzenix.util.Identifier;
import com.ezzenix.world.World;
import com.ezzenix.world.chunk.Chunk;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class WorldRenderer {
	public final Shader worldShader = new Shader("world.vert", "world.frag");
	public final Shader waterShader = new Shader("water.vert", "water.frag");

	private final Shader blockOverlayShader;

	//private final VertexBuffer blockOverlayBuffer;
	private final static BufferBuilder.Immediate immediate = new BufferBuilder.Immediate();

	public int chunksRenderedCount = 0;

	private BuiltChunkStorage builtChunkStorage;


	public WorldRenderer() {
		this.blockOverlayShader = new Shader("block_overlay");
		this.blockOverlayShader.setTexture(0, Client.getTextureManager().getTexture(Identifier.of("break_overlay")));
		//this.blockOverlayBuffer = new VertexBuffer(new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2), VertexBuffer.Usage.DYNAMIC);

		this.builtChunkStorage = new BuiltChunkStorage();
	}

	public void render() {
		World world = Client.getWorld();
		if (world == null) return;

		boolean renderWireframe = Debug.wireframeMode;
		if (renderWireframe) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		this.builtChunkStorage.update();
		ChunkBuilder.pollQueue();

		this.updateFog();

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
	}

	public void updateFog() {
		SubmersionType submersionType = Client.getCamera().getCameraSubmersionType();
		if (submersionType == SubmersionType.WATER) {
			RenderSystem.setShaderFogColor(Color.pack(0.2f, 0.2f, 1f, 1f));
			RenderSystem.setShaderFogStartEnd(0, 40);
		} else {
			RenderSystem.setShaderFogColor(-1);
			RenderSystem.setShaderFogStartEnd(80, 850);
		}
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
		Vector3f mid = blockPos.toVector3f();

		int stage = Math.round(progress*9);
		RenderLayer layer = RenderLayer.BREAK_OVERLAYS[stage];
		BufferBuilder builder = immediate.getBuilder(layer);

		for (Direction direction : Direction.values()) {
			Vector3f[] face = UnitCube.getFace(direction);

			Vector3f v0 = new Vector3f(mid).add(new Vector3f(face[0]).mul(1.01f));
			Vector3f v1 = new Vector3f(mid).add(new Vector3f(face[1]).mul(1.01f));
			Vector3f v2 = new Vector3f(mid).add(new Vector3f(face[2]).mul(1.01f));
			Vector3f v3 = new Vector3f(mid).add(new Vector3f(face[3]).mul(1.0f));

			builder.vertex(v0).texture(0, 0).next();
			builder.vertex(v1).texture(0, 1).next();
			builder.vertex(v2).texture(1, 1).next();
			builder.vertex(v2).texture(1, 1).next();
			builder.vertex(v3).texture(1, 0).next();
			builder.vertex(v0).texture(0, 0).next();
		}

		immediate.draw(layer);
	}

	public void reloadAllChunks() {
		Client.LOGGER.info("Reloading all chunks!");
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
