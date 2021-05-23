package mctester.tests;

import mctester.annotation.GameTest;
import mctester.common.test.creation.GameTestHelper;
import mctester.common.test.exceptions.GameTestAssertException;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.entity.EntityType.*;

public class MobAi {
    private static final EntityType<?>[] MOB_TYPES = {VILLAGER, PIG, CAVE_SPIDER, CREEPER};
    private static final int NUM_MOB_TYPES = 4;

    @GameTest(timeoutTicks = 200, required = false, numVariants = NUM_MOB_TYPES)
    public static void fire_maze(GameTestHelper helper) {
        //noinspection unchecked
        EntityType<? extends MobEntity> entityType = (EntityType<? extends MobEntity>) MOB_TYPES[helper.getVariantIndex()];
        MobEntity mob = helper.spawnWithNoFreeWill(entityType, 1, 2, 1);

        final BlockPos targetPos = new BlockPos(10, 2, 5);
        helper.walkTo(mob, targetPos);

        helper.succeedWhen(() -> {
            helper.assertEntityPresent(entityType, targetPos);
            if (mob.isOnFire()) {
                throw new GameTestAssertException("Mob should not be burning!");
            }
            if (mob.getHealth() < mob.getMaxHealth()) {
                throw new GameTestAssertException("Mob should not be hurt!");
            }
            return true;
        });
    }
}
