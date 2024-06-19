package com.ezzenix.client.rendering;

import com.ezzenix.client.Client;
import com.ezzenix.client.rendering.util.VertexBuffer;
import com.ezzenix.client.rendering.util.VertexFormat;
import com.ezzenix.client.resource.ResourceManager;
import com.ezzenix.engine.opengl.Mesh;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import org.joml.Matrix4f;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

public class Skybox {
	Texture texture;
	Shader shader;
	Mesh mesh;

	VertexBuffer vertexBuffer;

	public Skybox() {
		this.shader = new Shader("skybox");

		initVertexBuffer();

		texture = new Texture(ResourceManager.loadImage("skybox.png"));
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
		this.vertexBuffer.shader.bind();
		this.vertexBuffer.shader.setUniform("projectionMatrix", Client.getCamera().getProjectionMatrix());
		this.vertexBuffer.shader.setUniform("viewMatrix", Client.getCamera().getViewMatrix());
		this.vertexBuffer.shader.setUniform("modelMatrix", new Matrix4f().translate(Client.getCamera().getPosition()));

		this.texture.bind();

		this.vertexBuffer.draw();
	}
}
