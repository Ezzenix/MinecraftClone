package com.ezzenix.game.entities;

import com.ezzenix.Game;
import com.ezzenix.math.BlockPos;
import com.ezzenix.game.physics.AABB;
import com.ezzenix.game.world.World;
import org.joml.Vector3f;

public class Entity {
    private Vector3f position;
    private Vector3f velocity;
    private float yaw;
    private float pitch;
    private World world;

    public float eyeHeight = 1.5f;

    public AABB aabb;
    public boolean isGrounded;

    public Entity(World world, Vector3f position) {
        this.yaw = 0;
        this.pitch = 0;
        this.position = position;
        this.velocity = new Vector3f();

        this.isGrounded = false;
        this.aabb = new AABB( 0.8f, 1.8f);

        Game.getInstance().getEntities().add(this);

        this.world = world;
    }

    public Vector3f getPosition() {
        return this.position;
    }
    public Vector3f getVelocity() {
        return this.velocity;
    }
    public float getYaw() {
        return this.yaw;
    }
    public float getPitch() {
        return this.pitch;
    }

    public void setPosition(Vector3f pos) {
        this.position = pos;
    }
    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }
    public void setYaw(float yaw) {
        while (yaw > 180) yaw -= 360;
        while (yaw < 180) yaw += 360;
        this.yaw = (yaw + 180.0f) % 360.0f - 180.0f;
    }
    public void setPitch(float pitch) {
        float min = -89.99f;
        float max = 89.99f;
        this.pitch = Math.max(min, Math.min(max, pitch));
    }

    public void addYaw(float offset) {
        this.setYaw(this.yaw + offset);
    }
    public void addPitch(float offset) {
        this.setPitch(this.pitch + offset);
    }

    public BlockPos getBlockPos() {
        return BlockPos.from(position);
    }

    public World getWorld() {
        return this.world;
    }
}
