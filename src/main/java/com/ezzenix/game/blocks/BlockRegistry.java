package com.ezzenix.game.blocks;

import java.util.HashMap;

public class BlockRegistry {
    private static final HashMap<Byte, BlockType> blockMap = new HashMap<>();

    public static byte registerBlock(BlockType blockType) {
        byte newId = (byte)(blockMap.size() + 1);
        blockMap.put(newId, blockType);
        return newId;
    }

    public static BlockType getBlockFromId(byte id) {
        return blockMap.get(id);
    }
}
