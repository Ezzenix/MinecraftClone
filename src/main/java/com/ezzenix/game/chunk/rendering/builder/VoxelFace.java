package com.ezzenix.game.chunk.rendering.builder;

import com.ezzenix.engine.utils.BlockPos;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.chunk.Chunk;
import org.joml.Vector3i;

public class VoxelFace {
    public Face face;
    public byte blockId;

    public Vector3i position;

    public float ao1 = 0;
    public float ao2 = 0;
    public float ao3 = 0;
    public float ao4 = 0;

    public VoxelFace(Vector3i position, Face face, byte blockId) {
        this.position = position;
        this.face = face;
        this.blockId = blockId;
    }

    private int isBlockAt(Chunk chunk, Face face, Vector3i offset) {
        Vector3i realOffset = offsetFace(face, offset);

        BlockPos worldPos = chunk.toWorldPos(new Vector3i(this.position).add(realOffset));
        BlockType blockType = chunk.getWorld().getBlockTypeAt(worldPos);

        return blockType == BlockType.AIR || !blockType.isSolid() ? 0 : 1;
    }

    private Vector3i offsetFace(Face face, Vector3i offset) {
        return switch (face) {
            case TOP -> offset;
            case BOTTOM -> new Vector3i(-offset.x, -offset.y, -offset.z);
            case FRONT -> new Vector3i(offset.x, offset.z, -offset.y);
            case BACK -> new Vector3i(-offset.x, -offset.z, offset.y);
            case RIGHT -> new Vector3i(offset.y, -offset.x, offset.z);
            case LEFT -> new Vector3i(-offset.y, offset.x, -offset.z);
        };
    }

    private float solveAO(int side1, int side2, int corner) {
        if (side1 == 1 && side2 == 1) {
            return 1;
        }
        return 1-((float) (3 - (side1 + side2 + corner)) /3);
    }

    public void calculateAO(Chunk chunk) {
        int W = isBlockAt(chunk, this.face, new Vector3i(-1, 1, 0));
        int NW = isBlockAt(chunk, this.face, new Vector3i(-1, 1, -1));
        int N = isBlockAt(chunk, this.face, new Vector3i(0, 1, -1));
        int NE = isBlockAt(chunk, this.face, new Vector3i(1, 1, -1));
        int E = isBlockAt(chunk, this.face, new Vector3i(1, 1, 0));
        int SE = isBlockAt(chunk, this.face, new Vector3i(1, 1, 1));
        int S = isBlockAt(chunk, this.face, new Vector3i(0, 1, 1));
        int SW = isBlockAt(chunk, this.face, new Vector3i(-1, 1, 1));

        float ao1 = solveAO(W, N, NW);
        float ao2 = solveAO(W, S, SW);
        float ao3 = solveAO(E, S, SE);
        float ao4 = solveAO(E, N, NE);

        switch (face) {
            case TOP:
                this.ao1 = ao1;
                this.ao2 = ao2;
                this.ao3 = ao3;
                this.ao4 = ao4;
                break;
            case BOTTOM:
                this.ao1 = ao4;
                this.ao2 = ao3;
                this.ao3 = ao2;
                this.ao4 = ao1;
                break;
            case FRONT:
                this.ao1 = ao3;
                this.ao2 = ao4;
                this.ao3 = ao1;
                this.ao4 = ao2;
                break;
            case BACK:
                this.ao1 = ao4;
                this.ao2 = ao3;
                this.ao3 = ao2;
                this.ao4 = ao1;
                break;
            case RIGHT:
                this.ao1 = ao2;
                this.ao2 = ao3;
                this.ao3 = ao4;
                this.ao4 = ao1;
                break;
            case LEFT:
                this.ao1 = ao3;
                this.ao2 = ao2;
                this.ao3 = ao1;
                this.ao4 = ao4;
                break;
        }
    }
}