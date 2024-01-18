package com.ezzenix.rendering.chunkbuilder;

import com.ezzenix.game.chunk.Chunk;
import com.ezzenix.game.blocks.BlockType;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class GreedyShape {
    private Chunk chunk;
    public Face face;
    public BlockType blockType;
    public List<Vector3i> voxels;

    public int minX, maxX;
    public int minY, maxY;
    public int minZ, maxZ;


    public GreedyShape(Chunk chunk, Face face, Vector3i initialVoxel) {
        this.voxels = new ArrayList<>();
        this.blockType = chunk.getBlockTypeAt(initialVoxel);
        voxels.add(initialVoxel);
        this.chunk = chunk;
        this.face = face;
        minX = initialVoxel.x;
        maxX = initialVoxel.x;
        minY = initialVoxel.y;
        maxY = initialVoxel.y;
        minZ = initialVoxel.z;
        maxZ = initialVoxel.z;
    }

    public boolean hasVoxel(Vector3i voxel) {
        return this.voxels.contains(voxel);
    }

    private boolean canExpandTo(Vector3i voxel) {
        if (voxel.x < 0 || voxel.x > Chunk.CHUNK_SIZE-1) return false;
        if (voxel.y < 0 || voxel.y > Chunk.CHUNK_SIZE-1) return false;
        if (voxel.z < 0 || voxel.z > Chunk.CHUNK_SIZE-1) return false;
        BlockType type = chunk.getBlockTypeAt(voxel);
        return type == this.blockType;
    }

    private boolean isAtEdgeInDirection(Vector3i direction, Vector3i voxel) {
        if (direction.x < 0) return voxel.x == minX;
        if (direction.x > 0) return voxel.x == maxX;
        if (direction.y < 0) return voxel.y == minY;
        if (direction.y > 0) return voxel.y == maxY;
        if (direction.z < 0) return voxel.z == minZ;
        if (direction.z > 0) return voxel.z == maxZ;
        return false;
    }

    public boolean expand(Vector3i direction, List<Vector3i> possibleVoxels) {
        List<Vector3i> newVoxels = new ArrayList<>();
        boolean expanded = false;

        boolean canExpand = true;
        for (Vector3i voxel : this.voxels) {
            if (!isAtEdgeInDirection(direction, voxel)) continue;
            Vector3i v = new Vector3i(voxel.x + direction.x, voxel.y + direction.y, voxel.z+direction.z);
            if (possibleVoxels.contains(v) && canExpandTo(v)) {
            } else {
                canExpand = false;
                break;
            }
        }
        if (canExpand) {
            for (Vector3i voxel : this.voxels) {
                if (!isAtEdgeInDirection(direction, voxel)) continue;
                Vector3i v = new Vector3i(voxel.x + direction.x, voxel.y + direction.y, voxel.z+direction.z);
                if (!hasVoxel(v)) {
                    newVoxels.add(v);
                    //System.out.println("shape with initial at " + voxels.get(0).toString(new DecimalFormat("#")) + " added " + v.toString(new DecimalFormat("#")));
                    expanded = true;
                }
            }
            for (Vector3i voxel : newVoxels) {
                possibleVoxels.remove(voxel);
                this.voxels.add(voxel);

                minX = Math.min(minX, voxel.x);
                maxX = Math.max(maxX, voxel.x);
                minY = Math.min(minY, voxel.y);
                maxY = Math.max(maxY, voxel.y);
                minZ = Math.min(minZ, voxel.z);
                maxZ = Math.max(maxZ, voxel.z);
            }
        }

        return expanded;
    }
}
