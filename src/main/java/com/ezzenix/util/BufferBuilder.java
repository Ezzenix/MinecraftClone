package com.ezzenix.util;

import com.ezzenix.gui.Color;
import com.ezzenix.gui.GuiUtil;
import com.ezzenix.rendering.util.RenderLayer;
import com.ezzenix.rendering.util.VertexBuffer;
import com.ezzenix.rendering.util.VertexFormat;
import com.google.common.collect.Maps;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.util.Map;

public class BufferBuilder {
	private final BufferAllocator allocator;
	private final VertexFormat vertexFormat;

	public int vertexCount = 0;
	private boolean isMakingVertex = false;
	private boolean building = true;

	public BufferBuilder(BufferAllocator allocator, VertexFormat format) {
		this.allocator = allocator;
		this.vertexFormat = format;
		this.allocator.ensureNotClosed();
	}

	public BuiltBuffer end() {
		this.ensureBuilding();
		BuiltBuffer buffer = this.build();
		this.building = false;
		return buffer;
	}

	private void ensureBuilding() {
		if (!this.building) {
			throw new IllegalStateException("Not building!");
		}
	}

	private BuiltBuffer build() {
		if (this.vertexCount == 0) {
			return null;
		} else {
			BuiltBuffer builtBuffer = this.allocator.getAllocated();
			if (builtBuffer == null) return null;
			builtBuffer.vertexCount = this.vertexCount;
			builtBuffer.vertexFormat = this.vertexFormat;
			return builtBuffer;
		}
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


	public static class Immediate {
		protected final Map<RenderLayer, BufferAllocator> allocators = Maps.newHashMap();
		protected final Map<RenderLayer, BufferBuilder> builders = Maps.newHashMap();
		protected final Map<RenderLayer, VertexBuffer> buffers = Maps.newHashMap();

		public BufferBuilder getBuilder(RenderLayer layer) {
			BufferBuilder builder = builders.get(layer);
			if (builder != null) return builder;

			BufferAllocator allocator = allocators.computeIfAbsent(layer, v -> new BufferAllocator(5012));
			buffers.computeIfAbsent(layer, v -> new VertexBuffer(v.getVertexFormat(), VertexBuffer.Usage.STATIC));

			builder = new BufferBuilder(allocator, layer.getVertexFormat());
			builders.put(layer, builder);
			return builder;
		}

		public void draw() {
			for (RenderLayer renderLayer : this.builders.keySet()) {
				this.draw(renderLayer);
			}
		}

		public void draw(RenderLayer layer) {
			BufferBuilder builder = this.builders.remove(layer);
			if (builder == null) return;

			VertexBuffer buffer = buffers.get(layer);
			BuiltBuffer builtBuffer = builder.end();

			if (builtBuffer != null && buffer != null) {
				buffer.upload(builtBuffer);
				layer.draw(buffer);
			}

			allocators.get(layer).reset();
		}
	}
}
