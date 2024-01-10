package com.ezzenix.game;

import java.util.HashMap;

public class BlockDataStructure {
    private HashMap<Byte, HashMap<Byte, HashMap<Byte, Short>>> blocks;

    public BlockDataStructure() {
        blocks = new HashMap<>();
    }

    public short get(byte x, byte y, byte z) {
        HashMap<Byte, HashMap<Byte, Short>> xRow = blocks.get(x);
        if (xRow == null) return 0;
        HashMap<Byte, Short> yRow = xRow.get(y);
        if (yRow == null) return 0;
        return yRow.get(z);
    }

    public short get(float x, float y, float z) {
        return get((byte)x, (byte)y, (byte)z);
    }

    public void set(byte x, byte y, byte z, short id) {
        blocks.computeIfAbsent(x, k -> new HashMap<>()).computeIfAbsent(y, k -> new HashMap<>()).put(z, id);
    }

    public void set(float x, float y, float z, short id) {
        set((byte)x, (byte)y, (byte)z, id);
    }
}
