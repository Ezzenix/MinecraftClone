package com.ezzenix.blocks;

import java.util.HashMap;

public class Blocks {
	private static final HashMap<Byte, Block> blockMap = new HashMap<>();

	public static final Block AIR = new Block("Air").notSolid();
	public static final Block STONE = new Block("Stone").setTexture("stone");
	public static final Block GRASS_BLOCK = new Block("Grass Block").setTexture(new BlockTexture().top("grass_block_top").side("grass_block_side").bottom("dirt"));
	public static final Block DIRT = new Block("Dirt").setTexture("dirt");
	public static final Block OAK_PLANKS = new Block("Oak Planks").setTexture("oak_planks");
	public static final Block WATER = new FluidBlock("Water").setTexture("water").transparent().notSolid().fluid();
	public static final Block SAND = new Block("Sand").setTexture("sand");
	public static final Block OAK_LEAVES = new Block("Oak Leaves").setTexture("oak_leaves").transparent().notSolid();
	public static final Block OAK_LOG = new Block("Oak Log").setTexture(new BlockTexture().side("oak_log").topBottom("oak_log_top"));
	public static final Block GRASS = new PlantBlock("Grass").setTexture("grass").transparent().flower().instantBreak();
	public static final Block POPPY = new PlantBlock("Poppy").setTexture("poppy").transparent().flower().instantBreak();
	public static final Block GLASS = new Block("Glass").setTexture("glass").transparent();

	public static void register(Block block) {
		byte newId = (byte) (blockMap.size() + 1);
		blockMap.put(newId, block);
	}
}
