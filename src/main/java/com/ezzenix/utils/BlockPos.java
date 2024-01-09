package com.ezzenix.utils;

import org.joml.Vector3f;

public class BlockPos {
    public final int x;
    public final int y;
    public final int z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos add(BlockPos v) {
        return new BlockPos(x + v.x, y + v.y, z + v.z);
    }

    public Vector3f toVector3f(BlockPos v) {
        return new Vector3f(v.x+0.5f, v.y+0.5f, v.z+0.5f);
    }

    public BlockPos fromVector3f(Vector3f vec) {
        return new BlockPos(
                (int)Math.round(vec.x+0.5),
                (int)Math.round(vec.y+0.5),
                (int)Math.round(vec.z+0.5)
        );
    }

    public String toString() {
        return "X: "+this.x + " Y: " + this.x + " Z: " + this.z;
    }

    public boolean equals(BlockPos v) {
        return (this.x == v.x) && (this.y == v.y) && (this.z == v.z);
    }
}