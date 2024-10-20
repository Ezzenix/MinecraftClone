package com.ezzenix.util;

import com.ezzenix.Client;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public class BufferAllocator implements AutoCloseable {
	private static final MemoryUtil.MemoryAllocator allocator = MemoryUtil.getAllocator(false);
	long pointer;
	private int size;
	private int offset;
	private int prevOffset;
	private int refCount;
	private int clearCount;

	public BufferAllocator(int size) {
		this.size = size;
		this.pointer = allocator.malloc((long) size);
		if (this.pointer == 0L) {
			throw new OutOfMemoryError("Failed to allocate " + size + " bytes");
		}
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
		this.pointer = allocator.realloc(this.pointer, (long) targetSize);
		Client.LOGGER.info("Needed to grow buffer from {} bytes to {} bytes", this.size, targetSize);
		if (this.pointer == 0L) {
			throw new OutOfMemoryError("Failed to resize buffer from " + this.size + " bytes to " + targetSize + " bytes");
		} else {
			this.size = targetSize;
		}
	}

	@Nullable
	public BuiltBuffer getAllocated() {
		this.ensureNotClosed();
		int i = this.prevOffset;
		int j = this.offset - i;
		if (j == 0) {
			return null;
		} else {
			this.prevOffset = this.offset;
			++this.refCount;
			return new BuiltBuffer(this, i, j, this.clearCount);
		}
	}

	public void clear() {
		if (this.refCount > 0) {
			Client.LOGGER.warn("Clearing BufferBuilder with unused batches");
		}

		this.reset();
	}

	public void reset() {
		this.ensureNotClosed();
		if (this.refCount > 0) {
			this.forceClear();
			this.refCount = 0;
		}
	}

	boolean clearCountEquals(int clearCount) {
		return clearCount == this.clearCount;
	}

	void clearIfUnreferenced() {
		if (--this.refCount <= 0) {
			this.forceClear();
		}
	}

	private void forceClear() {
		int i = this.offset - this.prevOffset;
		if (i > 0) {
			MemoryUtil.memCopy(this.pointer + (long) this.prevOffset, this.pointer, (long) i);
		}

		this.offset = i;
		this.prevOffset = 0;
		++this.clearCount;
	}

	public void close() {
		if (this.pointer != 0L) {
			allocator.free(this.pointer);
			this.pointer = 0L;
			this.clearCount = -1;
		}
	}

	public void ensureNotClosed() {
		if (this.pointer == 0L) {
			throw new IllegalStateException("Allocator is closed");
		}
	}
}
