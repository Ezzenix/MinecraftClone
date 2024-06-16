package com.ezzenix.client.rendering.util;

import com.ezzenix.client.gui.library.GuiUtil;
import com.ezzenix.engine.opengl.Shader;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.ARBBufferStorage.GL_MAP_COHERENT_BIT;
import static org.lwjgl.opengl.ARBBufferStorage.GL_MAP_PERSISTENT_BIT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class VertexBuffer {
	private final Usage usage;
	private final Shader shader;
	private int vertexBufferId;
	//private int indexBufferId;
	private int vertexArrayId;

	private int uploadedVertexCount;

	private int vertexCount;
	private final DynamicByteBuffer byteBuffer;

	public enum Usage {
		STATIC(GL_STATIC_DRAW),
		DYNAMIC(GL_DYNAMIC_DRAW);
		final int id;
		Usage(int id) {
			this.id = id;
		}
	}

	public VertexBuffer(Shader shader, VertexFormat vertexFormat, Usage usage) {
		this.shader = shader;
		this.usage = usage;

		this.vertexBufferId = glGenBuffers();
		//this.indexBufferId = glGenBuffers();
		this.vertexArrayId = glGenVertexArrays();

		glBindVertexArray(this.vertexArrayId);
		glBindBuffer(GL_ARRAY_BUFFER, this.vertexBufferId);
		vertexFormat.use();
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindVertexArray(0);

		this.vertexCount = 0;
		this.byteBuffer = new DynamicByteBuffer(5012);
	}

	public void bind() {
		glBindVertexArray(this.vertexArrayId);
	}

	public void unbind() {
		glBindVertexArray(0);
	}

	public void uploadAndDraw() {
		this.upload();
		this.draw();
	}

	public void draw(boolean uploadFirst) {
		if (uploadFirst) {
			this.upload();
		}

		if (this.uploadedVertexCount == 0) return;

		shader.bind();
		this.bind();
		glDrawArrays(GL_TRIANGLES, 0, this.uploadedVertexCount);
		this.unbind();
	}

	public void draw() {
		this.draw(false);
	}

	public void upload() {
		if (vertexCount == 0) {
			if (this.uploadedVertexCount != 0) {
				this.reset();
				this.uploadedVertexCount = 0;
			}
			return;
		}

		this.bind();

		ByteBuffer buffer = this.byteBuffer.end();
		glBindBuffer(GL_ARRAY_BUFFER, this.vertexBufferId);
		glBufferData(GL_ARRAY_BUFFER, buffer, this.usage.id);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		this.uploadedVertexCount = this.vertexCount;
		this.reset();
	}

	public void reset() {
		this.byteBuffer.clear();
		this.vertexCount = 0;
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

	public VertexBuffer vertex(float x, float y, float z) {
		this.byteBuffer.putFloat(x);
		this.byteBuffer.putFloat(y);
		this.byteBuffer.putFloat(z);
		return this;
	}

	public VertexBuffer vertex(float x, float y) {
		this.byteBuffer.putFloat(GuiUtil.toNormalizedDeviceCoordinateX(x));
		this.byteBuffer.putFloat(GuiUtil.toNormalizedDeviceCoordinateY(y));
		return this;
	}

	public VertexBuffer color(float r, float g, float b) {
		this.byteBuffer.putFloat(r);
		this.byteBuffer.putFloat(g);
		this.byteBuffer.putFloat(b);
		return this;
	}

	public VertexBuffer color(float r, float g, float b, float a) {
		this.byteBuffer.putFloat(r);
		this.byteBuffer.putFloat(g);
		this.byteBuffer.putFloat(b);
		this.byteBuffer.putFloat(a);
		return this;
	}

	public VertexBuffer texture(float u, float v) {
		this.byteBuffer.putFloat(u);
		this.byteBuffer.putFloat(v);
		return this;
	}

	public VertexBuffer texture(Vector2f uv) {
		this.byteBuffer.putFloat(uv.x);
		this.byteBuffer.putFloat(uv.y);
		return this;
	}

	public void next() {
		this.vertexCount += 1;
	}
}
