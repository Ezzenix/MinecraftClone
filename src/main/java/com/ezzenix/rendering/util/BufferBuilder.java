package com.ezzenix.rendering.util;

import com.ezzenix.gui.Color;
import com.ezzenix.gui.GuiUtil;
import com.ezzenix.util.BufferAllocator;
import com.google.common.collect.Maps;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Map;

public class BufferBuilder {
	public final BufferAllocator allocator;
	private final VertexFormat vertexFormat;
	public int vertexCount;
	private boolean isMakingVertex;
	private long pointer;

	public BufferBuilder(int initialSize, VertexFormat vertexFormat) {
		this.vertexCount = 0;
		this.allocator = new BufferAllocator(initialSize);
		this.isMakingVertex = false;
		this.vertexFormat = vertexFormat;
		this.pointer = 0;
	}

	public BufferBuilder(RenderLayer layer) {
		this(layer.getExpectedBufferSize(), layer.getVertexFormat());
	}

	public ByteBuffer getBuffer() {
		endVertex();
		if (vertexCount == 0) return null;
		return allocator.getAllocated();
	}

	public void clear() {
		vertexCount = 0;
		allocator.clear();
	}

	public void close() {
		allocator.close();
	}

	public void endVertex() {
		if (!isMakingVertex) return;

		vertexCount += 1;
		isMakingVertex = false;
	}

	private void beginVertex() {
		endVertex();
		isMakingVertex = true;
		pointer = allocator.allocate(vertexFormat.getVertexSizeBytes());
	}

	public BufferBuilder putFloat(float v) {
		MemoryUtil.memPutFloat(pointer, v);
		pointer += 4L;
		return this;
	}

	public BufferBuilder putInt(int v) {
		MemoryUtil.memPutInt(pointer, v);
		pointer += 4L;
		return this;
	}

	public BufferBuilder vertex(float x, float y, float z) {
		beginVertex();
		putFloat(x);
		putFloat(y);
		putFloat(z);
		return this;
	}

	public BufferBuilder vertex(Vector3f vec) {
		return vertex(vec.x, vec.y, vec.z);
	}

	public BufferBuilder vertex(float x, float y) {
		beginVertex();
		putFloat(GuiUtil.toNormalizedDeviceCoordinateX(x));
		putFloat(GuiUtil.toNormalizedDeviceCoordinateY(y));
		return this;
	}

	public BufferBuilder color(int packedColor) {
		putInt(packedColor);
		return this;
	}

	public BufferBuilder color(float r, float g, float b) {
		return this.color(Color.pack(r, g, b, 1));
	}

	public BufferBuilder color(float r, float g, float b, float a) {
		return this.color(Color.pack(r, g, b, a));
	}

	public BufferBuilder texture(float u, float v) {
		putFloat(u);
		putFloat(v);
		return this;
	}

	public BufferBuilder texture(Vector2f uv) {
		putFloat(uv.x);
		putFloat(uv.y);
		return this;
	}

	// Immediate
	public static class Immediate {
		protected final Map<RenderLayer, BufferBuilder> builders = Maps.newHashMap();
		protected final Map<RenderLayer, VertexBuffer> buffers = Maps.newHashMap();

		public BufferBuilder getBuilder(RenderLayer layer) {
			BufferBuilder builder = builders.get(layer);
			if (builder != null) return builder;

			builder = new BufferBuilder(layer);
			builders.put(layer, builder);
			return builder;
		}

		public void draw() {
			for (RenderLayer renderLayer : this.buffers.keySet()) {
				this.draw(renderLayer);
			}
		}

		public void draw(RenderLayer layer) {
			BufferBuilder builder = builders.get(layer);
			if (builder == null) return;

			VertexBuffer buffer = buffers.computeIfAbsent(layer, v -> new VertexBuffer(layer.getVertexFormat(), VertexBuffer.Usage.DYNAMIC));

			buffer.upload(builder);
			layer.draw(buffer);
		}
	}
}
