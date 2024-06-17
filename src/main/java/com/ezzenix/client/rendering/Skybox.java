package com.ezzenix.client.rendering;

import com.ezzenix.client.Client;
import com.ezzenix.engine.opengl.Mesh;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import org.joml.Matrix4f;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;

public class Skybox {
	Texture texture;
	Shader shader;
	Mesh mesh;

	public Skybox() {
		try {
			texture = new Texture(ImageIO.read(new File("src/main/resources/skybox.png")));
		} catch (IOException e) {
			System.err.println("Failed to load skybox: " + e);
		}

		shader = new Shader("skybox");

		mesh = createMesh();
	}

	private Mesh createMesh() {
		float[] vertices = new float[]{
			-1.0f, 1.0f, -1.0f,// 0.0f, 0.0f,
			-1.0f, -1.0f, -1.0f,// 0.0f, 1.0f,
			1.0f, -1.0f, -1.0f,// 1.0f, 1.0f,
			1.0f, -1.0f, -1.0f,// 1.0f, 1.0f,
			1.0f, 1.0f, -1.0f,// 1.0f, 0.0f,
			-1.0f, 1.0f, -1.0f,// 0.0f, 0.0f,

			-1.0f, -1.0f, 1.0f,// 0.0f, 1.0f,
			-1.0f, -1.0f, -1.0f,// 0.0f, 0.0f,
			-1.0f, 1.0f, -1.0f,// 1.0f, 0.0f,
			-1.0f, 1.0f, -1.0f,// 1.0f, 0.0f,
			-1.0f, 1.0f, 1.0f,// 1.0f, 1.0f,
			-1.0f, -1.0f, 1.0f,// 0.0f, 1.0f,

			1.0f, -1.0f, -1.0f,// 0.0f, 0.0f,
			1.0f, -1.0f, 1.0f,// 0.0f, 1.0f,
			1.0f, 1.0f, 1.0f,// 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,// 1.0f, 1.0f,
			1.0f, 1.0f, -1.0f,// 1.0f, 0.0f,
			1.0f, -1.0f, -1.0f,// 0.0f, 0.0f,

			-1.0f, -1.0f, 1.0f,// 1.0f, 1.0f,
			-1.0f, 1.0f, 1.0f,// 1.0f, 0.0f,
			1.0f, 1.0f, 1.0f,// 0.0f, 0.0f,
			1.0f, 1.0f, 1.0f,// 0.0f, 0.0f,
			1.0f, -1.0f, 1.0f,// 0.0f, 1.0f,
			-1.0f, -1.0f, 1.0f,// 1.0f, 1.0f,

			-1.0f, 1.0f, -1.0f,// 0.0f, 0.0f,
			1.0f, 1.0f, -1.0f,// 1.0f, 0.0f,
			1.0f, 1.0f, 1.0f,// 1.0f, 1.0f,
			1.0f, 1.0f, 1.0f,// 1.0f, 1.0f,
			-1.0f, 1.0f, 1.0f,// 0.0f, 1.0f,
			-1.0f, 1.0f, -1.0f,// 0.0f, 0.0f,

			-1.0f, -1.0f, -1.0f,// 0.0f, 1.0f,
			-1.0f, -1.0f, 1.0f,// 0.0f, 0.0f,
			1.0f, -1.0f, -1.0f,// 1.0f, 1.0f,
			1.0f, -1.0f, -1.0f,// 1.0f, 1.0f,
			-1.0f, -1.0f, 1.0f,// 0.0f, 0.0f,
			1.0f, -1.0f, 1.0f,// 1.0f, 0.0f
		};

		Mesh mesh = new Mesh(Mesh.convertToBuffer(vertices), 36, GL_TRIANGLES);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, 3, 0);
		glEnableVertexAttribArray(0);

		mesh.unbind();

		return mesh;
	}

	public void render() {
		glDisable(GL_CULL_FACE);

		glDepthFunc(GL_ALWAYS);

		texture.bind();
		shader.bind();

		Camera camera = Client.getCamera();
		shader.setUniform("projectionMatrix", camera.getProjectionMatrix());
		shader.setUniform("viewMatrix", new Matrix4f(camera.getViewMatrix()).m30(0).m31(0).m32(0));

		mesh.render();

		glDepthFunc(GL_LESS); // Restore default depth function

		glEnable(GL_CULL_FACE);
	}
}
