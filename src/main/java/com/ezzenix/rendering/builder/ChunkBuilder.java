package com.ezzenix.rendering.builder;

import com.ezzenix.engine.opengl.utils.BlockPos;
import com.ezzenix.game.Chunk;
import com.ezzenix.game.blocks.BlockRegistry;
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
import static org.lwjgl.system.MemoryUtil.memFree;

public class ChunkBuilder {
    static Vector3i getFaceNormal(Face face) {
        return switch (face) {
            case TOP -> new Vector3i(0, 1, 0);
            case BOTTOM -> new Vector3i(0, -1, 0);
            case RIGHT -> new Vector3i(1, 0, 0);
            case LEFT -> new Vector3i(-1, 0, 0);
            case FRONT -> new Vector3i(0, 0, 1);
            case BACK -> new Vector3i(0, 0, -1);
        };
    }

    private static boolean shouldCullFace(Chunk chunk, BlockType type, BlockPos blockPos) {
        BlockType neighborType = chunk.getWorld().getBlockTypeAt(blockPos);
        if (type == neighborType) return true;
        return neighborType != null && neighborType != BlockType.AIR && neighborType != BlockType.WATER;
    }

    public static Vector2f[] getBlockTextureUV(BlockType blockType, Vector3f face) {
        if (face == com.ezzenix.engine.opengl.utils.OldFace.TOP) return blockType.textureUVTop;
        if (face == com.ezzenix.engine.opengl.utils.OldFace.BOTTOM) return blockType.textureUVBottom;
        return blockType.textureUVSides;
    }

    private static final List<Vector3f> faces = new ArrayList<>();

    static {
        faces.add(com.ezzenix.engine.opengl.utils.OldFace.TOP);
        faces.add(com.ezzenix.engine.opengl.utils.OldFace.BACK);
        faces.add(com.ezzenix.engine.opengl.utils.OldFace.BOTTOM);
        faces.add(com.ezzenix.engine.opengl.utils.OldFace.RIGHT);
        faces.add(com.ezzenix.engine.opengl.utils.OldFace.LEFT);
        faces.add(com.ezzenix.engine.opengl.utils.OldFace.FRONT);
    }

    public static Mesh createMesh(Chunk chunk, boolean waterOnly) {
        //long startTime = System.currentTimeMillis();

        List<GreedyShape> shapes = generateShapes(chunk);


        byte[] blockArray = chunk.getBlockArray();

        List<Float> vertexList = new ArrayList<>();

        for (int index = 0; index < blockArray.length; index++) {
            byte blockId = blockArray[index];
            if (blockId == 0) continue; // air

            BlockType blockType = BlockRegistry.getBlockFromId(blockId);
            if (waterOnly && blockType != BlockType.WATER) continue;
            if (!waterOnly && blockType == BlockType.WATER) continue;

            Vector3i localPosition = chunk.getLocalPositionFromIndex(index);
            Vector3f localPositionf = new Vector3f(localPosition.x, localPosition.y, localPosition.z);

            for (Vector3f face : faces) {
                BlockPos neighborPos = new BlockPos(
                        (int) (chunk.x * 16 + localPosition.x + face.x),
                        (int) (chunk.y * 16 + localPosition.y + face.y),
                        (int) (chunk.z * 16 + localPosition.z + face.z)
                );
                if (shouldCullFace(chunk, blockType, neighborPos)) continue;

                List<Vector3f> unitCubeFace = com.ezzenix.engine.opengl.utils.OldFace.faceUnitCube(face);

                Vector3f vert1 = unitCubeFace.get(0).add(0.5f, 0.5f, 0.5f).add(localPositionf);
                Vector3f vert2 = unitCubeFace.get(1).add(0.5f, 0.5f, 0.5f).add(localPositionf);
                Vector3f vert3 = unitCubeFace.get(2).add(0.5f, 0.5f, 0.5f).add(localPositionf);
                Vector3f vert4 = unitCubeFace.get(3).add(0.5f, 0.5f, 0.5f).add(localPositionf);

                Vector2f[] textureUV = getBlockTextureUV(blockType, face);

                addVertex(vertexList, vert1, textureUV[0].x, textureUV[0].y);
                addVertex(vertexList, vert2, textureUV[1].x, textureUV[1].y);
                addVertex(vertexList, vert3, textureUV[2].x, textureUV[2].y);

                addVertex(vertexList, vert3, textureUV[2].x, textureUV[2].y);
                addVertex(vertexList, vert4, textureUV[3].x, textureUV[3].y);
                addVertex(vertexList, vert1, textureUV[0].x, textureUV[0].y);
            }
        }

        float[] vertexArray = new float[vertexList.size()];
        for (int i = 0; i < vertexList.size(); i++) {
            vertexArray[i] = vertexList.get(i);
        }
        FloatBuffer vertexBuffer = createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray);
        vertexBuffer.flip();

        Mesh mesh = new Mesh(vertexBuffer, vertexList.size() / 5);

        int stride = 5 * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        mesh.unbind();

        //System.out.println("Chunk mesh built in " + (System.currentTimeMillis() - startTime) + "ms");
        return mesh;
    }

    private static void addVertex(List<Float> vertexList, Vector3f pos, float uvX, float uvY) {
        vertexList.add(pos.x);
        vertexList.add(pos.y);
        vertexList.add(pos.z);
        vertexList.add(uvX);
        vertexList.add(uvY);
    }

    // GREEDY
    private static List<Vector3i> getVoxels(Chunk chunk) {
        List<Vector3i> voxels = new ArrayList<>();
        for (int i = 0; i < Chunk.CHUNK_SIZE_CUBED; i++) {
            Vector3i localPosition = chunk.getLocalPositionFromIndex(i);
            BlockType type = getBlockType(chunk, localPosition);
            if (type != null && type != BlockType.AIR) {
                voxels.add(localPosition);
            }
        }
        return voxels;
    }

    static BlockPos toWorldPos(Chunk chunk, Vector3i voxel) {
        return new BlockPos(chunk.x*Chunk.CHUNK_SIZE + voxel.x, chunk.y*Chunk.CHUNK_SIZE + voxel.y, chunk.z*Chunk.CHUNK_SIZE + voxel.z);
    }

    static BlockType getBlockType(Chunk chunk, Vector3i voxel) {
        return chunk.getWorld().getBlockTypeAt(toWorldPos(chunk, voxel));
    }

    private static boolean shouldRenderFace(Chunk chunk, BlockType blockType, BlockPos blockPos, Face face) {
        if (blockType == BlockType.AIR) return false;
        Vector3i faceNormal = getFaceNormal(face);
        BlockPos neighborPos = blockPos.add(faceNormal.x, faceNormal.y, faceNormal.z);
        BlockType neighborType = chunk.getWorld().getBlockTypeAt(neighborPos);
        if (neighborType == BlockType.AIR) return false;
        if (blockType == neighborType) return false;
        return true;
    }

    public static List<GreedyShape> generateShapes(Chunk chunk) {
        List<GreedyShape> shapes = new ArrayList<>();

        for (Face face : Face.values()) {
            //if (face != Face.TOP) continue;
            List<Vector3i> remainingVoxels = getVoxels(chunk);

            while (!remainingVoxels.isEmpty()) {
                Vector3i initialVoxel = remainingVoxels.get(0);
                BlockType shapeType = getBlockType(chunk, initialVoxel);

                if (!shouldRenderFace(chunk, shapeType, toWorldPos(chunk, initialVoxel), face)) {
                    remainingVoxels.remove(initialVoxel); // the initialVoxel shouldn't even be rendered
                    continue;
                }

                GreedyShape shape = new GreedyShape(chunk, face, initialVoxel);

                List<Vector3i> possibleVoxels = new ArrayList<>();
                for (Vector3i v : remainingVoxels) {
                    BlockType t = getBlockType(chunk, v);
                    if (t == shapeType && shouldRenderFace(chunk, shapeType, toWorldPos(chunk, v), face)) {
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

        System.out.println("Shapes: " + shapes.size());
        return shapes;
    }
}
