package com.ezzenix.rendering.chunkbuilder;

import com.ezzenix.engine.utils.BlockPos;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.chunk.Chunk;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.text.DecimalFormat;

import static com.ezzenix.rendering.chunkbuilder.ChunkBuilder.getFaceNormal;

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

        //this.ao1 = Math.random() > 0.5 ? 1 : 0;
    }

    private boolean isBlockAt(Chunk chunk, Vector3i position) {
        BlockPos worldPos = chunk.toWorldPos(position);
        BlockType blockType = chunk.getWorld().getBlockTypeAt(worldPos);
        return blockType != null && blockType != BlockType.AIR;
    }

    private Vector3i getVectorFromNormalAndOffset(Vector3i normal, Vector3i offset) {
        return new Vector3i(normal).sub(offset);
    }

    private boolean isBlockAt(Chunk chunk, Vector3i normal, Vector3i offset) {
        return isBlockAt(chunk, getVectorFromNormalAndOffset(normal, offset));
    }

    public void calculateAO(Chunk chunk) {
        Vector3i faceNormal = getFaceNormal(this.face);
        if (this.face != Face.TOP) return;

        boolean leftUp = isBlockAt(chunk, faceNormal, new Vector3i(-1, 1, 0));
        boolean frontUp = isBlockAt(chunk, faceNormal, new Vector3i(0, 1, -1));
        boolean rightUp = isBlockAt(chunk, faceNormal, new Vector3i(1, 1, 0));
        boolean backUp = isBlockAt(chunk, faceNormal, new Vector3i(0, 1, 1));

        this.ao1 = (leftUp || frontUp)  ? 1 : 0;
        this.ao2 = (leftUp || backUp)  ? 1 : 0;
        this.ao3 = (rightUp || backUp)  ? 1 : 0;
        this.ao4 = (rightUp || frontUp)  ? 1 : 0;



    }
}