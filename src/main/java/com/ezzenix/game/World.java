package com.ezzenix.game;

import com.ezzenix.utilities.BlockPos;
import org.joml.Vector2i;

import java.util.HashMap;

public class World {
    private final HashMap<Vector2i, Chunk> chunks = new HashMap<>();

    public World() {
        Chunk c = new Chunk(new Vector2i(0, 0));
        chunks.put(new Vector2i(0, 0), c);
    }
}
