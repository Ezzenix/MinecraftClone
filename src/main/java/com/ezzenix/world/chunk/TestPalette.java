package com.ezzenix.world.chunk;

import com.ezzenix.blocks.BlockState;
import com.ezzenix.blocks.Blocks;

public class TestPalette {
	public static void run() {

		PalettedContainer<BlockState> container = new PalettedContainer<>(16, 256, Blocks.AIR.getDefaultState());


		BlockState stone = Blocks.STONE.getDefaultState();
		BlockState oak_log = Blocks.OAK_LOG.getDefaultState();
		BlockState oak_plank = Blocks.OAK_PLANKS.getDefaultState();
		BlockState glass = Blocks.GLASS.getDefaultState();
		BlockState grass_block = Blocks.GRASS_BLOCK.getDefaultState();
		BlockState dirt = Blocks.DIRT.getDefaultState();


		long start = System.currentTimeMillis();

		for (int i = 0; i <= 100; i++) {

			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 256; y++) {
					for (int z = 0; z < 16; z++) {
						container.set(x, y, z, glass);
						container.set(x, y, z, oak_log);
					}
					container.set(x, y, 0, oak_plank);
				}
			}


			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 256; y++) {
					for (int z = 0; z < 16; z++) {
						BlockState state = container.get(x, y, z);
					}
				}
			}

		}

		long t = System.currentTimeMillis() - start;
		System.out.println("Time: " + t + "ms");
	}
}
