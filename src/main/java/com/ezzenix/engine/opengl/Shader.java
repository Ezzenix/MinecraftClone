package com.ezzenix.engine.opengl;

import com.ezzenix.engine.core.FileUtil;
import org.joml.*;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
	private final int programId;

	public Shader(String vertexShaderPath, String fragmentShaderPath) {
		// create shader program
		programId = glCreateProgram();
		if (programId == 0)
			throw new RuntimeException("Could not create shader");

		// create shaders
		int vertexShader = loadShader(vertexShaderPath, GL_VERTEX_SHADER);
		int fragmentShader = loadShader(fragmentShaderPath, GL_FRAGMENT_SHADER);

		// link
		glLinkProgram(programId);
		if (glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL_FALSE)
			throw new RuntimeException("Could not link shader: " + glGetProgramInfoLog(programId, 1024));

		// validate
		glValidateProgram(programId);
		if (GL20.glGetProgrami(programId, GL20.GL_VALIDATE_STATUS) == GL_FALSE)
			throw new RuntimeException("Error validating shader: " + glGetProgramInfoLog(programId, 1024));

		// cleanup
		glDetachShader(programId, vertexShader);
		glDetachShader(programId, fragmentShader);
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	private int loadShader(String path, int type) {
		String shaderSource = FileUtil.readResourceSource("shaders/" + path);
		if (shaderSource == null)
			throw new RuntimeException("Could not read shader " + path);

		int shaderId = glCreateShader(type);
		if (shaderId == 0)
			throw new RuntimeException("Could not create shader.");

		glShaderSource(shaderId, shaderSource);
		glCompileShader(shaderId);

		if (glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL_FALSE)
			throw new RuntimeException("Could not compile shader: " + glGetShaderInfoLog(shaderId, 1024));

		glAttachShader(this.programId, shaderId);

		return shaderId;
	}

	public void use() {
		glUseProgram(programId);
	}

	public void setUniform(String uniformName, Matrix4f value) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			use();
			FloatBuffer matBuffer = stack.mallocFloat(16);
			value.get(matBuffer);
			glUniformMatrix4fv(glGetUniformLocation(programId, uniformName), false, matBuffer);
		}
	}

	public void setUniform(String uniformName, Matrix3f value) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			use();
			FloatBuffer matBuffer = stack.mallocFloat(9);
			value.get(matBuffer);
			glUniformMatrix3fv(glGetUniformLocation(programId, uniformName), false, matBuffer);
		}
	}

	public void setUniform(String uniformName, Vector4f value) {
		use();
		glUniform4f(glGetUniformLocation(programId, uniformName), value.x, value.y, value.z, value.w);
	}

	public void setUniform(String uniformName, Vector3f value) {
		use();
		glUniform3f(glGetUniformLocation(programId, uniformName), value.x, value.y, value.z);
	}

	public void setUniform(String uniformName, Vector2f value) {
		use();
		glUniform2f(glGetUniformLocation(programId, uniformName), value.x, value.y);
	}

	public void setUniform(String uniformName, float value) {
		use();
		glUniform1f(glGetUniformLocation(programId, uniformName), value);
	}

	public void setUniform(String uniformName, int value) {
		int varLocation = glGetUniformLocation(programId, uniformName);
		use();
		glUniform1i(glGetUniformLocation(programId, uniformName), value);
	}

	public void setUniform(String uniformName, int[] value) {
		use();
		glUniform1iv(glGetUniformLocation(programId, uniformName), value);
	}
}