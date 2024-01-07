package com.ezzenix.utilities;

public class Vector3 {
    private final float x;
    private final float y;
    private final float z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(Vector3 v) {
        return new Vector3(x + v.getX(), y + v.getY(), z + v.getZ());
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3 normalize() {
        float length = length();
        return divide(length);
    }

    public Vector3 scale(float scalar) {
        return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public Vector3 divide(float scalar) {
        return scale(1f / scalar);
    }

    public BlockPos toBlockPos(Vector3 v) {
        return new BlockPos(
                (int)Math.round(v.getX()+0.5),
                (int)Math.round(v.getY()+0.5),
                (int)Math.round(v.getZ()+0.5)
        );
    }
}