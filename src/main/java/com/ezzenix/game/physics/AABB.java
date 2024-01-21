package com.ezzenix.game.physics;

import com.ezzenix.game.BlockPos;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.hud.Debug;
import org.joml.Vector3f;

public class AABB {
	private float width;
	private float height;

	public AABB(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public Vector3f getCollision(Vector3f position, BlockPos blockPos, BlockType blockType) {
		// Calculate the bounds of the AABB
		float minX = position.x - width / 2;
		float maxX = position.x + width / 2;
		float minY = position.y;
		float maxY = position.y + height;
		float minZ = position.z - width / 2;
		float maxZ = position.z + width / 2;

		Vector3f color = new Vector3f(0, 1, 0);
		Debug.drawLine(new Vector3f(minX, minY, minZ), new Vector3f(maxX, minY, minZ), color);
		Debug.drawLine(new Vector3f(minX, minY, minZ), new Vector3f(minX, minY, maxZ), color);
		Debug.drawLine(new Vector3f(maxX, minY, minZ), new Vector3f(maxX, minY, maxZ), color);
		Debug.drawLine(new Vector3f(minX, minY, maxZ), new Vector3f(maxX, minY, maxZ), color);

		Debug.drawLine(new Vector3f(minX, minY, minZ), new Vector3f(minX, maxY, minZ), color);
		Debug.drawLine(new Vector3f(minX, minY, maxZ), new Vector3f(minX, maxY, maxZ), color);
		Debug.drawLine(new Vector3f(maxX, minY, maxZ), new Vector3f(maxX, maxY, maxZ), color);
		Debug.drawLine(new Vector3f(maxX, minY, minZ), new Vector3f(maxX, maxY, minZ), color);

		Debug.drawLine(new Vector3f(minX, maxY, minZ), new Vector3f(maxX, maxY, minZ), color);
		Debug.drawLine(new Vector3f(minX, maxY, minZ), new Vector3f(minX, maxY, maxZ), color);
		Debug.drawLine(new Vector3f(maxX, maxY, minZ), new Vector3f(maxX, maxY, maxZ), color);
		Debug.drawLine(new Vector3f(minX, maxY, maxZ), new Vector3f(maxX, maxY, maxZ), color);

		// Calculate the bounds of the block
		float blockMinX = blockPos.x;
		float blockMaxX = blockPos.x + 1;
		float blockMinY = blockPos.y;
		float blockMaxY = blockPos.y + 1;
		float blockMinZ = blockPos.z;
		float blockMaxZ = blockPos.z + 1;

		// Calculate the intersection
		float xIntersection = Math.min(maxX, blockMaxX) - Math.max(minX, blockMinX);
		float yIntersection = Math.min(maxY, blockMaxY) - Math.max(minY, blockMinY);
		float zIntersection = Math.min(maxZ, blockMaxZ) - Math.max(minZ, blockMinZ);

		if (xIntersection > 0 && yIntersection > 0 && zIntersection > 0) {
			return new Vector3f(xIntersection, yIntersection, zIntersection);
		} else {
			return new Vector3f(0, 0, 0);
		}
	}
}
