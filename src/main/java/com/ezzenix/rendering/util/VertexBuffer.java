package com.ezzenix.rendering.util;

import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.util.BuiltBuffer;

import static org.lwjgl.opengl.GL30.*;

/*
 * Stores vbo and vao
 * Buffer for building vertices
 */
public class VertexBuffer implements AutoCloseable {
	private final Usage usage;
	private int vertexBufferId;
	//private int indexBufferId;
	private int vertexArrayId;

	private int vertexCount;

	private int drawModeId;

	public enum Usage {
		STATIC(GL_STATIC_DRAW),
		DYNAMIC(GL_DYNAMIC_DRAW);
		final int id;
		Usage(int id) {
			this.id = id;
		}
	}

	public VertexBuffer(VertexFormat vertexFormat, Usage usage) {
		this.usage = usage;

		this.vertexBufferId = glGenBuffers();
		//this.indexBufferId = glGenBuffers();
		this.vertexArrayId = glGenVertexArrays();

		this.drawModeId = vertexFormat.getDrawMode().id;

		glBindVertexArray(this.vertexArrayId);
		glBindBuffer(GL_ARRAY_BUFFER, this.vertexBufferId);
		vertexFormat.use();
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);

		this.vertexCount = 0;
	}

	public void bind() {
		glBindVertexArray(this.vertexArrayId);
	}

	public void unbind() {
		glBindVertexArray(0);
	}

	public void draw() {
		if (this.vertexCount == 0) return;
		this.bind();
		glDrawArrays(this.drawModeId, 0, this.vertexCount);
		this.unbind();
	}

	public void draw(Shader shader) {
		shader.bind();
		shader.setUniforms();
		this.draw();
		shader.unbind();
	}

	public void upload(BuiltBuffer builtBuffer) {
		if (this.isClosed())
			throw new IllegalStateException("VertexBuffer is closed");

		this.bind();

		this.vertexCount = builtBuffer.vertexCount;

		glBindBuffer(GL_ARRAY_BUFFER, this.vertexBufferId);
		glBufferData(GL_ARRAY_BUFFER, builtBuffer.getBuffer(), this.usage.id);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		builtBuffer.close();
	}

	public boolean isClosed() {
		return this.vertexBufferId == -1;
	}

	public void close() {
		if (this.vertexBufferId >= 0) {
			glDeleteBuffers(this.vertexBufferId);
			this.vertexBufferId = -1;
		}
		//if (this.indexBufferId >= 0) {
		//	glDeleteBuffers(this.indexBufferId);
		//	this.indexBufferId = -1;
		//}
		if (this.vertexArrayId >= 0) {
			glDeleteVertexArrays(this.vertexArrayId);
			this.vertexArrayId = -1;
		}
	}
}
