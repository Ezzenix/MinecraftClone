package com.ezzenix.rendering;

import com.ezzenix.Game;
import com.ezzenix.engine.core.Util;
import com.ezzenix.engine.physics.Raycast;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.engine.physics.Physics;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera {
	private final Entity entity;
	public boolean thirdPerson = false;

	private final Matrix4f projectionMatrix;

	public Camera() {
		this.entity = Game.getInstance().getPlayer();

		// Initialize projection matrix
		float fov = Math.toRadians(75);
		float aspectRatio = 16.0f / 9.0f;
		float near = 0.1f;
		float far = 2000.0f;
		projectionMatrix = new Matrix4f().perspective(fov, aspectRatio, near, far);
	}

	public Matrix4f getProjectionMatrix() {
		return this.projectionMatrix;
	}

	public Vector3f getLookVector() {
		return this.entity.getLookVector();
	}

	public Matrix4f getViewMatrix() {
		float yaw = ((entity.getYaw() + 180) + 90) % 360;
		float pitch = Math.clamp(-89f, 89f, entity.getPitch());
		Vector3f position = getPosition();

		if (thirdPerson) {
			Vector3f lookVector = getLookVector();
			position.add(lookVector.mul(-8));
		}

		return new Matrix4f().setLookAt(
			position,
			new Vector3f(
				position.x + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
				position.y + Math.sin(Math.toRadians(pitch)),
				position.z - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
			),
			new Vector3f(0.0f, 1.0f, 0.0f)
		);
	}

	public Vector3f getPosition() {
		return new Vector3f(entity.getPosition()).add(0, entity.eyeHeight, 0);
	}

	public Matrix4f getViewProjectionMatrix() {
		return new Matrix4f().set(getProjectionMatrix()).mul(getViewMatrix());
	}
}
