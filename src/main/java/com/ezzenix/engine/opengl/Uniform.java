package com.ezzenix.engine.opengl;

import org.joml.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform1iv;

public class Uniform {
	private final int location;

	private final FloatBuffer mat4Buffer;

	public Uniform(int location) {
		this.location = location;
		this.mat4Buffer = MemoryUtil.memAllocFloat(16);
	}

	public void set(Matrix4f value) {
		if (value == null)
			throw new IllegalArgumentException("Attempt to set uniform to null");

		mat4Buffer.clear();
		value.get(mat4Buffer);
		glUniformMatrix4fv(this.location, false, mat4Buffer);
	}

	public void set(Matrix3f value) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer matBuffer = stack.mallocFloat(9);
			value.get(matBuffer);
			glUniformMatrix3fv(this.location, false, matBuffer);
		}
	}

	public void set(Vector4f value) {
		glUniform4f(this.location, value.x, value.y, value.z, value.w);
	}

	public void set(Vector3f value) {
		glUniform3f(this.location, value.x, value.y, value.z);
	}

	public void set(Vector2f value) {
		glUniform2f(this.location, value.x, value.y);
	}

	public void set(float value) {
		glUniform1f(this.location, value);
	}

	public void set(int value) {
		glUniform1i(this.location, value);
	}

	public void set(int[] value) {
		glUniform1iv(this.location, value);
	}
}
