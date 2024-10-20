package com.ezzenix.world.chunk;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Arrays;
import java.util.List;

public class PalettedContainer<T> {
	private final Palette<T> palette = new Palette<>();
	private final int[] data;
	private final int width;
	private final int height;

	public PalettedContainer(int width, int height, T defaultValue) {
		this.width = width;
		this.height = height;
		this.data = new int[width * width * height];

		Arrays.fill(data, palette.getOrCreateValueIndex(defaultValue));
	}

	public void set(int x, int y, int z, T value) {
		int index = getIndex(x, y, z);
		data[index] = palette.getOrCreateValueIndex(value);
	}

	public T get(int x, int y, int z) {
		int index = getIndex(x, y, z);
		return palette.getValueByIndex(data[index]);
	}

	private int getIndex(int x, int y, int z) {
		if (x < 0 || x >= width || y < 0 || y > height || z < 0 || z >= width)
			throw new IndexOutOfBoundsException("Invalid position: (" + x + ", " + y + ", " + z + ")");

		return x + width * (y + height * z);
	}

	public List<T> getValues() {
		return this.palette.getValues();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < height; y++) {
			sb.append("Layer ").append(y).append(":\n");
			for (int z = 0; z < width; z++) {
				for (int x = 0; x < width; x++) {
					sb.append(get(x, y, z)).append(" ");
				}
				sb.append("\n");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	private static class Palette<T> {
		private final ObjectArrayList<T> values = new ObjectArrayList<>(); // ObjectArrayList
		private final Object2IntOpenHashMap<T> valueToIndex = new Object2IntOpenHashMap<>(); // Object2IntOpenHashMap

		public int getOrCreateValueIndex(T value) {
			return valueToIndex.computeIntIfAbsent(value, v -> {
				int index = values.size();
				values.add(value);
				return index;
			});
		}

		public T getValueByIndex(int index) {
			if (index < 0 || index >= values.size())
				throw new IndexOutOfBoundsException("Invalid value index: " + index);

			return values.get(index);
		}

		public List<T> getValues() {
			return this.values;
		}

		@Override
		public String toString() {
			return values.toString();
		}
	}
}
