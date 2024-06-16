package com.ezzenix.client.rendering.util;

import java.nio.ByteBuffer;

public class DynamicByteBuffer {
	private ByteBuffer buffer;
	private int size;

	public DynamicByteBuffer(int initialSize) {
		if (initialSize <= 0) initialSize = 1;
		this.buffer = GlAllocationUtils.allocateByteBuffer(initialSize);
		this.size = 0;
	}

	private void grow() {
		int currentCapacity = this.buffer.capacity();
		if (this.size <= currentCapacity) return;

		System.out.println("Resizing buffer from " + currentCapacity + " to " + this.size);
		int pos = this.buffer.position();
		this.buffer = GlAllocationUtils.resizeByteBuffer(this.buffer, this.size);
		this.buffer.position(pos);
	}

	public void putFloat(float value) {
		this.size += Float.BYTES;
		this.grow();
		this.buffer.putFloat(value);
	}

	public void putInt(int value) {
		this.size += Integer.BYTES;
		this.grow();
		this.buffer.putInt(value);
	}

	public void putShort(short value) {
		this.size += Short.BYTES;
		this.grow();
		this.buffer.putShort(value);
	}

	public void putByte(byte value) {
		this.size += Byte.BYTES;
		this.grow();
		this.buffer.put(value);
	}

	public ByteBuffer end() {
		this.buffer.flip();
		return this.buffer;
	}

	public void clear() {
		this.buffer.clear();
		this.size = 0;
	}
}
