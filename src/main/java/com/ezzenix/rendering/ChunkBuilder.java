package com.ezzenix.rendering;

import com.ezzenix.Game;
import com.ezzenix.game.BlockType;
import com.ezzenix.game.Chunk;
import com.ezzenix.utilities.BlockPos;
import com.ezzenix.utilities.Face;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.system.MemoryStack.stackMallocFloat;
import static org.lwjgl.system.MemoryStack.stackPush;

public class ChunkBuilder {
    public static Mesh createMesh(Chunk chunk) {
        Map<BlockPos, BlockType> blocks = chunk.getBlocks();

        FloatBuffer vertexBuffer;
        try (MemoryStack stack = stackPush()) {
            vertexBuffer = stackMallocFloat(5 * 6 * 6 * blocks.size());

            List<Vector3f> faces = new ArrayList<>();
            faces.add(Face.UP);
            faces.add(Face.BACK);
            faces.add(Face.DOWN);
            faces.add(Face.RIGHT);
            faces.add(Face.LEFT);
            faces.add(Face.FRONT);

            for (BlockPos blockPos : blocks.keySet()) {
                BlockType blockType = blocks.get(blockPos);

                Vector3f offset = new Vector3f(
                        blockPos.x - chunk.getChunkX()*16,
                        blockPos.y,
                        blockPos.z - chunk.getChunkZ()*16
                );

                for (Vector3f face : faces) {
                    List<Vector3f> unitCubeFace = Face.faceUnitCube(face);

                    Vector3f vert1 = unitCubeFace.get(0).add(offset);
                    Vector3f vert2 = unitCubeFace.get(1).add(offset);
                    Vector3f vert3 = unitCubeFace.get(2).add(offset);
                    Vector3f vert4 = unitCubeFace.get(3).add(offset);

                    List<Vector2f> uvCoords = Game.getInstance().blockTextures.getTextureUVs("stone");

                    vertexBuffer.put(vert1.x).put(vert1.y).put(vert1.z).put(uvCoords.get(0).x).put(uvCoords.get(0).y);
                    vertexBuffer.put(vert2.x).put(vert2.y).put(vert2.z).put(uvCoords.get(1).x).put(uvCoords.get(1).y);
                    vertexBuffer.put(vert3.x).put(vert3.y).put(vert3.z).put(uvCoords.get(2).x).put(uvCoords.get(2).y);

                    vertexBuffer.put(vert3.x).put(vert3.y).put(vert3.z).put(uvCoords.get(2).x).put(uvCoords.get(2).y);
                    vertexBuffer.put(vert4.x).put(vert4.y).put(vert4.z).put(uvCoords.get(3).x).put(uvCoords.get(3).y);
                    vertexBuffer.put(vert1.x).put(vert1.y).put(vert1.z).put(uvCoords.get(0).x).put(uvCoords.get(0).y);
                }
            }

            vertexBuffer.flip();
        }

        Mesh mesh = new Mesh(vertexBuffer, 6 * 6 * blocks.size());

        glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        mesh.unbind();

        return mesh;
    }
}
