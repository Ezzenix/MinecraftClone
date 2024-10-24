package com.ezzenix.rendering;

import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.rendering.util.BufferBuilder;
import com.ezzenix.rendering.util.RenderLayer;
import com.ezzenix.rendering.util.VertexBuffer;
import com.ezzenix.rendering.util.VertexFormat;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FLOAT;
import static org.lwjgl.opengl.GL30.GL_INT;

public class LineRenderer {
	private static final VertexFormat FORMAT = new VertexFormat(VertexFormat.DrawMode.LINES, GL_FLOAT, 3, GL_INT, 1);
	private static final RenderLayer LINES = new RenderLayer(new Shader("debugLine")).format(FORMAT).depth(GL_LESS);
	private static final BufferBuilder.Immediate immediate = new BufferBuilder.Immediate();

	public static void renderBatch() {
		immediate.draw(LINES);
	}

	public static void drawLine(Vector3f pos1, Vector3f pos2, int color) {
		BufferBuilder builder = immediate.getBuilder(LINES);

		builder.vertex(pos1).color(color);
		builder.vertex(pos2).color(color);
	}

	public static void drawLine(Vector3f pos1, Vector3f pos2) {
		drawLine(pos1, pos2, -1);
	}

	public static void drawBox(Vector3f corner1, Vector3f corner2, int color) {
		float minX = Math.min(corner1.x, corner2.x);
		float maxX = Math.max(corner1.x, corner2.x);
		float minY = Math.min(corner1.y, corner2.y);
		float maxY = Math.max(corner1.y, corner2.y);
		float minZ = Math.min(corner1.z, corner2.z);
		float maxZ = Math.max(corner1.z, corner2.z);

		drawLine(new Vector3f(minX, minY, minZ), new Vector3f(maxX, minY, minZ), color);
		drawLine(new Vector3f(minX, minY, minZ), new Vector3f(minX, minY, maxZ), color);
		drawLine(new Vector3f(maxX, minY, minZ), new Vector3f(maxX, minY, maxZ), color);
		drawLine(new Vector3f(minX, minY, maxZ), new Vector3f(maxX, minY, maxZ), color);

		drawLine(new Vector3f(minX, minY, minZ), new Vector3f(minX, maxY, minZ), color);
		drawLine(new Vector3f(minX, minY, maxZ), new Vector3f(minX, maxY, maxZ), color);
		drawLine(new Vector3f(maxX, minY, maxZ), new Vector3f(maxX, maxY, maxZ), color);
		drawLine(new Vector3f(maxX, minY, minZ), new Vector3f(maxX, maxY, minZ), color);

		drawLine(new Vector3f(minX, maxY, minZ), new Vector3f(maxX, maxY, minZ), color);
		drawLine(new Vector3f(minX, maxY, minZ), new Vector3f(minX, maxY, maxZ), color);
		drawLine(new Vector3f(maxX, maxY, minZ), new Vector3f(maxX, maxY, maxZ), color);
		drawLine(new Vector3f(minX, maxY, maxZ), new Vector3f(maxX, maxY, maxZ), color);
	}

	public static void highlightVoxel(Vector3f voxel, int color) {
		drawBox(voxel, new Vector3f(voxel).add(1, 1, 1), color);
	}

	public static void highlightVoxel(Vector3f voxel) {
		highlightVoxel(voxel, -1);
	}
}
