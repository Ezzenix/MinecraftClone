package com.ezzenix.client.rendering.util;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class GlAllocationUtils {
	private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);

	public static ByteBuffer allocateByteBuffer(int size) {
		long l = ALLOCATOR.malloc(size);
		if (l == 0L) {
			throw new OutOfMemoryError("Failed to allocate " + size + " bytes");
		}
		return MemoryUtil.memByteBuffer(l, size);
	}

	public static ByteBuffer resizeByteBuffer(ByteBuffer source, int size) {
		/*
		int pos = source.position();

		ByteBuffer newBuffer = allocateByteBuffer(size);
		newBuffer.put(source);
		newBuffer.position(pos);

		free(source);

		return newBuffer;
		 */

		long adr = MemoryUtil.memAddress0(source);
		long l = ALLOCATOR.realloc(adr, size);

		if (l == 0L) {
			throw new OutOfMemoryError("Failed to resize buffer from " + source.capacity() + " bytes to " + size + " bytes");
		}
		return MemoryUtil.memByteBuffer(l, size);
	}

	public static void free(ByteBuffer buf) {
		ALLOCATOR.free(MemoryUtil.memAddress0(buf));
	}
}

