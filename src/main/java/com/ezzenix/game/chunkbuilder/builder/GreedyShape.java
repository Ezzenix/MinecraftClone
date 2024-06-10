package com.ezzenix.game.chunkbuilder.builder;

import com.ezzenix.engine.core.enums.Face;
import com.ezzenix.game.world.Chunk;
import com.ezzenix.math.LocalPosition;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.List;

public class GreedyShape {
	public final Chunk chunk;
	public final List<VoxelFace> voxels;
	public final VoxelFace initialVoxelFace;

	public int minX, maxX;
	public int minY, maxY;
	public int minZ, maxZ;


	public GreedyShape(Chunk chunk, VoxelFace initialVoxelFace) {
		this.voxels = new ArrayList<>();
		this.initialVoxelFace = initialVoxelFace;
		voxels.add(initialVoxelFace);
		this.chunk = chunk;
		minX = initialVoxelFace.position.x;
		maxX = initialVoxelFace.position.x;
		minY = initialVoxelFace.position.y;
		maxY = initialVoxelFace.position.y;
		minZ = initialVoxelFace.position.z;
		maxZ = initialVoxelFace.position.z;
	}

	private boolean isAtEdgeInDirection(Vector3i direction, VoxelFace voxel) {
		if (direction.x < 0) return voxel.position.x == minX;
		if (direction.x > 0) return voxel.position.x == maxX;
		if (direction.y < 0) return voxel.position.y == minY;
		if (direction.y > 0) return voxel.position.y == maxY;
		if (direction.z < 0) return voxel.position.z == minZ;
		if (direction.z > 0) return voxel.position.z == maxZ;
		return false;
	}

	private List<VoxelFace> getNextVoxelsInDirection(Vector3i direction, List<VoxelFace> voxelsAtEdge, List<VoxelFace> possibleVoxels) {
		List<VoxelFace> voxels = new ArrayList<>();
		for (VoxelFace edgeVoxel : voxelsAtEdge) {
			LocalPosition newVoxelPos = edgeVoxel.position.add(direction.x, direction.y, direction.z);
			VoxelFace newVoxel = getVoxelAt(newVoxelPos, possibleVoxels);
			if (newVoxel != null && canMergeWith(newVoxel)) {
				voxels.add(newVoxel);
			} else {
				return null;
			}
		}
		return voxels;
	}

	private VoxelFace getVoxelAt(LocalPosition position, List<VoxelFace> possibleVoxels) {
		for (VoxelFace voxel : possibleVoxels) {
			if (voxel.position.equals(position)) {
				return voxel;
			}
		}
		return null;
	}

	private boolean canMergeWith(VoxelFace voxelFace) {
		if (voxelFace.blockId != this.initialVoxelFace.blockId) return false;
		if (this.initialVoxelFace.ao1 != voxelFace.ao1) return false;
		if (this.initialVoxelFace.ao2 != voxelFace.ao2) return false;
		if (this.initialVoxelFace.ao3 != voxelFace.ao3) return false;
		if (this.initialVoxelFace.ao4 != voxelFace.ao4) return false;
		return true;
	}

	public static List<GreedyShape> createShapesFrom(Chunk chunk, List<VoxelFace> possibleVoxelFaces) {
		List<GreedyShape> shapes = new ArrayList<>();

		while (!possibleVoxelFaces.isEmpty() && !chunk.isDisposed) {
			VoxelFace initalVoxelFace = possibleVoxelFaces.get(0);

			GreedyShape shape = new GreedyShape(chunk, initalVoxelFace);
			possibleVoxelFaces.remove(initalVoxelFace);

			for (Face face : Face.values()) {
				Vector3i direction = face.getNormal();

				List<VoxelFace> voxelsAtEdge = new ArrayList<>();
				for (VoxelFace voxel : shape.voxels) {
					if (shape.isAtEdgeInDirection(direction, voxel)) {
						voxelsAtEdge.add(voxel);
					}
				}

				while (true) {
					if (chunk.isDisposed) {
						return shapes;
					}

					List<VoxelFace> voxelsToExpandTo = shape.getNextVoxelsInDirection(direction, voxelsAtEdge, possibleVoxelFaces);
					if (voxelsToExpandTo == null) {
						break;
					}

					voxelsAtEdge = voxelsToExpandTo;
					shape.voxels.addAll(voxelsToExpandTo);
					possibleVoxelFaces.removeAll(voxelsToExpandTo);

					switch (face) {
						case TOP:
							shape.maxY += 1;
							break;
						case BOTTOM:
							shape.minY += 1;
							break;
						case FRONT:
							shape.minZ += 1;
							break;
						case BACK:
							shape.maxZ += 1;
							break;
						case RIGHT:
							shape.maxX += 1;
							break;
						case LEFT:
							shape.minX += 1;
							break;
					}
				}
			}

			shapes.add(shape);
		}

		return shapes;
	}
}
