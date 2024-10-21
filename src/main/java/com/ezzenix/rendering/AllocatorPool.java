package com.ezzenix.rendering;

import com.ezzenix.util.BufferAllocator;
import com.google.common.collect.Queues;

import java.util.Queue;

public class AllocatorPool {
	private static final Queue<BufferAllocator> availableBuffers = Queues.newArrayDeque();

	public static BufferAllocator acquire() {
		BufferAllocator buffer = availableBuffers.poll();
		if (buffer != null) {
			return buffer;
		} else {
			return new BufferAllocator(400000);
		}
	}

	public static void release(BufferAllocator buffer) {
		buffer.clear();
		availableBuffers.add(buffer);
	}
}