package com.ezzenix.rendering.particle;

import com.ezzenix.engine.Scheduler;
import com.ezzenix.rendering.util.BufferBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Particle {
	private float posX;
	private float posY;
	private float posZ;

	private float velocityX;
	private float velocityY;
	private float velocityZ;

	int color;
	float size;
	float lifetime;
	float spawnedAt;

	public Particle(float x, float y, float z, float vx, float vy, float vz, float size, int color, float lifetime) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;

		this.velocityX = vx;
		this.velocityY = vy;
		this.velocityZ = vz;

		this.color = color;
		this.size = size;
		this.lifetime = lifetime;
		this.spawnedAt = Scheduler.getClock();

		ParticleSystem.particles.add(this);
	}

	public void update() {
		if (Scheduler.getClock() - this.spawnedAt > this.lifetime) {
			ParticleSystem.particles.remove(this);
			return;
		}

		float deltaTime = Scheduler.getDeltaTime();

		this.velocityY -= 7f * deltaTime;

		this.posX += this.velocityX * deltaTime;
		this.posY += this.velocityY * deltaTime;
		this.posZ += this.velocityZ * deltaTime;
	}

	public void draw(BufferBuilder builder, Vector3f direction) {
		Vector3f position = new Vector3f(this.posX, this.posY, this.posZ);

		Matrix4f lookAtMatrix = new Matrix4f().lookAt(
			position,
			new Vector3f(position).add(direction), // add small number otherwise result is NaN
			new Vector3f(0, 1, 0)
		).invert();

		Matrix4f transformationMatrix = new Matrix4f()
			.mul(lookAtMatrix);

		float halfSize = size / 2;
		Vector3f[] vertices = {
			new Vector3f(-halfSize, -halfSize, 0),
			new Vector3f(halfSize, -halfSize, 0),
			new Vector3f(halfSize, halfSize, 0),
			new Vector3f(-halfSize, halfSize, 0)
		};

		for (Vector3f vertex : vertices) {
			transformationMatrix.transformPosition(vertex);
		}

		int[] indices = {0, 1, 2, 2, 3, 0};

		for (int index : indices) {
			Vector3f v = vertices[index];
			builder.vertex(v.x, v.y, v.z).color(this.color).next();
		}
	}
}
