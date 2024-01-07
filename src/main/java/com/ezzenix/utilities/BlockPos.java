package com.ezzenix.utilities;

public class BlockPos {
    private final int x;
    private final int y;
    private final int z;

    public BlockPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockPos add(BlockPos v) {
        return new BlockPos(x + v.getX(), y + v.getY(), z + v.getZ());
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public Vector3 toVector3(BlockPos v) {
        return new Vector3(v.getX()+0.5f, v.getY()+0.5f, v.getZ()+0.5f);
    }
}