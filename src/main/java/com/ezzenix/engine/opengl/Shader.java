package com.ezzenix.engine.opengl;

import com.ezzenix.engine.core.FileUtil;
import org.joml.*;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;

public class Shader {
	private final int programId;
	private final HashMap<String, Integer> uniformLocations = new HashMap<>();

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

		initializeUniforms(vertexShaderPath);
		initializeUniforms(fragmentShaderPath);

		// cleanup
		glDetachShader(programId, vertexShader);
		glDetachShader(programId, fragmentShader);
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	private String readShader(String shaderPath) {
		String shaderSource = FileUtil.readResourceSource("shaders/" + shaderPath);
		if (shaderSource == null)
			throw new RuntimeException("Could not read shader " + shaderPath);
		return shaderSource;
	}

	private void initializeUniforms(String shaderPath) {
		String shaderSource = readShader(shaderPath);

		String[] lines = shaderSource.split("\n");
		for (String line : lines) {
			if (line.startsWith("uniform")) {
				String[] tokens = line.split("\\s+");
				if (tokens.length >= 3) {
					String uniformName = tokens[2].replaceAll(";", "");
					int location = glGetUniformLocation(programId, uniformName);
					if (location != -1) {
						uniformLocations.put(uniformName, location);
						//System.out.println("Added uniform " + uniformName + " at location " + location);
					} else {
						System.err.println("Uniform " + uniformName + " is defined in shader/" + shaderPath + " but is never used");
					}
				}
			}
		}
	}

	private int loadShader(String path, int type) {
		String shaderSource = readShader(path);

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

	public void bind() {
		glUseProgram(programId);
	}

	public int getLocation(String uniformName) {
		return uniformLocations.get(uniformName);
	}

	public void setUniform(String uniformName, Matrix4f value) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer matBuffer = stack.mallocFloat(16);
			value.get(matBuffer);
			glUniformMatrix4fv(getLocation(uniformName), false, matBuffer);
		}
	}

	public void setUniform(String uniformName, Matrix3f value) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer matBuffer = stack.mallocFloat(9);
			value.get(matBuffer);
			glUniformMatrix3fv(getLocation(uniformName), false, matBuffer);
		}
	}

	public void setUniform(String uniformName, Vector4f value) {
		glUniform4f(getLocation(uniformName), value.x, value.y, value.z, value.w);
	}

	public void setUniform(String uniformName, Vector3f value) {
		glUniform3f(getLocation(uniformName), value.x, value.y, value.z);
	}

	public void setUniform(String uniformName, Vector2f value) {
		glUniform2f(getLocation(uniformName), value.x, value.y);
	}

	public void setUniform(String uniformName, float value) {
		glUniform1f(getLocation(uniformName), value);
	}

	public void setUniform(String uniformName, int value) {
		glUniform1i(getLocation(uniformName), value);
	}

	public void setUniform(String uniformName, int[] value) {
		glUniform1iv(getLocation(uniformName), value);
	}

	public void cleanup() {
		glUseProgram(0);
		glDeleteProgram(programId);
		uniformLocations.clear();
	}
}