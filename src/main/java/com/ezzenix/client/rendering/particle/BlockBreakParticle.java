package com.ezzenix.client.rendering.particle;

import com.ezzenix.client.gui.Color;

public class BlockBreakParticle extends Particle {
	public BlockBreakParticle(float x, float y, float z, float vx, float vy, float vz) {
		super(x, y, z, vx, vy, vz, 0.05f, Color.pack(120, 120, 120, 255), 0.6f);
	}
}
