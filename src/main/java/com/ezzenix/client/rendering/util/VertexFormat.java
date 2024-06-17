package com.ezzenix.client.rendering.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class VertexFormat {
	public static VertexFormat POSITION_COLOR = new VertexFormat(GL_FLOAT, 2, GL_FLOAT, 4);

	int[] types;
	int[] sizes;

	public VertexFormat(int... data) {
		if (data.length % 2 != 0)
			throw new RuntimeException("VertexFormat data array length must be even number");

		types = new int[data.length / 2];
		sizes = new int[data.length / 2];

		for (int i = 0; i < data.length; i += 2) {
			types[i / 2] = data[i];
			sizes[i / 2] = data[i + 1];
		}
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

	public void use() {
		int stride = 0;
		for (int i = 0; i < this.types.length; i++) {
			stride += getBytes(types[i], sizes[i]);
		}

		int pointer = 0;
		for (int i = 0; i < this.types.length; i++) {
			glVertexAttribPointer(i, sizes[i], types[i], false, stride, pointer);
			glEnableVertexAttribArray(i);
			pointer += getBytes(types[i], sizes[i]);
		}
	}
}
