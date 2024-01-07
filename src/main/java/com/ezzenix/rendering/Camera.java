package com.ezzenix.rendering;

import com.ezzenix.utilities.Vector3;

public class Camera {
    private Vector3 position;
    private float yaw;
    private float pitch;

    public Camera() {
        position = new Vector3(0, 0, 0);
        yaw = 0;
        pitch = 0;
    }

    public Vector3 getPosition() { return this.position; }
    public float getYaw() { return this.yaw; }
    public float getPitch() { return this.pitch; }

    public void setPosition(Vector3 pos) { this.position = pos; }
    public void setYaw(float yaw) { this.yaw = yaw; }
    public void setPitch(float pitch) { this.pitch = pitch; }

    public void addPosition(Vector3 offset) { this.position = this.position.add(offset); }
    public void addYaw(float offset) { this.yaw += offset; }
    public void addPitch(float offset) { this.pitch += offset; }

    public float[] getViewMatrix() {
        float[] viewMatrix = new float[16];
        float cosPitch = (float) Math.cos(Math.toRadians(pitch));
        float sinPitch = (float) Math.sin(Math.toRadians(pitch));
        float cosYaw = (float) Math.cos(Math.toRadians(yaw));
        float sinYaw = (float) Math.sin(Math.toRadians(yaw));

        viewMatrix[0] = cosYaw;
        viewMatrix[1] = sinYaw * sinPitch;
        viewMatrix[2] = sinYaw * cosPitch;
        viewMatrix[3] = 0;

        viewMatrix[4] = 0;
        viewMatrix[5] = cosPitch;
        viewMatrix[6] = -sinPitch;
        viewMatrix[7] = 0;

        viewMatrix[8] = -sinYaw;
        viewMatrix[9] = cosYaw * sinPitch;
        viewMatrix[10] = cosYaw * cosPitch;
        viewMatrix[11] = 0;

        viewMatrix[12] = -position.getX();
        viewMatrix[13] = -position.getY();
        viewMatrix[14] = -position.getZ();
        viewMatrix[15] = 1;

        return viewMatrix;
    }
}
