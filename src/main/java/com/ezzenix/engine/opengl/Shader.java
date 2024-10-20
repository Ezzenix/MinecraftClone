package com.ezzenix.engine.opengl;

import com.ezzenix.Client;
import com.ezzenix.engine.Scheduler;
import com.ezzenix.resource.ResourceManager;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL30.*;

public class Shader {
	private final int programId;
	private final HashMap<String, Uniform> uniforms = new HashMap<>();
	private final Texture[] samplers = new Texture[3];

	private final Uniform uniformViewMatrix;
	private final Uniform uniformProjectionMatrix;
	private final Uniform uniformGameTime;
	private final Uniform uniformModelMatrix;

	public Shader(String vertexShaderPath, String fragmentShaderPath) {
		// create shader program
		programId = glCreateProgram();
		if (programId == 0)
			throw new RuntimeException("Could not create shader");

		vertexShaderPath = "core/" + vertexShaderPath;
		fragmentShaderPath = "core/" + fragmentShaderPath;

		// create shaders
		int vertexShader = loadShader(vertexShaderPath, GL_VERTEX_SHADER);
		int fragmentShader = loadShader(fragmentShaderPath, GL_FRAGMENT_SHADER);

		// link
		glLinkProgram(programId);
		if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE)
			throw new RuntimeException("Could not link shader: " + glGetProgramInfoLog(programId, 1024));

		// validate
		glValidateProgram(programId);
		if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE)
			throw new RuntimeException("Error validating shader: " + glGetProgramInfoLog(programId, 1024));

		// load uniforms
		initializeUniforms(vertexShaderPath);
		initializeUniforms(fragmentShaderPath);
		this.uniformViewMatrix = getUniform("viewMatrix");
		this.uniformProjectionMatrix = getUniform("projectionMatrix");
		this.uniformGameTime = getUniform("gameTime");
		this.uniformModelMatrix = getUniform("modelMatrix");

		// cleanup
		glDetachShader(programId, vertexShader);
		glDetachShader(programId, fragmentShader);
		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	public Shader(String path) {
		this(path + ".vert", path + ".frag");
	}

	private String readShader(String shaderPath) {
		String shaderSource = ResourceManager.readFile("shaders/" + shaderPath);
		if (shaderSource == null)
			throw new RuntimeException("Could not read shader " + shaderPath);

		// process includes
		StringBuilder processedSource = new StringBuilder();
		try {
			Pattern pattern = Pattern.compile("#include \"(.*)\"");
			Matcher matcher = pattern.matcher(shaderSource);
			while (matcher.find()) {
				String includeFilePath = "include/" + matcher.group(1);
				String includeSource = readShader(includeFilePath); // Recursively process includes
				matcher.appendReplacement(processedSource, includeSource);
			}
			matcher.appendTail(processedSource);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return processedSource.toString();
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
						Uniform uniform = new Uniform(location);
						this.uniforms.put(uniformName, uniform);
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

		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
			System.out.println(shaderSource);
			throw new RuntimeException("Could not compile shader " + path + ": " + glGetShaderInfoLog(shaderId, 1024));
		}

		glAttachShader(this.programId, shaderId);

		return shaderId;
	}

	public Uniform getUniform(String name) {
		return this.uniforms.get(name);
	}

	public void setTexture(int slot, Texture texture) {
		if (slot < 0 || slot >= this.samplers.length)
			throw new RuntimeException("Sampler slot " + slot + " is not a valid slot");

		if (this.samplers[slot] == null) {
			this.bind();
			Uniform uniform = getUniform("sampler" + slot);
			if (uniform != null) {
				uniform.set(slot);
			}
		}

		this.samplers[slot] = texture;
	}

	public void bind() {
		glUseProgram(programId);

		for (int i = 0; i < this.samplers.length; i++) {
			if (this.samplers[i] != null) {
				glActiveTexture(GL_TEXTURE0 + i);
				this.samplers[i].bind();
			}
		}
	}

	public void unbind() {
		glUseProgram(0);
	}

	public void setModelMatrix(Matrix4f value) {
		if (this.uniformModelMatrix != null) {
			this.uniformModelMatrix.set(value);
		}
	}

	public void setUniforms(Matrix4f modelMatrix) {
		if (this.uniformProjectionMatrix != null) {
			this.uniformProjectionMatrix.set(Client.getCamera().getProjectionMatrix());
		}
		if (this.uniformViewMatrix != null) {
			this.uniformViewMatrix.set(Client.getCamera().getViewMatrix());
		}
		if (this.uniformModelMatrix != null && modelMatrix != null) {
			this.uniformModelMatrix.set(modelMatrix);
		}
		if (this.uniformGameTime != null) {
			this.uniformGameTime.set(Scheduler.getClock());
		}
	}

	public void setUniforms() {
		setUniforms(null);
	}

	public void cleanup() {
		glUseProgram(0);
		glDeleteProgram(programId);
		uniforms.clear();
	}
}