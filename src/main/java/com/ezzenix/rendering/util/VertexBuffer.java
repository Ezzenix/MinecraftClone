package com.ezzenix.rendering.util;

import com.ezzenix.Client;
import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.gui.Color;
import com.ezzenix.gui.GuiUtil;
import com.ezzenix.util.BufferAllocator;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

/*
 * Stores vbo and vao
 * Buffer for building vertices
 */
public class VertexBuffer implements AutoCloseable {
	private int vertexBufferId;
	//private int indexBufferId;
	private int vertexArrayId;

	public int vertexCount;
	private Usage usage;
	private int drawModeId;
	private boolean readyToDraw;

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

		this.vertexCount = 0;
		this.usage = usage;
		this.drawModeId = vertexFormat.getDrawMode().id;
		this.readyToDraw = false;

		glBindVertexArray(this.vertexArrayId);
		glBindBuffer(GL_ARRAY_BUFFER, this.vertexBufferId);
		vertexFormat.use();
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);
	}

	public void bind() {
		glBindVertexArray(this.vertexArrayId);
	}

	public void unbind() {
		glBindVertexArray(0);
	}

	public void draw() {
		if (!this.readyToDraw) return;
		this.ensureNotClosed();
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

	public void upload(BufferBuilder builder) {
		this.ensureNotClosed();

		ByteBuffer buffer = builder.getBuffer();
		if (buffer == null) {
			//Client.LOGGER.warn("Buffer is null");
			this.readyToDraw = false;
			return;
		}

		this.vertexCount = builder.vertexCount;

		this.bind();

		glBindBuffer(GL_ARRAY_BUFFER, this.vertexBufferId);
		glBufferData(GL_ARRAY_BUFFER, buffer, this.usage.id);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		this.readyToDraw = true;
		builder.clear();
	}

	public boolean isClosed() {
		return this.vertexBufferId == -1;
	}

	public void ensureNotClosed() {
		if (this.isClosed())
			throw new IllegalStateException("VertexBuffer is closed");
	}

	public void clear() {
		this.vertexCount = 0;
	}

	public void close() {
		this.clear();

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
