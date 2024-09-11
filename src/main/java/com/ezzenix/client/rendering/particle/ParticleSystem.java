package com.ezzenix.client.rendering.particle;

import com.ezzenix.client.Client;
import com.ezzenix.client.rendering.Camera;
import com.ezzenix.client.rendering.util.VertexBuffer;
import com.ezzenix.client.rendering.util.VertexFormat;
import com.ezzenix.engine.opengl.Shader;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class ParticleSystem {
	public static final List<Particle> particles = new ArrayList<>();

	private static final Shader shader = new Shader("particle");
	private static final VertexBuffer vertexBuffer = new VertexBuffer(shader, new VertexFormat(GL_FLOAT, 3, GL_INT, 1), VertexBuffer.Usage.DYNAMIC);

	public static void render() {
		Camera camera = Client.getCamera();
		Vector3f direction = camera.getLookVector().mul(-1);

		glDisable(GL_CULL_FACE);

		shader.bind();
		shader.setUniform("projectionMatrix", camera.getProjectionMatrix());
		shader.setUniform("viewMatrix", camera.getViewMatrix());

		for (Particle particle : particles.stream().toList()) {
			particle.update();
		}
		for (Particle particle : particles) {
			particle.render(vertexBuffer, direction);
		}

		vertexBuffer.upload();
		vertexBuffer.draw();

		glEnable(GL_CULL_FACE);
	}
}
