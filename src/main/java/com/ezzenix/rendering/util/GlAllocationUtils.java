package com.ezzenix.rendering.util;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class GlAllocationUtils {
	private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);

	public static ByteBuffer allocateByteBuffer(int size) {
		if (size <= 0)
			throw new IllegalArgumentException("Invalid buffer size: " + size);

		long address = ALLOCATOR.malloc(size);
		if (address == 0L)
			throw new OutOfMemoryError("Failed to allocate " + size + " bytes");

		return MemoryUtil.memByteBuffer(address, size);
	}

	public static ByteBuffer resizeByteBuffer(ByteBuffer source, int size) {
		ByteBuffer newBuffer = allocateByteBuffer(size);
		newBuffer.put(source.flip());
		free(source);
		return newBuffer;
	}

	public static void free(ByteBuffer buf) {
		ALLOCATOR.free(MemoryUtil.memAddress0(buf));
	}
}

