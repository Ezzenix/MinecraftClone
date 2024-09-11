package com.ezzenix.client.rendering;

import com.ezzenix.client.Client;
import com.ezzenix.client.rendering.util.VertexBuffer;
import com.ezzenix.client.rendering.util.VertexFormat;
import com.ezzenix.client.resource.ResourceManager;
import com.ezzenix.engine.opengl.Mesh;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import org.joml.Math;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

public class Skybox {
	Texture texture;
	Shader shader;
	Mesh mesh;

	VertexBuffer vertexBuffer;

	VertexBuffer sunVertexBuffer;

	private final Texture skyboxTexture = new Texture(ResourceManager.loadImage("skybox.png"));
	private final Texture sunTexture = new Texture(ResourceManager.loadImage("sun.png"));

	public Skybox() {
		this.shader = new Shader("skybox");

		this.sunVertexBuffer = new VertexBuffer(this.shader, new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2), VertexBuffer.Usage.STATIC);
		int SUN_SIZE = 8;
		this.sunVertexBuffer.vertex(-SUN_SIZE, -100, -SUN_SIZE).texture(0, 0).next();
		this.sunVertexBuffer.vertex(-SUN_SIZE, -100, SUN_SIZE).texture(0, 1).next();
		this.sunVertexBuffer.vertex(SUN_SIZE, -100, SUN_SIZE).texture(1, 1).next();
		this.sunVertexBuffer.vertex(SUN_SIZE, -100, SUN_SIZE).texture(1, 1).next();
		this.sunVertexBuffer.vertex(SUN_SIZE, -100, -SUN_SIZE).texture(1, 0).next();
		this.sunVertexBuffer.vertex(-SUN_SIZE, -100, -SUN_SIZE).texture(0, 0).next();
		this.sunVertexBuffer.upload();

		initVertexBuffer();
	}

	private void initVertexBuffer() {
		VertexBuffer buffer = this.vertexBuffer = new VertexBuffer(this.shader, new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2), VertexBuffer.Usage.STATIC);

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
			buffer.vertex(vertices[i] * SIZE, vertices[i + 1] * SIZE, vertices[i + 2] * SIZE).texture(vertices[i + 3], vertices[i + 4]).next();
		}

		this.vertexBuffer.upload();
	}

	public void render() {
		Matrix4f modelMatrix = new Matrix4f().translate(Client.getCamera().getPosition());

		shader.bind();
		shader.setUniform("projectionMatrix", Client.getCamera().getProjectionMatrix());
		shader.setUniform("viewMatrix", Client.getCamera().getViewMatrix());

		shader.setTexture(0, skyboxTexture);
		shader.setUniform("modelMatrix", modelMatrix);
		vertexBuffer.draw();

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		glDepthMask(false);

		modelMatrix.rotate(Math.toRadians(180 + 60), 1, 0, 0);

		shader.setTexture(0, sunTexture);
		shader.setUniform("modelMatrix", modelMatrix);
		sunVertexBuffer.draw();

		glDepthMask(true);
	}
}
