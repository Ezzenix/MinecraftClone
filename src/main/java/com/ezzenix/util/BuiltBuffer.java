package com.ezzenix.util;

import com.ezzenix.rendering.util.VertexFormat;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class BuiltBuffer implements AutoCloseable {
	private final BufferAllocator allocator;
	private final int offset;
	private final int size;
	private final int clearCount;
	private boolean closed;
	public int vertexCount;
	public VertexFormat vertexFormat;

	BuiltBuffer(BufferAllocator allocator, int offset, int size, int clearCount) {
		this.allocator = allocator;
		this.offset = offset;
		this.size = size;
		this.clearCount = clearCount;
		this.vertexCount = 0;
	}

	public ByteBuffer getBuffer() {
		if (!allocator.clearCountEquals(this.clearCount)) {
			throw new IllegalStateException("Buffer is no longer valid");
		} else {
			return MemoryUtil.memByteBuffer(allocator.pointer + (long) this.offset, this.size);
		}
	}

	public void close() {
		if (!this.closed) {
			this.closed = true;
			if (allocator.clearCountEquals(this.clearCount)) {
				allocator.clearIfUnreferenced();
			}
		}
	}
}
