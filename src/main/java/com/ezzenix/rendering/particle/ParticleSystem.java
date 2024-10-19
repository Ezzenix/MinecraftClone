package com.ezzenix.rendering.particle;

import com.ezzenix.Client;
import com.ezzenix.math.BlockPos;
import com.ezzenix.rendering.Camera;
import com.ezzenix.rendering.util.VertexBuffer;
import com.ezzenix.rendering.util.VertexFormat;
import com.ezzenix.engine.opengl.Shader;
import org.joml.Math;
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
		shader.setUniforms();

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

	public static void createBlockBreakParticles(BlockPos blockPos) {
		float minX = blockPos.x;
		float maxX = blockPos.x + 1;
		float minY = blockPos.y;
		float maxY = blockPos.y + 1;
		float minZ = blockPos.z;
		float maxZ = blockPos.z + 1;

		float step = 1f / 4f;

		float centerX = minX + 0.5f;
		float centerZ = minZ + 0.5f;

		for (float x = minX + step; x < maxX; x += step) {
			for (float y = minY + step; y < maxY; y += step) {
				for (float z = minZ + step; z < maxZ; z += step) {
					float vx = x - centerX;
					float vy = 2f;
					float vz = z - centerZ;

					float spread = 0.25f;
					float px = (x + ((float) Math.random() * 2 - 1) * spread);
					float py = (y + ((float) Math.random() * 2 - 1) * spread);
					float pz = (z + ((float) Math.random() * 2 - 1) * spread);

					new BlockBreakParticle(px, py, pz, vx, vy, vz);
				}
			}
		}
	}
}
