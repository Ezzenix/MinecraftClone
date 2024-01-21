package com.ezzenix.game.world.chunk.rendering.builder;

import com.ezzenix.Game;
import com.ezzenix.engine.core.enums.Face;
import com.ezzenix.engine.opengl.Mesh;
import com.ezzenix.game.core.BlockPos;
import com.ezzenix.game.blocks.BlockRegistry;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.world.chunk.Chunk;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class ChunkBuilder {
    public static Vector2f[] getBlockTextureUV(BlockType blockType, Face face) {
        if (face == Face.TOP) return blockType.textureUVTop;
        if (face == Face.BOTTOM) return blockType.textureUVBottom;
        return blockType.textureUVSides;
    }

    public static Mesh createMesh(Chunk chunk, boolean transparentBlocksOnly) {
        long startTime = System.currentTimeMillis();
        List<Float> vertexList = new ArrayList<>();

        // Flowers
        if (transparentBlocksOnly) {
            for (int i = 0; i < Chunk.CHUNK_SIZE_CUBED; i++) {
                Vector3i localPosition = chunk.getLocalPositionFromIndex(i);
                BlockType blockType = chunk.getBlockTypeAt(localPosition);
                if (blockType == null || !blockType.isFlower()) continue;
                Vector3f midPos = new Vector3f(localPosition.x + 0.5f, localPosition.y, localPosition.z + 0.5f);

                Vector2f[] textureUV = getBlockTextureUV(blockType, Face.TOP);

                float flowerSize = 0.9f;
                for (float deg = 45; deg <= (45 + 90*4); deg += 90) {
                    Vector3f lookVector = new Vector3f((float)-Math.cos(Math.toRadians(deg)), 0.0f, (float)-Math.sin(Math.toRadians(deg)));
                    lookVector.mul((float)Math.pow(flowerSize, 4));
                    addQuad(vertexList,
                            new Vector3f(midPos).add(-lookVector.x, flowerSize, -lookVector.z),
                            new Vector3f(midPos).add(-lookVector.x, 0, -lookVector.z),
                            new Vector3f(midPos).add(lookVector.x, 0, lookVector.z),
                            new Vector3f(midPos).add(lookVector.x, flowerSize, lookVector.z),
                            textureUV[0],
                            textureUV[2],
                            new Vector2f(1, 1),
                            0, 0, 0, 0
                    );
                }
            }
        }

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

            VoxelFace initialVoxel = shape.initialVoxelFace;
            addQuad(vertexList, vert1, vert2, vert3, vert4, textureUV[0], textureUV[2], shapeSize, initialVoxel.ao1, initialVoxel.ao2, initialVoxel.ao3, initialVoxel.ao4);
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
        Game.getInstance().TIME_MESH_BUILD += (System.currentTimeMillis() - startTime);
        return mesh;
    }

    private static void addQuad(List<Float> vertexList, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, Vector2f uvCorner1, Vector2f uvCorner2, Vector2f numTiles, float ao1, float ao2, float ao3, float ao4) {
        Vector2f uv1 = new Vector2f(uvCorner1.x, uvCorner1.y).add(numTiles.x, numTiles.y);
        Vector2f uv2 = new Vector2f(uvCorner1.x, uvCorner2.y).add(numTiles.x, numTiles.y);
        Vector2f uv3 = new Vector2f(uvCorner2.x, uvCorner2.y).add(numTiles.x, numTiles.y);
        Vector2f uv4 = new Vector2f(uvCorner2.x, uvCorner1.y).add(numTiles.x, numTiles.y);

        addVertex(vertexList, p1, uv1, ao1);
        addVertex(vertexList, p2, uv2, ao2);
        addVertex(vertexList, p3, uv3, ao3);

        addVertex(vertexList, p3, uv3, ao3);
        addVertex(vertexList, p4, uv4, ao4);
        addVertex(vertexList, p1, uv1, ao1);
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
        Vector3i faceNormal = face.getNormal();
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

        byte[] blockArray = chunk.getBlockArray();

        for (int i = 0; i < Chunk.CHUNK_SIZE_CUBED; i++) {
            byte blockId = blockArray[i];
            if (blockId == 1) continue; // air
            BlockType type = BlockRegistry.getBlockFromId(blockId);
            if (type.isFlower()) continue;

            Vector3i localPosition = chunk.getLocalPositionFromIndex(i);

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
