package com.ezzenix.rendering;

import com.ezzenix.game.BlockType;
import com.ezzenix.game.Chunk;
import com.ezzenix.utils.BlockPos;
import com.ezzenix.utils.Face;
import com.ezzenix.utils.textures.TextureUV;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.BufferUtils.createFloatBuffer;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class ChunkBuilder {
    private Map<Integer, Map<Integer, Chunk>> chunks;

    private static boolean isNeighborSolid(Chunk chunk, BlockPos blockPos, Vector3f face) {
        BlockPos blockPosOffset = new BlockPos((int) face.x, (int) face.y, (int) face.z);
        BlockType neighborType = chunk.getWorld().getBlockTypeAt(blockPos.add(blockPosOffset));
        return neighborType != null;
    }

    public static TextureUV getBlockTextureUV(BlockType blockType, Vector3f face) {
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

    public static Mesh createMesh(Chunk chunk) {
        long startTime = System.currentTimeMillis();

        Map<BlockPos, BlockType> blocks = chunk.getBlocks();

        List<Float> vertexList = new ArrayList<>();

        for (BlockPos blockPos : blocks.keySet()) {
            BlockType blockType = blocks.get(blockPos);

            Vector3f offset = new Vector3f(
                    blockPos.x,// - chunk.getChunkX()*16,
                    blockPos.y,
                    blockPos.z// - chunk.getChunkZ()*16
            );

            for (Vector3f face : faces) {
                if (isNeighborSolid(chunk, blockPos, face)) {
                    continue;
                }

                List<Vector3f> unitCubeFace = Face.faceUnitCube(face);

                Vector3f vert1 = unitCubeFace.get(0).add(offset);
                Vector3f vert2 = unitCubeFace.get(1).add(offset);
                Vector3f vert3 = unitCubeFace.get(2).add(offset);
                Vector3f vert4 = unitCubeFace.get(3).add(offset);

                TextureUV textureUV = getBlockTextureUV(blockType, face);

                addVertex(vertexList, vert1, textureUV.uv1.x, textureUV.uv1.y);
                addVertex(vertexList, vert2, textureUV.uv2.x, textureUV.uv2.y);
                addVertex(vertexList, vert3, textureUV.uv3.x, textureUV.uv3.y);

                addVertex(vertexList, vert3, textureUV.uv3.x, textureUV.uv3.y);
                addVertex(vertexList, vert4, textureUV.uv4.x, textureUV.uv4.y);
                addVertex(vertexList, vert1, textureUV.uv1.x, textureUV.uv1.y);
            }
        }

        float[] vertexArray = new float[vertexList.size()];
        for (int i = 0; i < vertexList.size(); i++) {
            vertexArray[i] = vertexList.get(i);
        }
        FloatBuffer vertexBuffer = createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray);
        vertexBuffer.flip();

        Mesh mesh = new Mesh(vertexBuffer, vertexList.size());

        glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        mesh.unbind();

        System.out.println("Chunk mesh built in " + (System.currentTimeMillis() - startTime) + "ms");

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
