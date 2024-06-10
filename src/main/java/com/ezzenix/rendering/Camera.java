package com.ezzenix.rendering;

import com.ezzenix.Game;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.game.physics.Physics;
import com.ezzenix.game.physics.RaycastResult;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera {
    private final Entity entity;
    public boolean thirdPerson = false;

    private Matrix4f projectionMatrix;

    public Camera() {
        this.entity = Game.getInstance().getPlayer();

        // Initialize projection matrix
        float fov = 70.0f;
        float aspectRatio = 16.0f / 9.0f;
        float near = 0.1f;
        float far = 2000.0f;
        projectionMatrix = new Matrix4f().perspective(fov, aspectRatio, near, far);
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Vector3f getLookVector() {
        Vector3f lookVector = new Vector3f(0.0f, 0.0f, -1.0f);
        Vector3f upVector = new Vector3f(0.0f, 1.0f, 0.0f);
        Quaternionf orientation = new Quaternionf()
                .rotateAxis((float) Math.toRadians(entity.getYaw()+180), upVector)
                .rotateAxis((float) Math.toRadians(entity.getPitch()), new Vector3f(1.0f, 0.0f, 0.0f));
        lookVector.set(0.0f, 0.0f, -1.0f).rotate(orientation);
        upVector.set(0.0f, 1.0f, 0.0f);
        Vector3f rightVector = new Vector3f();
        lookVector.cross(upVector, rightVector).normalize();
        return lookVector;
    }

    public Matrix4f getViewMatrix() {
        float yaw = ((entity.getYaw() + 180) + 90) % 360;
        float pitch = entity.getPitch();
        Vector3f position = getPosition();

        if (thirdPerson) {
            Vector3f lookVector = getLookVector();
            position.add(lookVector.mul(-8));
        }

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

    public Vector3f getPosition() {
        return new Vector3f(entity.getPosition()).add(0, entity.eyeHeight, 0);
    }

    public RaycastResult raycast(float maxDistance) {
        return Physics.raycast(this.entity.getWorld(), this.getPosition(), this.getLookVector().mul(maxDistance));
    }

    public Matrix4f getViewProjectionMatrix() {
        return new Matrix4f().set(getProjectionMatrix()).mul(getViewMatrix());
    }
}
