package com.ezzenix.game.chunk.rendering.builder;

import com.ezzenix.engine.utils.BlockPos;
import com.ezzenix.game.blocks.BlockRegistry;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.chunk.Chunk;
import com.ezzenix.engine.opengl.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.system.MemoryUtil.memFree;

public class ChunkBuilder {
    static Vector3i getFaceNormal(Face face) {
        return switch (face) {
            case TOP -> new Vector3i(0, 1, 0);
            case BOTTOM -> new Vector3i(0, -1, 0);
            case RIGHT -> new Vector3i(1, 0, 0);
            case LEFT -> new Vector3i(-1, 0, 0);
            case FRONT -> new Vector3i(0, 0, -1);
            case BACK -> new Vector3i(0, 0, 1);
        };
    }

    public static Vector2f[] getBlockTextureUV(BlockType blockType, Face face) {
        if (face == Face.TOP) return blockType.textureUVTop;
        if (face == Face.BOTTOM) return blockType.textureUVBottom;
        return blockType.textureUVSides;
    }

    public static Mesh createMesh(Chunk chunk, boolean transparentBlocksOnly) {
        // Flowers
        /*
        for (int i = 0; i < Chunk.CHUNK_SIZE_CUBED; i++) {
            Vector3i localPosition = chunk.getLocalPositionFromIndex(i);
            BlockType blockType = chunk.getBlockTypeAt(localPosition);
            if (blockType == null || !blockType.isFlower()) continue;
            Vector2f[] textureUV = getBlockTextureUV(blockType, Face.TOP);
            Vector3f voxelPos = new Vector3f(localPosition);

            for (int k = 0; k < 4; k++) {
                textureUV[k] = new Vector2f(textureUV[k]).add(1, 1);
            }

            addVertex(vertexList, new Vector3f(voxelPos).add(0, 1, 0), textureUV[0], 0);
            addVertex(vertexList, new Vector3f(voxelPos).add(0, 0, 0), textureUV[1], 0);
            addVertex(vertexList, new Vector3f(voxelPos).add(1, 0, 1), textureUV[2], 0);

            addVertex(vertexList, new Vector3f(voxelPos).add(1, 0, 1), textureUV[2], 0);
            addVertex(vertexList, new Vector3f(voxelPos).add(1, 1, 1), textureUV[3], 0);
            addVertex(vertexList, new Vector3f(voxelPos).add(0, 1, 0), textureUV[0], 0);
        }
        */

        //long startTime = System.currentTimeMillis();
        List<Float> vertexList = new ArrayList<>();

        // Blocks
        for (GreedyShape shape : generateShapes(chunk, transparentBlocksOnly)) {
            BlockType blockType = BlockRegistry.getBlockFromId(shape.initialVoxelFace.blockId);

            Vector2f[] textureUV = getBlockTextureUV(blockType, shape.initialVoxelFace.face);

            // Voxel coordinates are at the bottom corner of the blocks, so offset max by 1 to cover the last blocks
            shape.maxX += 1;
            shape.maxY += 1;
            shape.maxZ += 1;

            Vector3f vert1 = new Vector3f();
            Vector3f vert2 = new Vector3f();
            Vector3f vert3 = new Vector3f();
            Vector3f vert4 = new Vector3f();

            Vector2f shapeSize = new Vector2f();

            // NOTE: Voxel coordinates are at the bottom corner of the blocks
            switch (shape.initialVoxelFace.face) {
                case TOP: {
                    vert1.set(shape.minX, shape.maxY, shape.minZ);
                    vert2.set(shape.minX, shape.maxY, shape.maxZ);
                    vert3.set(shape.maxX, shape.maxY, shape.maxZ);
                    vert4.set(shape.maxX, shape.maxY, shape.minZ);
                    shapeSize.set(shape.maxX - shape.minX, shape.maxZ - shape.minZ);
                    break;
                }
                case BOTTOM: {
                    vert1.set(shape.minX, shape.minY, shape.maxZ);
                    vert2.set(shape.minX, shape.minY, shape.minZ);
                    vert3.set(shape.maxX, shape.minY, shape.minZ);
                    vert4.set(shape.maxX, shape.minY, shape.maxZ);
                    shapeSize.set(shape.maxX - shape.minX, shape.maxZ - shape.minZ);
                    break;
                }
                case FRONT: {
                    vert1.set(shape.maxX, shape.maxY, shape.minZ);
                    vert2.set(shape.maxX, shape.minY, shape.minZ);
                    vert3.set(shape.minX, shape.minY, shape.minZ);
                    vert4.set(shape.minX, shape.maxY, shape.minZ);
                    shapeSize.set(shape.maxX - shape.minX, shape.maxY - shape.minY);
                    break;
                }
                case BACK: {
                    vert1.set(shape.minX, shape.maxY, shape.maxZ);
                    vert2.set(shape.minX, shape.minY, shape.maxZ);
                    vert3.set(shape.maxX, shape.minY, shape.maxZ);
                    vert4.set(shape.maxX, shape.maxY, shape.maxZ);
                    shapeSize.set(shape.maxX - shape.minX, shape.maxY - shape.minY);
                    break;
                }
                case LEFT: {
                    vert1.set(shape.minX, shape.maxY, shape.minZ);
                    vert2.set(shape.minX, shape.minY, shape.minZ);
                    vert3.set(shape.minX, shape.minY, shape.maxZ);
                    vert4.set(shape.minX, shape.maxY, shape.maxZ);
                    shapeSize.set(shape.maxZ - shape.minZ, shape.maxY - shape.minY);
                    break;
                }
                case RIGHT: {
                    vert1.set(shape.maxX, shape.maxY, shape.maxZ);
                    vert2.set(shape.maxX, shape.minY, shape.maxZ);
                    vert3.set(shape.maxX, shape.minY, shape.minZ);
                    vert4.set(shape.maxX, shape.maxY, shape.minZ);
                    shapeSize.set(shape.maxZ - shape.minZ, shape.maxY - shape.minY);
                    break;
                }
            }

            addVertex(vertexList, vert1, new Vector2f(textureUV[0]).add(shapeSize.x, shapeSize.y), shape.initialVoxelFace.ao1);
            addVertex(vertexList, vert2, new Vector2f(textureUV[1]).add(shapeSize.x, shapeSize.y), shape.initialVoxelFace.ao2);
            addVertex(vertexList, vert3, new Vector2f(textureUV[2]).add(shapeSize.x, shapeSize.y), shape.initialVoxelFace.ao3);

            addVertex(vertexList, vert3, new Vector2f(textureUV[2]).add(shapeSize.x, shapeSize.y), shape.initialVoxelFace.ao3);
            addVertex(vertexList, vert4, new Vector2f(textureUV[3]).add(shapeSize.x, shapeSize.y), shape.initialVoxelFace.ao4);
            addVertex(vertexList, vert1, new Vector2f(textureUV[0]).add(shapeSize.x, shapeSize.y), shape.initialVoxelFace.ao1);
        }


        FloatBuffer buffer = Mesh.convertToBuffer(vertexList);

        Mesh mesh = new Mesh(buffer, vertexList.size() / 6);

        int stride = 6 * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 1, GL_FLOAT, false, stride, 5 * Float.BYTES);
        glEnableVertexAttribArray(2);

        mesh.unbind();

        //System.out.println("Chunk mesh built in " + (System.currentTimeMillis() - startTime) + "ms");
        return mesh;
    }

    private static void addVertex(List<Float> vertexList, Vector3f pos, Vector2f uv, float aoFactor) {
        vertexList.add(pos.x);
        vertexList.add(pos.y);
        vertexList.add(pos.z);
        vertexList.add(uv.x);
        vertexList.add(uv.y);
        vertexList.add(aoFactor);
    }

    private static boolean shouldRenderFace(Chunk chunk, BlockType blockType, BlockPos blockPos, Face face) {
        if (blockType == BlockType.AIR) return false;
        Vector3i faceNormal = getFaceNormal(face);
        BlockPos neighborPos = blockPos.add(faceNormal.x, faceNormal.y, faceNormal.z);
        BlockType neighborType = chunk.getWorld().getBlockTypeAt(neighborPos);
        if (neighborType == null) neighborType = BlockType.AIR;

        if (blockType == neighborType) return false; // do not render face if same block

        return neighborType == BlockType.AIR || !neighborType.isSolid();
    }

    private static HashMap<Face, List<VoxelFace>> generateVoxelFaces(Chunk chunk, boolean transparentBlocksOnly) {
        HashMap<Face, List<VoxelFace>> voxelFaces = new HashMap<>();
        for (Face face : Face.values()) {
            voxelFaces.put(face, new ArrayList<>());
        }

        for (int i = 0; i < Chunk.CHUNK_SIZE_CUBED; i++) {
            Vector3i localPosition = chunk.getLocalPositionFromIndex(i);
            BlockType type = chunk.getBlockTypeAt(localPosition);
            if (type == null || type == BlockType.AIR || type.isFlower()) continue;

            for (Face face : Face.values()) {
                if ((type.isTransparent() && transparentBlocksOnly) || (!type.isTransparent() && !transparentBlocksOnly)) {
                    if (shouldRenderFace(chunk, type, chunk.toWorldPos(localPosition), face)) {
                        VoxelFace voxelFace = new VoxelFace(localPosition, face, type.getId());
                        voxelFace.calculateAO(chunk);
                        voxelFaces.get(face).add(voxelFace);
                    }
                }
            }
        }
        return voxelFaces;
    }

    public static List<GreedyShape> generateShapes(Chunk chunk, boolean transparentBlocksOnly) {
        HashMap<Face, List<VoxelFace>> voxelFaces = generateVoxelFaces(chunk, transparentBlocksOnly);

        List<GreedyShape> shapes = new ArrayList<>();

        for (Face face : Face.values()) {
            List<VoxelFace> possibleVoxels = voxelFaces.get(face);
            shapes.addAll(GreedyShape.createShapesFrom(chunk, possibleVoxels));
        }

        return shapes;
    }
}
