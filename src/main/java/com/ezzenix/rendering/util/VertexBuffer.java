package com.ezzenix.rendering.util;

import com.ezzenix.gui.Color;
import com.ezzenix.gui.GuiUtil;
import com.ezzenix.engine.opengl.Shader;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class VertexBuffer {
	private final Usage usage;
	public final Shader shader;
	private int vertexBufferId;
	//private int indexBufferId;
	private int vertexArrayId;

	private int uploadedVertexCount;

	private int vertexCount;
	private final DynamicByteBuffer byteBuffer;

	private boolean isMakingVertex = false;

	public enum Usage {
		STATIC(GL_STATIC_DRAW),
		DYNAMIC(GL_DYNAMIC_DRAW);
		final int id;
		Usage(int id) {
			this.id = id;
		}
	}

	public VertexBuffer(Shader shader, VertexFormat vertexFormat, Usage usage, int initialSize) {
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
		this.byteBuffer = new DynamicByteBuffer(initialSize);
	}

	public VertexBuffer(Shader shader, VertexFormat vertexFormat, Usage usage) {
		this(shader, vertexFormat, usage, 5012);
	}

	public void bind() {
		glBindVertexArray(this.vertexArrayId);
	}

	public void unbind() {
		glBindVertexArray(0);
	}

	public void draw() {
		if (this.uploadedVertexCount == 0) return;

		shader.bind();

		this.bind();
		glDrawArrays(GL_TRIANGLES, 0, this.uploadedVertexCount);
		this.unbind();
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

	public void clear() {
		this.uploadedVertexCount = 0;
		this.reset();
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

	private void checkMakingVertex() {
		if (isMakingVertex) {
			throw new IllegalStateException("Already making vertex, did you forget to call .next()?");
		} else {
			isMakingVertex = true;
		}
	}

	public VertexBuffer vertex(float x, float y, float z) {
		checkMakingVertex();
		this.byteBuffer.putFloat(x);
		this.byteBuffer.putFloat(y);
		this.byteBuffer.putFloat(z);
		return this;
	}

	public VertexBuffer vertex(Vector3f vec) {
		return vertex(vec.x, vec.y, vec.z);
	}

	public VertexBuffer vertex(float x, float y) {
		checkMakingVertex();
		this.byteBuffer.putFloat(GuiUtil.toNormalizedDeviceCoordinateX(x));
		this.byteBuffer.putFloat(GuiUtil.toNormalizedDeviceCoordinateY(y));
		return this;
	}

	public VertexBuffer color(int packedColor) {
		this.byteBuffer.putInt(packedColor);
		return this;
	}

	public VertexBuffer color(float r, float g, float b) {
		return this.color(Color.pack(r, g, b, 1));
	}

	public VertexBuffer color(float r, float g, float b, float a) {
		return this.color(Color.pack(r, g, b, a));
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

	public VertexBuffer putFloat(float v) {
		this.byteBuffer.putFloat(v);
		return this;
	}

	public void next() {
		if (!isMakingVertex)
			throw new IllegalStateException("No vertex was initiated. Call .vertex() before .next()");

		this.vertexCount += 1;
		this.isMakingVertex = false;
	}
}
