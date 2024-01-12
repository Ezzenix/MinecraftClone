package com.ezzenix.rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private float yaw;
    private float pitch;

    public Camera() {
        position = new Vector3f(0, 40, 0);
        yaw = 0;
        pitch = 0;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPosition(Vector3f pos) {
        this.position = pos;
    }

    public void setYaw(float yaw) {
        while (yaw > 180) yaw -= 360;
        while (yaw < 180) yaw += 360;
        this.yaw = (yaw + 180.0f) % 360.0f - 180.0f;
    }

    public void setPitch(float pitch) {
        float min = -89.99f;
        float max = 89.99f;
        this.pitch = Math.max(min, Math.min(max, pitch));
    }

    public void addPosition(Vector3f offset) {
        this.position = this.position.add(offset);
    }

    public void addYaw(float offset) {
        this.setYaw(this.yaw + offset);
    }

    public void addPitch(float offset) {
        this.setPitch(this.pitch + offset);
    }

    public Matrix4f getProjectionMatrix() {
        // Example parameters
        float fov = 70.0f;
        float aspectRatio = 16.0f / 9.0f;
        float near = 0.1f;
        float far = 500.0f;

        // Create a perspective projection matrix
        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.perspective(fov, aspectRatio, near, far);

        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        float fakeYaw = ((yaw + 180)) % 360;

        return new Matrix4f().setLookAt(
                position,
                new Vector3f(
                        (float) (position.x + Math.cos(Math.toRadians(fakeYaw)) * Math.cos(Math.toRadians(pitch))),
                        (float) (position.y + Math.sin(Math.toRadians(pitch))),
                        (float) (position.z + Math.sin(Math.toRadians(fakeYaw)) * Math.cos(Math.toRadians(pitch)))
                ),
                new Vector3f(0.0f, 1.0f, 0.0f)
        );
    }
}
