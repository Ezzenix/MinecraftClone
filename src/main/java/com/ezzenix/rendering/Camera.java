package com.ezzenix.rendering;

import com.ezzenix.Game;
import com.ezzenix.game.entities.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Entity entity;

    public Camera() {
        this.entity = Game.getInstance().getPlayer();
    }

    public Entity getEntity() {
        return entity;
    }

    public Matrix4f getProjectionMatrix() {
        // Example parameters
        float fov = 70.0f;
        float aspectRatio = 16.0f / 9.0f;
        float near = 0.1f;
        float far = 2000.0f;

        // Create a perspective projection matrix
        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.perspective(fov, aspectRatio, near, far);

        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        float yaw = ((entity.getYaw() + 180) + 90) % 360;
        float pitch = entity.getPitch();
        Vector3f position = new Vector3f(entity.getPosition()).add(0, entity.eyeHeight, 0);

        return new Matrix4f().setLookAt(
                position,
                new Vector3f(
                        (float) (position.x + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))),
                        (float) (position.y + Math.sin(Math.toRadians(entity.getPitch()))),
                        (float) (position.z - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)))
                ),
                new Vector3f(0.0f, 1.0f, 0.0f)
        );
    }

    public Matrix4f getViewProjectionMatrix() {
        return new Matrix4f().set(getProjectionMatrix()).mul(getViewMatrix());
    }
}
