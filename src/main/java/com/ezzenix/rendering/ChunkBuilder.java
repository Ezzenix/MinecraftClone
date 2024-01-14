package com.ezzenix.rendering;

import com.ezzenix.engine.opengl.utils.BlockPos;
import com.ezzenix.engine.opengl.utils.Face;
import com.ezzenix.game.Chunk;
import com.ezzenix.game.blocks.BlockRegistry;
import com.ezzenix.game.blocks.BlockType;
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
    private static boolean shouldCullFace(Chunk chunk, BlockType type, BlockPos blockPos) {
        BlockType neighborType = chunk.getWorld().getBlockTypeAt(blockPos);
        if (type == neighborType) return true;
        return neighborType != null && neighborType != BlockType.AIR && neighborType != BlockType.WATER;
    }

    public static Vector2f[] getBlockTextureUV(BlockType blockType, Vector3f face) {
        if (face == Face.TOP) return blockType.textureUVTop;
        if (face == Face.BOTTOM) return blockType.textureUVBottom;
        return blockType.textureUVSides;
    }

    private static final List<Vector3f> faces = new ArrayList<>();

    static {
        faces.add(Face.TOP);
        faces.add(Face.BACK);
        faces.add(Face.BOTTOM);
        faces.add(Face.RIGHT);
        faces.add(Face.LEFT);
        faces.add(Face.FRONT);
    }

    public static Mesh createMesh(Chunk chunk, boolean waterOnly) {
        //long startTime = System.currentTimeMillis();


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

                List<Vector3f> unitCubeFace = Face.faceUnitCube(face);

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
}
