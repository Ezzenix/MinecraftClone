package com.ezzenix.client.rendering.chunkbuilder.builder;

import com.ezzenix.blocks.BlockRegistry;
import com.ezzenix.blocks.BlockType;
import com.ezzenix.client.rendering.chunkbuilder.ChunkBuildRequest;
import com.ezzenix.engine.opengl.Mesh;
import com.ezzenix.enums.Direction;
import com.ezzenix.math.BlockPos;
import com.ezzenix.math.LocalPosition;
import com.ezzenix.world.Chunk;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChunkBuilder {
	public static Vector2f[] getBlockTextureUV(BlockType blockType, Direction direction) {
		if (direction == Direction.UP) return blockType.textureUVTop;
		if (direction == Direction.DOWN) return blockType.textureUVBottom;
		return blockType.textureUVSides;
	}

	private static List<Float> create(ChunkBuildRequest request, boolean transparentBlocksOnly) {
		//long start = System.currentTimeMillis();
		List<Float> vertexList = new ArrayList<>();

		Chunk chunk = request.chunk;

		// Flowers
		if (transparentBlocksOnly) {
			for (int i = 0; i < chunk.getBlockIDs().length; i++) {
				LocalPosition localPosition = LocalPosition.fromIndex(i);
				BlockType blockType = chunk.getBlock(localPosition);
				if (blockType == null || !blockType.isFlower()) continue;
				Vector3f midPos = new Vector3f(localPosition.x + 0.5f, localPosition.y, localPosition.z + 0.5f);

				Vector2f[] textureUV = getBlockTextureUV(blockType, Direction.UP);

				float flowerSize = 0.9f;
				for (float deg = 45; deg <= (45 + 90 * 4); deg += 90) {
					Vector3f lookVector = new Vector3f((float) -Math.cos(Math.toRadians(deg)), 0.0f, (float) -Math.sin(Math.toRadians(deg)));
					lookVector.mul((float) Math.pow(flowerSize, 4));
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

			Vector2f[] textureUV = getBlockTextureUV(blockType, shape.initialVoxelFace.direction);

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
			switch (shape.initialVoxelFace.direction) {
				case UP: {
					vert1.set(shape.minX, shape.maxY, shape.minZ);
					vert2.set(shape.minX, shape.maxY, shape.maxZ);
					vert3.set(shape.maxX, shape.maxY, shape.maxZ);
					vert4.set(shape.maxX, shape.maxY, shape.minZ);
					shapeSize.set(shape.maxX - shape.minX, shape.maxZ - shape.minZ);
					break;
				}
				case DOWN: {
					vert1.set(shape.minX, shape.minY, shape.maxZ);
					vert2.set(shape.minX, shape.minY, shape.minZ);
					vert3.set(shape.maxX, shape.minY, shape.minZ);
					vert4.set(shape.maxX, shape.minY, shape.maxZ);
					shapeSize.set(shape.maxX - shape.minX, shape.maxZ - shape.minZ);
					break;
				}
				case NORTH: {
					vert1.set(shape.maxX, shape.maxY, shape.minZ);
					vert2.set(shape.maxX, shape.minY, shape.minZ);
					vert3.set(shape.minX, shape.minY, shape.minZ);
					vert4.set(shape.minX, shape.maxY, shape.minZ);
					shapeSize.set(shape.maxX - shape.minX, shape.maxY - shape.minY);
					break;
				}
				case SOUTH: {
					vert1.set(shape.minX, shape.maxY, shape.maxZ);
					vert2.set(shape.minX, shape.minY, shape.maxZ);
					vert3.set(shape.maxX, shape.minY, shape.maxZ);
					vert4.set(shape.maxX, shape.maxY, shape.maxZ);
					shapeSize.set(shape.maxX - shape.minX, shape.maxY - shape.minY);
					break;
				}
				case WEST: {
					vert1.set(shape.minX, shape.maxY, shape.minZ);
					vert2.set(shape.minX, shape.minY, shape.minZ);
					vert3.set(shape.minX, shape.minY, shape.maxZ);
					vert4.set(shape.minX, shape.maxY, shape.maxZ);
					shapeSize.set(shape.maxZ - shape.minZ, shape.maxY - shape.minY);
					break;
				}
				case EAST: {
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

		/*
		long time = System.currentTimeMillis() - start;
		if (time > 3) {
			System.out.println("CreateMesh took " + (System.currentTimeMillis() - start) + " ms");
		}
		*/

		return vertexList;
	}

	public static void generate(ChunkBuildRequest request) {
		//long startTime = System.currentTimeMillis();

		List<Float> blockVertexList = create(request, false);
		List<Float> waterVertexList = create(request, true);

		request.blockVertexBuffer = Mesh.convertToBuffer(blockVertexList);
		request.blockVertexLength = blockVertexList.size() / 6;

		request.waterVertexBuffer = Mesh.convertToBuffer(waterVertexList);
		request.waterVertexLength = waterVertexList.size() / 6;

		//System.out.println("Chunk built in " + (System.currentTimeMillis() - startTime) + "ms");
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

	private static boolean shouldRenderFace(Chunk chunk, BlockType blockType, BlockPos blockPos, Direction direction) {
		if (blockType == BlockType.AIR) return false;

		BlockType neighborType = chunk.getBlock(blockPos.add(direction.getNormal().x, direction.getNormal().y, direction.getNormal().z));

		if (neighborType == null) return (direction == Direction.UP || direction == Direction.DOWN);
		if (neighborType == BlockType.AIR) return true;

		if (neighborType == blockType && blockType.isFluid()) return false;

		if (neighborType.isTransparent()) return true;
		return !neighborType.isSolid();
	}

	private static HashMap<Direction, List<VoxelFace>> generateVoxelFaces(Chunk chunk, boolean transparentBlocksOnly) {
		HashMap<Direction, List<VoxelFace>> voxelFaces = new HashMap<>();
		for (Direction direction : Direction.values()) {
			voxelFaces.put(direction, new ArrayList<>());
		}

		byte[] blockArray = chunk.getBlockIDs();

		for (int i = 0; i < chunk.getBlockIDs().length; i++) {
			byte blockId = blockArray[i];
			if (blockId == 1) continue; // air
			BlockType type = BlockRegistry.getBlockFromId(blockId);
			if (type.isFlower()) continue;

			LocalPosition localPosition = LocalPosition.fromIndex(i);

			for (Direction direction : Direction.values()) {
				if ((type.isTransparent() && transparentBlocksOnly) || (!type.isTransparent() && !transparentBlocksOnly)) {
					if (shouldRenderFace(chunk, type, BlockPos.from(chunk, localPosition), direction)) {
						VoxelFace voxelFace = new VoxelFace(localPosition, direction, type.getId());
						voxelFace.calculateAO(chunk);
						voxelFaces.get(direction).add(voxelFace);
					}
				}
			}
		}
		return voxelFaces;
	}

	public static List<GreedyShape> generateShapes(Chunk chunk, boolean transparentBlocksOnly) {
		HashMap<Direction, List<VoxelFace>> voxelFaces = generateVoxelFaces(chunk, transparentBlocksOnly);

		List<GreedyShape> shapes = new ArrayList<>();
		if (voxelFaces.isEmpty()) return shapes; // no faces for chunk, cancel here

		for (Direction direction : Direction.values()) {
			List<VoxelFace> possibleVoxels = voxelFaces.get(direction);
			shapes.addAll(GreedyShape.createShapesFrom(chunk, possibleVoxels));
		}

		return shapes;
	}
}
