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
	public int vertexCount;
	public BufferAllocator allocator;
	private boolean isMakingVertex;

	public BufferBuilder(int initialSize) {
		this.vertexCount = 0;
		this.allocator = new BufferAllocator(initialSize);
		this.isMakingVertex = false;
	}

	public ByteBuffer getBuffer() {
		if (this.vertexCount == 0) return null;
		return this.allocator.getAllocated();
	}

	public void clear() {
		this.vertexCount = 0;
		this.allocator.clear();
	}

	public BufferBuilder putFloat(float v) {
		long l = this.allocator.allocate(Float.BYTES);
		MemoryUtil.memPutFloat(l, v);
		return this;
	}

	public BufferBuilder putInt(int v) {
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

	public void close() {
		this.allocator.close();
	}

	public BufferBuilder vertex(float x, float y, float z) {
		checkMakingVertex();
		this.putFloat(x);
		this.putFloat(y);
		this.putFloat(z);
		return this;
	}

	public BufferBuilder vertex(Vector3f vec) {
		return vertex(vec.x, vec.y, vec.z);
	}

	public BufferBuilder vertex(float x, float y) {
		checkMakingVertex();
		this.putFloat(GuiUtil.toNormalizedDeviceCoordinateX(x));
		this.putFloat(GuiUtil.toNormalizedDeviceCoordinateY(y));
		return this;
	}

	public BufferBuilder color(int packedColor) {
		this.putInt(packedColor);
		return this;
	}

	public BufferBuilder color(float r, float g, float b) {
		return this.color(Color.pack(r, g, b, 1));
	}

	public BufferBuilder color(float r, float g, float b, float a) {
		return this.color(Color.pack(r, g, b, a));
	}

	public BufferBuilder texture(float u, float v) {
		this.putFloat(u);
		this.putFloat(v);
		return this;
	}

	public BufferBuilder texture(Vector2f uv) {
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
		protected final Map<RenderLayer, BufferBuilder> builders = Maps.newHashMap();
		protected final Map<RenderLayer, VertexBuffer> buffers = Maps.newHashMap();

		public BufferBuilder getBuilder(RenderLayer layer) {
			BufferBuilder builder = builders.get(layer);
			if (builder != null) return builder;

			builder = new BufferBuilder(layer.getExpectedBufferSize());
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
