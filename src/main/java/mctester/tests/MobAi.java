package mctester.tests;

import mctester.annotation.GameTestExtra;
import mctester.common.test.creation.GameTestHelper;
import mctester.common.test.exceptions.GameTestAssertException;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.entity.EntityType.*;

public class MobAi {
    private static final EntityType<?>[] MOB_TYPES = {VILLAGER, PIG, CAVE_SPIDER, CREEPER};

    @GameTest(tickLimit = 200, required = false, templateName = "fire_maze")
    @GameTestExtra(variants = 4)
    public static void fire_maze(TestContext context) {
        GameTestHelper helper = GameTestHelper.get(context);
        int variant = helper.getVariant();
        //noinspection unchecked
        EntityType<? extends MobEntity> entityType = (EntityType<? extends MobEntity>) MOB_TYPES[variant];
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
