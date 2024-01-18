package com.ezzenix.rendering.chunkbuilder;

import org.joml.Vector3f;

public class VoxelFace {
    public Face face;
    public byte blockId;

    public int x;
    public int y;
    public int z;

    public float ao1;
    public float ao2;
    public float ao3;
    public float ao4;

    public VoxelFace(int x, int y, int z, Face face, byte blockId) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
        this.blockId = blockId;
    }
}