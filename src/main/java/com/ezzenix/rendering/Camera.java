package com.ezzenix.rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private float yaw;
    private float pitch;

    public Camera() {
        position = new Vector3f(0, 0, 0);
        yaw = 0;
        pitch = 0;
    }

    public Vector3f getPosition() { return this.position; }
    public float getYaw() { return this.yaw; }
    public float getPitch() { return this.pitch; }

    public void setPosition(Vector3f pos) { this.position = pos; }
    public void setYaw(float yaw) {
        while (yaw > 180) yaw -= 360;
        while (yaw < 180) yaw += 360;
        this.yaw = (yaw + 180.0f) % 360.0f - 180.0f;
    }
    public void setPitch(float pitch) {
        float min = -90;
        float max = 90;
        this.pitch = Math.max(min, Math.min(max, pitch));
    }

    public void addPosition(Vector3f offset) { this.position = this.position.add(offset); }
    public void addYaw(float offset) { this.setYaw(this.yaw + offset); }
    public void addPitch(float offset) { this.setPitch(this.pitch + offset); }

    public Vector3f getLookVector() {
        double x = Math.cos(pitch) * Math.sin(yaw);
        double y = Math.sin(pitch);
        double z = -Math.cos(pitch) * Math.cos(yaw);

        double magnitude = Math.sqrt(x * x + y * y + z * z);
        x /= magnitude;
        y /= magnitude;
        z /= magnitude;

        return new Vector3f((float)x, (float)y, (float)z);
    }

    public Matrix4f getProjectionMatrix() {
        // Example parameters
        float fov = 70.0f;
        float aspectRatio = 16.0f / 9.0f;
        float near = 0.1f;
        float far = 200.0f;

        // Create a perspective projection matrix
        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.perspective(fov, aspectRatio, near, far);

        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().setLookAt(
                position,
                new Vector3f(
                        (float) (position.x + Math.cos(Math.toRadians(yaw+180)) * Math.cos(Math.toRadians(pitch))),
                        (float) (position.y + Math.sin(Math.toRadians(pitch))),
                        (float) (position.z + Math.sin(Math.toRadians(yaw+180)) * Math.cos(Math.toRadians(pitch)))
                ),
                new Vector3f(0.0f, 1.0f, 0.0f)
        );
    }
}
