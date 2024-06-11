package com.ezzenix.skybox;

import com.ezzenix.Game;
import com.ezzenix.engine.opengl.Mesh;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.engine.opengl.Texture;
import com.ezzenix.rendering.Camera;
import org.joml.Matrix4f;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

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

		shader = new Shader("skybox.vert", "skybox.frag");

		mesh = createMesh();
	}

	private Mesh createMesh() {
		float[] vertices = {
			// positions        // texture Coords
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

		System.out.println("Created skybox mesh");

		return mesh;
	}

	public void render() {
		glDisable(GL_CULL_FACE);

		glDepthFunc(GL_LEQUAL);

		texture.bind();
		shader.use();

		Camera camera = Game.getInstance().getCamera();
		shader.uploadMat4f("projectionMatrix", camera.getProjectionMatrix());
		shader.uploadMat4f("viewMatrix", new Matrix4f(camera.getViewMatrix()).m30(0).m31(0).m32(0)); // Zero translation

		mesh.render();

		glDepthFunc(GL_LESS); // Restore default depth function

		glEnable(GL_CULL_FACE);
	}
}
