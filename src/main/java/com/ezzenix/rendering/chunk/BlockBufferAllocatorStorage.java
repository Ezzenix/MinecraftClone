package com.ezzenix.rendering.chunk;

import com.ezzenix.rendering.util.RenderLayer;
import com.ezzenix.util.BufferAllocator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;

import java.util.List;
import java.util.Map;

public class BlockBufferAllocatorStorage implements AutoCloseable {
	private static final List<RenderLayer> BLOCK_LAYERS = RenderLayer.BLOCK_LAYERS.stream().toList();
	public static final int EXPECTED_TOTAL_SIZE;
	private final Map<RenderLayer, BufferAllocator> allocators;

	public BlockBufferAllocatorStorage() {
		allocators = new Reference2ObjectArrayMap<>(BLOCK_LAYERS.size());

		for (RenderLayer renderLayer : BLOCK_LAYERS) {
			allocators.put(renderLayer, new BufferAllocator(renderLayer.getExpectedBufferSize()));
		}
	}

	public BufferAllocator get(RenderLayer layer) {
		return (BufferAllocator) this.allocators.get(layer);
	}

	public void clear() {
		this.allocators.values().forEach(BufferAllocator::clear);
	}

	public void reset() {
		this.allocators.values().forEach(BufferAllocator::reset);
	}

	public void close() {
		this.allocators.values().forEach(BufferAllocator::close);
	}

	static {
		EXPECTED_TOTAL_SIZE = BLOCK_LAYERS.stream().mapToInt(RenderLayer::getExpectedBufferSize).sum();
	}
}
