package com.ezzenix.rendering.util;

import static org.lwjgl.opengl.GL30.*;

public class VertexFormat {
	public static VertexFormat POSITION_COLOR = new VertexFormat(GL_FLOAT, 2, GL_FLOAT, 4);
	public static VertexFormat POSITION_UV_AO = new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2, GL_FLOAT, 1);
	public static VertexFormat POSITION_UV = new VertexFormat(GL_FLOAT, 3, GL_FLOAT, 2);

	private final int[] types;
	private final int[] sizes;
	private final DrawMode drawMode;
	private int vertexSizeBytes;

	public VertexFormat(DrawMode drawMode, int... data) {
		if (data.length % 2 != 0)
			throw new RuntimeException("VertexFormat data array length must be even number");

		this.drawMode = drawMode;
		this.vertexSizeBytes = 0;

		types = new int[data.length / 2];
		sizes = new int[data.length / 2];

		for (int i = 0; i < data.length; i += 2) {
			types[i / 2] = data[i];
			sizes[i / 2] = data[i + 1];

			this.vertexSizeBytes += getBytes(data[i], data[i + 1]);
		}
	}

	public VertexFormat(int... data) {
		this(DrawMode.TRIANGLES, data);
	}

	public DrawMode getDrawMode() {
		return this.drawMode;
	}

	private static int getBytes(int type, int size) {
		return size * switch (type) {
			case GL_FLOAT -> Float.BYTES;
			case GL_BYTE -> Byte.BYTES;
			case GL_SHORT -> Short.BYTES;
			case GL_INT -> Integer.BYTES;
			default -> 0;
		};
	}

	public int getVertexSizeBytes() {
		return this.vertexSizeBytes;
	}

	public void use() {
		int stride = this.vertexSizeBytes;

		int pointer = 0;
		for (int i = 0; i < this.types.length; i++) {
			if (this.types[i] == GL_INT) {
				glVertexAttribIPointer(i, sizes[i], types[i], stride, pointer);
			} else if (this.types[i] == GL_BOOL) {
				glVertexAttribPointer(i, sizes[i], GL_UNSIGNED_BYTE, false, stride, pointer);
			} else {
				glVertexAttribPointer(i, sizes[i], types[i], false, stride, pointer);
			}
			glEnableVertexAttribArray(i);
			pointer += getBytes(types[i], sizes[i]);
		}
	}

	public enum DrawMode {
		TRIANGLES(GL_TRIANGLES),
		LINES(GL_LINES);

		public final int id;

		DrawMode(int id) {
			this.id = id;
		}
	}
}
