package com.ezzenix.util;

import com.ezzenix.Client;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class BufferAllocator implements AutoCloseable {
	private static final MemoryUtil.MemoryAllocator allocator = MemoryUtil.getAllocator(false);
	private long pointer;
	private int size;
	private int offset;
	private int prevOffset;

	public BufferAllocator(int size) {
		if (size < 0)
			throw new IllegalArgumentException("Invalid allocator size " + size);

		this.size = size;
		this.pointer = allocator.malloc(size);
		if (this.pointer == 0L)
			throw new OutOfMemoryError("Failed to allocate " + size + " bytes");
	}

	public long allocate(int size) {
		ensureNotClosed();
		int i = this.offset;
		int j = i + size;
		this.growIfNecessary(j);
		this.offset = j;
		return this.pointer + (long) i;
	}

	private void growIfNecessary(int targetSize) {
		if (targetSize > this.size) {
			int i = Math.min(this.size, 2097152);
			int j = Math.max(this.size + i, targetSize);
			this.grow(j);
		}
	}

	private void grow(int targetSize) {
		Client.LOGGER.info("Needed to grow buffer from {} bytes to {} bytes", this.size, targetSize);
		this.pointer = allocator.realloc(this.pointer, (long) targetSize);
		if (this.pointer == 0L) {
			throw new OutOfMemoryError("Failed to resize buffer from " + this.size + " bytes to " + targetSize + " bytes");
		} else {
			this.size = targetSize;
		}
	}

	// Careful use
	public long getPointer() {
		return this.pointer;
	}

	@Nullable
	public ByteBuffer getAllocated() {
		this.ensureNotClosed();
		int i = this.prevOffset;
		int j = this.offset - i;
		if (j == 0) {
			return null;
		} else {
			return MemoryUtil.memByteBuffer(pointer + (long) i, j);
		}
	}

	public void clear() {
		this.ensureNotClosed();
		this.forceClear();
	}

	private void forceClear() {
		int i = this.offset - this.prevOffset;
		if (i > 0) {
			MemoryUtil.memCopy(this.pointer + (long) this.prevOffset, this.pointer, (long) i);
		}

		this.offset = 0;
		this.prevOffset = 0;
	}

	public void close() {
		if (this.pointer != 0L) {
			allocator.free(this.pointer);
			this.pointer = 0L;
		}
	}

	public void ensureNotClosed() {
		if (this.pointer == 0L) {
			throw new IllegalStateException("Allocator is closed");
		}
	}
}
