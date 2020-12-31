package mctester;

import mctester.annotation.Test;
import mctester.test.TestActions;
import mctester.test.TestConfig;
import mctester.util.GameTestUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.FluidTags;

public class ExampleTests {
    public static final CompoundTag PERSISTENCE_REQUIRED = new CompoundTag();
    static {
        PERSISTENCE_REQUIRED.putBoolean("PersistenceRequired", true);
    }

    @Test(structureName = "cow_on_cactus", groupName = "example")
    public static void cowOnCactus(TestConfig testConfig) {
        testConfig.addAction(0, TestActions.spawnEntity(2, 4, 2, EntityType.COW));
        testConfig.addSuccessCondition(
                gameTest -> GameTestUtil.getEntitiesInTestArea(gameTest).stream().anyMatch(entity -> entity.getType() == EntityType.COW && !entity.isAlive())
        );
    }

    @Test(structureName = "waterflow1", groupName = "example")
    public static void waterflow1(TestConfig testConfig) {
        //set a cooldown to allow firing observers etc. to stop before starting the test. We don't need that for waterflow.
        testConfig.structurePlaceCooldown(0);
        //example: shorter cooldown, fail after 5 seconds of no success
        testConfig.timeout(100);

        testConfig.addAction(0, new TestActions.SetBlockState(4, 3, 2, Blocks.WATER.getDefaultState()));
        testConfig.addAction(0, gameTest -> GameTestUtil.setBlockStateWithTransforms(gameTest, 4, 3, 2, Blocks.WATER.getDefaultState()));

        testConfig.addSuccessCondition(gameTest -> GameTestUtil.getBlockStateWithTransforms(gameTest, 2, 2, 2).getFluidState().isIn(FluidTags.WATER));
    }

    @Test(structureName = "wolf_skeleton_fight", groupName = "example")
    public static void wolf_skeleton_fight(TestConfig testConfig) {
        testConfig.addAction(0, TestActions.spawnEntity(2, 2, 2, EntityType.SKELETON, PERSISTENCE_REQUIRED));
        testConfig.addAction(0, TestActions.spawnEntity(2, 2, 2, EntityType.WOLF, PERSISTENCE_REQUIRED));

        testConfig.addSuccessCondition(
                gameTest -> GameTestUtil.getEntitiesInTestArea(gameTest).stream().anyMatch(
                        entity -> (entity.getType() == EntityType.SKELETON || entity.getType() == EntityType.WOLF)
                                && !entity.isAlive())
        );
    }
}
