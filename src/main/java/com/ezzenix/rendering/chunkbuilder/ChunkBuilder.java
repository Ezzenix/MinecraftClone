package com.ezzenix.rendering.chunkbuilder;

import com.ezzenix.engine.utils.BlockPos;
import com.ezzenix.game.Chunk;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.rendering.Mesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

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

    public static Mesh createMesh(Chunk chunk, boolean waterOnly) {
        //long startTime = System.currentTimeMillis();

        List<GreedyShape> shapes = generateShapes(chunk);
        int vertexCount = shapes.size() * 6;
        FloatBuffer buffer = createFloatBuffer(vertexCount * 5);

        for (GreedyShape shape : shapes) {
            Vector2f[] textureUV = getBlockTextureUV(shape.blockType, shape.face);

            // Voxel coordinates are at the bottom corner of the blocks, so offset max by 1 to cover the last blocks
            shape.maxX += 1;
            shape.maxY += 1;
            shape.maxZ += 1;

            Vector3f vert1 = null;
            Vector3f vert2 = null;
            Vector3f vert3 = null;
            Vector3f vert4 = null;

            Vector2f shapeSize = new Vector2f();

            // NOTE: Voxel coordinates are at the bottom corner of the blocks
            switch (shape.face) {
                case TOP: {
                    vert1 = new Vector3f(shape.minX, shape.maxY, shape.minZ);
                    vert2 = new Vector3f(shape.minX, shape.maxY, shape.maxZ);
                    vert3 = new Vector3f(shape.maxX, shape.maxY, shape.maxZ);
                    vert4 = new Vector3f(shape.maxX, shape.maxY, shape.minZ);
                    shapeSize.add(new Vector2f(shape.maxX - shape.minX, shape.maxZ - shape.minZ));
                    break;
                }
                case BOTTOM: {
                    vert1 = new Vector3f(shape.minX, shape.minY, shape.maxZ);
                    vert2 = new Vector3f(shape.minX, shape.minY, shape.minZ);
                    vert3 = new Vector3f(shape.maxX, shape.minY, shape.minZ);
                    vert4 = new Vector3f(shape.maxX, shape.minY, shape.maxZ);
                    shapeSize.add(new Vector2f(shape.maxX - shape.minX, shape.maxZ - shape.minZ));
                    break;
                }
                case FRONT: {
                    vert1 = new Vector3f(shape.maxX, shape.maxY, shape.minZ);
                    vert2 = new Vector3f(shape.maxX, shape.minY, shape.minZ);
                    vert3 = new Vector3f(shape.minX, shape.minY, shape.minZ);
                    vert4 = new Vector3f(shape.minX, shape.maxY, shape.minZ);
                    shapeSize.add(new Vector2f(shape.maxX - shape.minX, shape.maxY - shape.minY));
                    break;
                }
                case BACK: {
                    vert1 = new Vector3f(shape.minX, shape.maxY, shape.maxZ);
                    vert2 = new Vector3f(shape.minX, shape.minY, shape.maxZ);
                    vert3 = new Vector3f(shape.maxX, shape.minY, shape.maxZ);
                    vert4 = new Vector3f(shape.maxX, shape.maxY, shape.maxZ);
                    shapeSize.add(new Vector2f(shape.maxX - shape.minX, shape.maxY - shape.minY));
                    break;
                }
                case LEFT: {
                    vert1 = new Vector3f(shape.minX, shape.maxY, shape.minZ);
                    vert2 = new Vector3f(shape.minX, shape.minY, shape.minZ);
                    vert3 = new Vector3f(shape.minX, shape.minY, shape.maxZ);
                    vert4 = new Vector3f(shape.minX, shape.maxY, shape.maxZ);
                    shapeSize.add(new Vector2f(shape.maxZ - shape.minZ, shape.maxY - shape.minY));
                    break;
                }
                case RIGHT: {
                    vert1 = new Vector3f(shape.maxX, shape.maxY, shape.maxZ);
                    vert2 = new Vector3f(shape.maxX, shape.minY, shape.maxZ);
                    vert3 = new Vector3f(shape.maxX, shape.minY, shape.minZ);
                    vert4 = new Vector3f(shape.maxX, shape.maxY, shape.minZ);
                    shapeSize.add(new Vector2f(shape.maxZ - shape.minZ, shape.maxY - shape.minY));
                    break;
                }
            }

            addVertex(buffer, vert1, new Vector2f(textureUV[0]).add(shapeSize.x, shapeSize.y));
            addVertex(buffer, vert2, new Vector2f(textureUV[1]).add(shapeSize.x, shapeSize.y));
            addVertex(buffer, vert3, new Vector2f(textureUV[2]).add(shapeSize.x, shapeSize.y));

            addVertex(buffer, vert3, new Vector2f(textureUV[2]).add(shapeSize.x, shapeSize.y));
            addVertex(buffer, vert4, new Vector2f(textureUV[3]).add(shapeSize.x, shapeSize.y));
            addVertex(buffer, vert1, new Vector2f(textureUV[0]).add(shapeSize.x, shapeSize.y));
        }

        buffer.flip();

        Mesh mesh = new Mesh(buffer, vertexCount);

        int stride = 5 * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        mesh.unbind();

        //System.out.println("Chunk mesh built in " + (System.currentTimeMillis() - startTime) + "ms");
        return mesh;
    }

    private static void addVertex(FloatBuffer buffer, Vector3f pos, Vector2f uv) {
        buffer.put(pos.x);
        buffer.put(pos.y);
        buffer.put(pos.z);
        buffer.put(uv.x);
        buffer.put(uv.y);
    }

    // GREEDY
    private static List<Vector3i> getVoxels(Chunk chunk) {
        List<Vector3i> voxels = new ArrayList<>();
        for (int i = 0; i < Chunk.CHUNK_SIZE_CUBED; i++) {
            Vector3i localPosition = chunk.getLocalPositionFromIndex(i);
            BlockType type = chunk.getBlockTypeAt(localPosition);
            if (type != null && type != BlockType.AIR) {
                voxels.add(localPosition);
            }
        }
        return voxels;
    }

    private static boolean shouldRenderFace(Chunk chunk, BlockType blockType, BlockPos blockPos, Face face) {
        if (blockType == BlockType.AIR) return false;
        Vector3i faceNormal = getFaceNormal(face);
        BlockPos neighborPos = blockPos.add(faceNormal.x, faceNormal.y, faceNormal.z);
        BlockType neighborType = chunk.getWorld().getBlockTypeAt(neighborPos);
        if (neighborType == null || neighborType == BlockType.AIR) return true;
        return false;
    }

    public static List<GreedyShape> generateShapes(Chunk chunk) {
        List<GreedyShape> shapes = new ArrayList<>();

        for (Face face : Face.values()) {
            //if (face != Face.TOP) continue;
            List<Vector3i> remainingVoxels = getVoxels(chunk);

            while (!remainingVoxels.isEmpty()) {
                Vector3i initialVoxel = remainingVoxels.get(0);
                BlockType shapeType = chunk.getBlockTypeAt(initialVoxel);

                if (!shouldRenderFace(chunk, shapeType, chunk.toWorldPos(initialVoxel), face)) {
                    remainingVoxels.remove(initialVoxel); // the initialVoxel shouldn't even be rendered
                    continue;
                }

                GreedyShape shape = new GreedyShape(chunk, face, initialVoxel);

                List<Vector3i> possibleVoxels = new ArrayList<>();
                for (Vector3i v : remainingVoxels) {
                    BlockType t = chunk.getBlockTypeAt(v);
                    if (t == shapeType && shouldRenderFace(chunk, shapeType, chunk.toWorldPos(v), face)) {
                        possibleVoxels.add(v);
                    }
                }

                for (Face f : Face.values()) {
                    Vector3i direction = getFaceNormal(f);
                    while (shape.expand(direction, possibleVoxels)) {}
                }

                for (Vector3i v : shape.voxels) {
                    remainingVoxels.remove(v);
                }
                //System.out.println("remainingVoxels " + remainingVoxels.size());

                shapes.add(shape);
            }
        }

        //System.out.println("Shapes: " + shapes.size());
        return shapes;
    }
}
