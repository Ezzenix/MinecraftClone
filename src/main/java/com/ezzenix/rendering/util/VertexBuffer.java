package com.ezzenix.rendering.util;

import com.ezzenix.engine.opengl.Shader;
import com.ezzenix.gui.Color;
import com.ezzenix.gui.GuiUtil;
import com.ezzenix.util.BufferAllocator;
import com.google.common.collect.Maps;
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
	private final Usage usage;
	private int vertexBufferId;
	//private int indexBufferId;
	private int vertexArrayId;

	private int vertexCount;
	private int drawModeId;
	public BufferAllocator allocator;
	private boolean isMakingVertex;

	public enum Usage {
		STATIC(GL_STATIC_DRAW),
		DYNAMIC(GL_DYNAMIC_DRAW);
		final int id;
		Usage(int id) {
			this.id = id;
		}
	}

	public VertexBuffer(VertexFormat vertexFormat, Usage usage, int initialSize) {
		this.usage = usage;

		this.vertexBufferId = glGenBuffers();
		//this.indexBufferId = glGenBuffers();
		this.vertexArrayId = glGenVertexArrays();

		this.vertexCount = 0;
		this.drawModeId = vertexFormat.getDrawMode().id;
		this.allocator = new BufferAllocator(initialSize);
		this.isMakingVertex = false;

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
		this.ensureNotClosed();
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

	public void upload() {
		this.ensureNotClosed();

		ByteBuffer buffer = allocator.getAllocated();
		if (buffer == null)
			throw new IllegalStateException("Buffer is null");

		this.bind();

		glBindBuffer(GL_ARRAY_BUFFER, this.vertexBufferId);
		glBufferData(GL_ARRAY_BUFFER, buffer, this.usage.id);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		allocator.clear();
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
		this.allocator.clear();
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

	public VertexBuffer putFloat(float v) {
		long l = this.allocator.allocate(Float.BYTES);
		MemoryUtil.memPutFloat(l, v);
		return this;
	}

	public VertexBuffer putInt(int v) {
		long l = this.allocator.allocate(Integer.BYTES);
		MemoryUtil.memPutInt(l, v);
		return this;
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
		this.putFloat(x);
		this.putFloat(y);
		this.putFloat(z);
		return this;
	}

	public VertexBuffer vertex(Vector3f vec) {
		return vertex(vec.x, vec.y, vec.z);
	}

	public VertexBuffer vertex(float x, float y) {
		checkMakingVertex();
		this.putFloat(GuiUtil.toNormalizedDeviceCoordinateX(x));
		this.putFloat(GuiUtil.toNormalizedDeviceCoordinateY(y));
		return this;
	}

	public VertexBuffer color(int packedColor) {
		this.putInt(packedColor);
		return this;
	}

	public VertexBuffer color(float r, float g, float b) {
		return this.color(Color.pack(r, g, b, 1));
	}

	public VertexBuffer color(float r, float g, float b, float a) {
		return this.color(Color.pack(r, g, b, a));
	}

	public VertexBuffer texture(float u, float v) {
		this.putFloat(u);
		this.putFloat(v);
		return this;
	}

	public VertexBuffer texture(Vector2f uv) {
		this.putFloat(uv.x);
		this.putFloat(uv.y);
		return this;
	}

	public void next() {
		if (!isMakingVertex)
			throw new IllegalStateException("No vertex was initiated. Call .vertex() before .next()");

		this.vertexCount += 1;
		this.isMakingVertex = false;
	}



	// Immediate
	public static class Immediate {
		protected final Map<RenderLayer, VertexBuffer> buffers = Maps.newHashMap();

		public VertexBuffer getBuffer(RenderLayer layer) {
			VertexBuffer buffer = buffers.get(layer);
			if (buffer != null) return buffer;

			buffer = new VertexBuffer(layer.getVertexFormat(), Usage.DYNAMIC, layer.getExpectedBufferSize());
			buffers.put(layer, buffer);
			return buffer;
		}

		public void draw() {
			for (RenderLayer renderLayer : this.buffers.keySet()) {
				this.draw(renderLayer);
			}
		}

		public void draw(RenderLayer layer) {
			VertexBuffer buffer = buffers.get(layer);
			if (buffer == null) return;

			buffer.upload();
			layer.draw(buffer);
		}
	}
}
