package com.ezzenix.rendering;

import com.ezzenix.Client;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import com.ezzenix.util.Identifier;

public class Skybox {
	private final Shader shader;
	//private final VertexBuffer vertexBuffer;
	//private final VertexBuffer sunVertexBuffer;
	private final Texture skyboxTexture = Client.getTextureManager().getTexture(Identifier.of("sky/skybox"));
	private final Texture sunTexture = Client.getTextureManager().getTexture(Identifier.of("sky/sun"));

	public Skybox() {
		shader = new Shader("skybox");

		/*
		sunVertexBuffer = new VertexBuffer(new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2), VertexBuffer.Usage.STATIC);
		int SUN_SIZE = 8;
		sunVertexBuffer.vertex(-SUN_SIZE, -100, -SUN_SIZE).texture(0, 0).next();
		sunVertexBuffer.vertex(-SUN_SIZE, -100, SUN_SIZE).texture(0, 1).next();
		sunVertexBuffer.vertex(SUN_SIZE, -100, SUN_SIZE).texture(1, 1).next();
		sunVertexBuffer.vertex(SUN_SIZE, -100, SUN_SIZE).texture(1, 1).next();
		sunVertexBuffer.vertex(SUN_SIZE, -100, -SUN_SIZE).texture(1, 0).next();
		sunVertexBuffer.vertex(-SUN_SIZE, -100, -SUN_SIZE).texture(0, 0).next();
		sunVertexBuffer.upload();

		vertexBuffer = new VertexBuffer(new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2), VertexBuffer.Usage.STATIC);
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
		for (int i = 0; i < vertices.length; i += 5) {
			vertexBuffer.vertex(vertices[i] * SIZE, vertices[i + 1] * SIZE, vertices[i + 2] * SIZE).texture(vertices[i + 3], vertices[i + 4]).next();
		}
		vertexBuffer.upload();

		initVertexBuffer();
		 */
	}

	private void initVertexBuffer() {

	}

	public void render() {
		/*
		Matrix4f modelMatrix = new Matrix4f().translate(Client.getCamera().getPosition());

		shader.setTexture(0, skyboxTexture);
		shader.bind();
		shader.setUniforms();

		shader.setModelMatrix(modelMatrix);
		vertexBuffer.draw();

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
