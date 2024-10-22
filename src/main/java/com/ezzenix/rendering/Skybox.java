package com.ezzenix.rendering;

import com.ezzenix.Client;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import com.ezzenix.rendering.util.BufferBuilder;
import com.ezzenix.rendering.util.RenderLayer;
import com.ezzenix.rendering.util.VertexBuffer;
import com.ezzenix.rendering.util.VertexFormat;
import com.ezzenix.util.Identifier;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

public class Skybox {
	//private final VertexBuffer vertexBuffer;
	//private final VertexBuffer sunVertexBuffer;
	private final Texture skyboxTexture = Client.getTextureManager().getTexture(Identifier.of("sky/skybox"));
	private final Texture sunTexture = Client.getTextureManager().getTexture(Identifier.of("sky/sun"));

	private final Shader SHADER = new Shader("skybox");

	private final RenderLayer LAYER = new RenderLayer(SHADER).blend(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA).depth(GL_LESS);
	private final VertexBuffer SKY_BUFFER = new VertexBuffer(new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2), VertexBuffer.Usage.STATIC);
	private final VertexBuffer SUN_BUFFER = new VertexBuffer(new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2), VertexBuffer.Usage.STATIC);

	public Skybox() {

		BufferBuilder sunBuilder = new BufferBuilder(LAYER.getExpectedBufferSize());
		int SUN_SIZE = 8;
		sunBuilder.vertex(-SUN_SIZE, -100, -SUN_SIZE).texture(0, 0).next();
		sunBuilder.vertex(-SUN_SIZE, -100, SUN_SIZE).texture(0, 1).next();
		sunBuilder.vertex(SUN_SIZE, -100, SUN_SIZE).texture(1, 1).next();
		sunBuilder.vertex(SUN_SIZE, -100, SUN_SIZE).texture(1, 1).next();
		sunBuilder.vertex(SUN_SIZE, -100, -SUN_SIZE).texture(1, 0).next();
		sunBuilder.vertex(-SUN_SIZE, -100, -SUN_SIZE).texture(0, 0).next();
		SUN_BUFFER.upload(sunBuilder);

		float[] vertices = new float[]{
			// Bottom face
			-1.0f, -1.0f, -1.0f, 0 / 3f, 0 / 2f,
			-1.0f, -1.0f, 1.0f, 0 / 3f, 1 / 2f,
			1.0f, -1.0f, 1.0f, 1 / 3f, 1 / 2f,
			1.0f, -1.0f, 1.0f, 1 / 3f, 1 / 2f,
			1.0f, -1.0f, -1.0f, 1 / 3f, 0 / 2f,
			-1.0f, -1.0f, -1.0f, 0 / 3f, 0 / 2f,

			// Top face
			-1.0f, 1.0f, -1.0f, 2 / 3f, 0 / 2f,
			1.0f, 1.0f, -1.0f, 1 / 3f, 0 / 2f,
			1.0f, 1.0f, 1.0f, 1 / 3f, 1 / 2f,
			1.0f, 1.0f, 1.0f, 1 / 3f, 1 / 2f,
			-1.0f, 1.0f, 1.0f, 2 / 3f, 1 / 2f,
			-1.0f, 1.0f, -1.0f, 2 / 3f, 0 / 2f,

			// Front face
			-1.0f, -1.0f, -1.0f, 2 / 3f, 1 / 2f,
			1.0f, -1.0f, -1.0f, 3 / 3f, 1 / 2f,
			1.0f, 1.0f, -1.0f, 3 / 3f, 0 / 2f,
			1.0f, 1.0f, -1.0f, 3 / 3f, 0 / 2f,
			-1.0f, 1.0f, -1.0f, 2 / 3f, 0 / 2f,
			-1.0f, -1.0f, -1.0f, 2 / 3f, 1 / 2f,

			// Right face
			1.0f, -1.0f, -1.0f, 0 / 3f, 2 / 2f,
			1.0f, -1.0f, 1.0f, 1 / 3f, 2 / 2f,
			1.0f, 1.0f, 1.0f, 1 / 3f, 1 / 2f,
			1.0f, 1.0f, 1.0f, 1 / 3f, 1 / 2f,
			1.0f, 1.0f, -1.0f, 0 / 3f, 1 / 2f,
			1.0f, -1.0f, -1.0f, 0 / 3f, 2 / 2f,

			// Back face
			1.0f, -1.0f, 1.0f, 1 / 3f, 2 / 2f,
			-1.0f, -1.0f, 1.0f, 2 / 3f, 2 / 2f,
			-1.0f, 1.0f, 1.0f, 2 / 3f, 1 / 2f,
			-1.0f, 1.0f, 1.0f, 2 / 3f, 1 / 2f,
			1.0f, 1.0f, 1.0f, 1 / 3f, 1 / 2f,
			1.0f, -1.0f, 1.0f, 1 / 3f, 2 / 2f,

			// Left face
			-1.0f, -1.0f, 1.0f, 2 / 3f, 2 / 2f,
			-1.0f, -1.0f, -1.0f, 3 / 3f, 2 / 2f,
			-1.0f, 1.0f, -1.0f, 3 / 3f, 1 / 2f,
			-1.0f, 1.0f, -1.0f, 3 / 3f, 1 / 2f,
			-1.0f, 1.0f, 1.0f, 2 / 3f, 1 / 2f,
			-1.0f, -1.0f, 1.0f, 2 / 3f, 2 / 2f,
		};
		float SIZE = 1000;
		BufferBuilder skyBuilder = new BufferBuilder(LAYER.getExpectedBufferSize());
		for (int i = 0; i < vertices.length; i += 5) {
			skyBuilder.vertex(vertices[i] * SIZE, vertices[i + 1] * SIZE, vertices[i + 2] * SIZE).texture(vertices[i + 3], vertices[i + 4]).next();
		}
		SKY_BUFFER.upload(skyBuilder);
		skyBuilder.close();
	}

	public void render() {
		Matrix4f modelMatrix = new Matrix4f().translate(Client.getCamera().getPosition());
		SHADER.setTexture(0, skyboxTexture);
		LAYER.apply();
		SHADER.setModelMatrix(modelMatrix);
		SKY_BUFFER.draw();

		SHADER.setTexture(0, sunTexture);
		SHADER.bind();
		modelMatrix.rotate((float) Math.toRadians(180 + 60), 1, 0, 0);
		SHADER.setModelMatrix(modelMatrix);
		SUN_BUFFER.draw();

		LAYER.unapply();

		/*
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		glDepthMask(false);

		modelMatrix.rotate(Math.toRadians(180 + 60), 1, 0, 0);

		shader.setTexture(0, sunTexture);
		shader.bind();
		shader.setModelMatrix(modelMatrix);
		sunVertexBuffer.draw();

		glDepthMask(true);
		 */
	}
}
