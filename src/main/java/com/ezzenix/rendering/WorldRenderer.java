package com.ezzenix.rendering;

import com.ezzenix.Debug;
import com.ezzenix.Client;
import com.ezzenix.enums.Direction;
import com.ezzenix.math.BlockPos;
import com.ezzenix.rendering.chunkbuilder.ChunkBuilder;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import com.ezzenix.math.ChunkPos;
import com.ezzenix.rendering.chunkbuilder.UnitCube;
import com.ezzenix.rendering.util.VertexBuffer;
import com.ezzenix.rendering.util.VertexFormat;
import com.ezzenix.resource.ResourceManager;
import com.ezzenix.world.chunk.Chunk;
import com.ezzenix.world.World;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class WorldRenderer {
	public final Shader worldShader = new Shader("world.vert", "world.frag");
	public final Shader waterShader = new Shader("water.vert", "water.frag");
	public final Texture blockTexture;

	private final Vector2f textureAtlasSize;

	private final Shader blockOverlayShader;
	private final VertexBuffer blockOverlayBuffer;

	public int chunksRenderedCount = 0;

	public WorldRenderer() {
		BufferedImage blockAtlasImage = Client.getTextureManager().blockAtlas.getAtlasImage();
		blockTexture = new Texture(blockAtlasImage);
		textureAtlasSize = new Vector2f(blockAtlasImage.getWidth(), blockAtlasImage.getHeight());
		//blockTexture.generateMipmap();
		//blockTexture.setParameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		blockTexture.setParameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		this.blockOverlayShader = new Shader("block_overlay");
		this.blockOverlayShader.setTexture(0, new Texture(ResourceManager.loadImage("break_overlay.png")));
		this.blockOverlayBuffer = new VertexBuffer(blockOverlayShader, new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2), VertexBuffer.Usage.DYNAMIC);
	}

	public void render(long window) {
		World world = Client.getWorld();
		if (world == null) return;

		boolean renderWireframe = Debug.wireframeMode;
		if (renderWireframe) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

		blockTexture.bind();

		Camera camera = Client.getCamera();
		ChunkPos cameraChunkPos = new ChunkPos(camera.getPosition());

		Matrix4f projectionMatrix = camera.getProjectionMatrix();
		Matrix4f viewMatrix = camera.getViewMatrix();

		FrustumIntersection frustumIntersection = new FrustumIntersection().set(new Matrix4f().set(projectionMatrix).mul(viewMatrix));

		// Get chunks in frustum and sort them by distance to camera
		List<Chunk> chunks = new ArrayList<>(world.getChunkManager().getChunks().stream().filter(chunk -> chunk.getBoundingBox().checkFrustum(frustumIntersection)).toList());
		chunks.sort((a, b) -> Float.compare(cameraChunkPos.distanceTo(b.getPos()), cameraChunkPos.distanceTo(a.getPos())));
		chunksRenderedCount = chunks.size();

		worldShader.bind();
		worldShader.setUniforms();
		for (Chunk chunk : chunks) {
			ChunkBuilder.BuiltChunk builtChunk = chunk.getBuiltChunk();
			worldShader.setModelMatrix(builtChunk.getTranslationMatrix());
			builtChunk.vertexBufferSolid.draw();
		}

		waterShader.bind();
		waterShader.setUniforms();
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		glDepthMask(false);
		for (Chunk chunk : chunks) {
			ChunkBuilder.BuiltChunk builtChunk = chunk.getBuiltChunk();
			waterShader.setModelMatrix(builtChunk.getTranslationMatrix());
			builtChunk.vertexBufferTransparent.draw();
		}
		//glDisable(GL_BLEND);
		//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDepthMask(true);

		if (Client.getInteractionManager().getBreakingPos() != null) {
			renderDamage(Client.getInteractionManager().getBreakingPos(), Client.getInteractionManager().getBreakingProgress());
		}

		if (renderWireframe) glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	}

	public void renderDamage(BlockPos blockPos, float progress) {
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
	}

	public void reloadAllChunks() {
		System.out.println("Reloading all chunks!");
		Client.getHud().chatHud.addMessage("Reloading all chunks");
		World world = Client.getWorld();
		for (Chunk chunk : world.getChunkManager().getChunks()) {
			ChunkBuilder.BuiltChunk builtChunk = chunk.getBuiltChunk();
			if (builtChunk != null) {
				builtChunk.dispose();
				builtChunk.rebuild();
			}
		}
	}
}
