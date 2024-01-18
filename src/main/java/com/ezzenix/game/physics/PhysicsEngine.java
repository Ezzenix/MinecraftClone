package com.ezzenix.game.physics;

import com.ezzenix.Game;
import com.ezzenix.engine.scheduler.Scheduler;
import com.ezzenix.engine.utils.BlockPos;
import com.ezzenix.game.blocks.BlockType;
import com.ezzenix.game.entities.Entity;
import com.ezzenix.game.world.World;
import com.ezzenix.hud.Debug;
import org.joml.Vector3f;

public class PhysicsEngine {
    public static void step() {
        float deltaTime = Scheduler.getDeltaTime();

        float gameSpeed = 0.02f;

        for (Entity entity : Game.getInstance().getEntities()) {
            // gravity
            //entity.getVelocity().add(0, -9.82f * deltaTime, 0);

            Vector3f newPosition = new Vector3f(
                    entity.getPosition()
            ).add(
                    new Vector3f(entity.getVelocity()).mul(deltaTime).mul(gameSpeed)
            );

            World world = entity.getWorld();
            BlockPos blockPos = BlockPos.fromVector3f(newPosition);

            //System.out.println(newPosition.toString(new DecimalFormat("#.##")) + "   " + blockPos);

            BlockPos blockPosBelow = blockPos.add(0, (newPosition.y%1) > 0.9f ? 0 : -1, 0);
            Debug.highlightVoxel(new Vector3f(blockPosBelow.x, blockPosBelow.y, blockPosBelow.z));
            BlockType blockBelow = world.getBlockTypeAt(blockPosBelow);
            if (blockBelow.isSolid()) {
                float distance = newPosition.y - (blockPosBelow.y+1);
                System.out.println(distance);
                if (distance < 0.4f) {
                    newPosition.add(0, distance-0.4f, 0);
                    entity.getVelocity().set(0);
                }
            }

            // apply velocity to position
            entity.getPosition().set(newPosition);
        }
    }
}
