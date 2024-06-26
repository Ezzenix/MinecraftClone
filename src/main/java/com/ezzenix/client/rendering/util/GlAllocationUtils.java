package com.ezzenix.client.rendering.util;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class GlAllocationUtils {
	private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);

	public static ByteBuffer allocateByteBuffer(int size) {
		long address = MemoryUtil.nmemAllocChecked(size);
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

