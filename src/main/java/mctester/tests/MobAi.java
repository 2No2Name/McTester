package mctester.tests;

import mctester.annotation.GameTest;
import mctester.common.test.creation.GameTestHelper;
import mctester.common.test.exceptions.GameTestAssertException;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.entity.EntityType.VILLAGER;

public class MobAi {
    @GameTest(timeoutTicks = 200, required = false)
    public static void fire_maze(GameTestHelper helper) {
        MobEntity mob = helper.spawnWithNoFreeWill(VILLAGER, 1, 2, 1);

        final BlockPos targetPos = new BlockPos(10, 2, 5);
        helper.walkTo(mob, targetPos);

        helper.succeedWhen(() -> {
            helper.assertEntityPresent(VILLAGER, targetPos);
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
