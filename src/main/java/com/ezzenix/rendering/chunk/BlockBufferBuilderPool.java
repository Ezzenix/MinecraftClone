package com.ezzenix.rendering.chunk;

import com.ezzenix.Client;
import com.google.common.collect.Queues;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class BlockBufferBuilderPool {
	private final Queue<BlockBufferAllocatorStorage> availableBuilders;
	private volatile int availableBuilderCount;

	private BlockBufferBuilderPool(List<BlockBufferAllocatorStorage> availableBuilders) {
		this.availableBuilders = Queues.newArrayDeque(availableBuilders);
		this.availableBuilderCount = this.availableBuilders.size();
	}

	public static BlockBufferBuilderPool allocate(int max) {
		int i = Math.max(1, (int) ((double) Runtime.getRuntime().maxMemory() * 0.3) / BlockBufferAllocatorStorage.EXPECTED_TOTAL_SIZE);
		int j = Math.max(1, Math.min(max, i));
		List<BlockBufferAllocatorStorage> list = new ArrayList<>(j);

		try {
			for (int k = 0; k < j; ++k) {
				list.add(new BlockBufferAllocatorStorage());
			}
		} catch (OutOfMemoryError var7) {
			Client.LOGGER.warn("Allocated only {}/{} buffers", (Object) list.size(), (Object) j);
			int l = Math.min(list.size() * 2 / 3, list.size() - 1);

			for (int m = 0; m < l; ++m) {
				(list.removeLast()).close();
			}
		}

		return new BlockBufferBuilderPool(list);
	}

	@Nullable
	public BlockBufferAllocatorStorage acquire() {
		BlockBufferAllocatorStorage blockBufferAllocatorStorage = this.availableBuilders.poll();
		if (blockBufferAllocatorStorage != null) {
			this.availableBuilderCount = this.availableBuilders.size();
			return blockBufferAllocatorStorage;
		} else {
			return null;
		}
	}

	public void release(BlockBufferAllocatorStorage builders) {
		this.availableBuilders.add(builders);
		this.availableBuilderCount = this.availableBuilders.size();
	}

	public boolean hasNoAvailableBuilder() {
		return this.availableBuilders.isEmpty();
	}

	public int getAvailableBuilderCount() {
		return this.availableBuilderCount;
	}
}