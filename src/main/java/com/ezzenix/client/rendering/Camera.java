package com.ezzenix.client.rendering;

import com.ezzenix.Game;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.engine.opengl.Window;
import com.ezzenix.game.entities.Entity;
import org.joml.*;
import org.joml.Math;

public class Camera {
	private final Entity entity;
	public boolean thirdPerson = false;

	private Matrix4f projectionMatrix;

	private float previousVelocityFactor = 0;

	public Camera() {
		this.entity = Game.getInstance().getPlayer();

		// Initialize projection matrix
		updateProjectionMatrix();
		Game.getInstance().getWindow().sizeChanged.connect(this::updateProjectionMatrix);
	}

	public void updateProjectionMatrix() {
		Window window = Game.getInstance().getWindow();

		float fov = Math.toRadians(75);
		float aspectRatio = (float) window.getWidth() / window.getHeight();
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

	public Vector3f getViewBobbingTranslation() {
		float SPEED = 5.5f;
		float AMOUNT = 0.065f;

		float targetVelocityFactor = this.entity.isGrounded ? Math.min(1, (this.entity.getHorizontalVelocity().length() / 2f)) : 0;
		float velocityFactor = Math.lerp(previousVelocityFactor, targetVelocityFactor, 10 * Scheduler.getDeltaTime());
		previousVelocityFactor = velocityFactor;

		float t = Scheduler.getClock();
		float bobbingOffsetX = velocityFactor * (Math.sin(t * SPEED) * AMOUNT);
		float bobbingOffsetY = velocityFactor * (Math.cos(t * SPEED * 2) * AMOUNT) * 0.5f;

		return new Vector3f(
			(bobbingOffsetX * Math.cos(Math.toRadians(entity.getYaw()))),
			bobbingOffsetY,
			(-bobbingOffsetX * Math.sin(Math.toRadians(entity.getYaw())))
		);
	}

	public Matrix4f getViewMatrix() {
		float yaw = ((entity.getYaw() + 180) + 90) % 360;
		float pitch = Math.clamp(-89f, 89f, entity.getPitch());
		Vector3f position = getPosition();

		if (thirdPerson) {
			Vector3f lookVector = getLookVector();
			position.add(lookVector.mul(-8));
		}

		Matrix4f viewMatrix = new Matrix4f().setLookAt(
			position,
			new Vector3f(
				position.x + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
				position.y + Math.sin(Math.toRadians(pitch)),
				position.z - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
			),
			new Vector3f(0.0f, 1.0f, 0.0f)
		);

		//if (InputHandler.isMoving) {
		viewMatrix.translate(getViewBobbingTranslation());
		//}

		return viewMatrix;
	}

	public Vector3f getPosition() {
		return new Vector3f(entity.getPosition()).add(0, entity.getEyeHeight(), 0);
	}

	public Matrix4f getViewProjectionMatrix() {
		return new Matrix4f().set(getProjectionMatrix()).mul(getViewMatrix());
	}
}
