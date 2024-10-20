package com.ezzenix;

public class Game {
	public static void main(String[] args) {
		/*
		LocalPosition[] array = new LocalPosition[]{
			new LocalPosition(0, 0, 0),
			new LocalPosition(0, 1, 0),
			new LocalPosition(0, 2, 0),

			new LocalPosition(0, 1, 0),
			new LocalPosition(1, 1, 0),
			new LocalPosition(2, 1, 0),

			new LocalPosition(0, 1, 0),
			new LocalPosition(0, 1, 1),
			new LocalPosition(0, 1, 2)
		};

		for (LocalPosition pos : array) {
			System.out.println(pos + " " + pos.toIndex());
		}
		 */

		//new TestBlock();


		Client.init();


		/*
		int count = 1000000;

		BufferAllocator allocator = new BufferAllocator(1024);

		BufferBuilder builder = new BufferBuilder(allocator);
		builder.vertex(1, 2, 3).next();

		BuiltBuffer builtBuffer = builder.end();
		ByteBuffer buff = builtBuffer.getBuffer();
		Client.LOGGER.info(buff.getFloat());
		Client.LOGGER.info(buff.getFloat());
		Client.LOGGER.info(buff.getFloat());

		builtBuffer.close();


		Client.LOGGER.info("Hello {}", 1);
		Client.LOGGER.warn("wow warning");
		Client.LOGGER.error("oh no! error?!?!");
		 */


		/*
		long st1 = System.currentTimeMillis();
		DynamicByteBuffer consumer = new DynamicByteBuffer(1024);
		for (int i = 0; i <= count; i++) {
			consumer.putFloat(1234423f);
			consumer.putFloat(5);
			consumer.putFloat(2);
		}
		ByteBuffer byteBuffer = consumer.end();
		for (int i = 0; i <= count; i++) {
			byteBuffer.getFloat();
		}
		System.out.println((System.currentTimeMillis() - st1) + "ms");
		 */

	}
}