package mctester.tests;

import mctester.common.test.creation.GameTestHelper;
import mctester.common.test.exceptions.GameTestAssertException;
import mctester.common.util.GameTestUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.test.GameTest;
import net.minecraft.test.PositionedException;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

public class Example {
    public static final NbtCompound PERSISTENCE_REQUIRED = new NbtCompound();

    static {
        PERSISTENCE_REQUIRED.putBoolean("PersistenceRequired", true);
    }

    @GameTest
    public static void cow_on_cactus(TestContext context) {
        GameTestHelper helper = GameTestHelper.get(context);
        CowEntity cowEntity = helper.spawnEntity(2, 4, 2, EntityType.COW, PERSISTENCE_REQUIRED);
        helper.succeedWhen(
                helper1 -> !cowEntity.isAlive(),
                helper1 -> {
                    BlockPos relativePos = new BlockPos(2, 4, 2);
                    return new PositionedException("Expected dead cow", GameTestUtil.transformRelativeToAbsolutePos(helper1.gameTest, relativePos), relativePos, helper1.currTick);
                }
        );
    }

    @GameTest(
            rotation = 5,
            duration = 0 //set a cooldown to allow firing observers etc. to stop before starting the test. We don't need that for waterflow.
    )
    public static void waterflow1(TestContext context) {
        GameTestHelper helper = GameTestHelper.get(context);
        helper.setBlockState(4, 3, 2, Blocks.WATER.getDefaultState());
        helper.succeedWhen(
                helper1 -> helper.getBlockState(2, 2, 2).getFluidState().isIn(FluidTags.WATER),
                helper1 -> {
                    BlockPos relativePos = new BlockPos(2, 2, 2);
                    return new PositionedException("Expected water", GameTestUtil.transformRelativeToAbsolutePos(helper1.gameTest, relativePos), relativePos, helper1.currTick);
                }
        );
    }

    @GameTest(tickLimit = 300)
    public static void wolf_skeleton_fight(TestContext context) {
        GameTestHelper helper = GameTestHelper.get(context);
        helper.spawnEntity(2, 2, 2, EntityType.SKELETON, PERSISTENCE_REQUIRED);
        helper.spawnEntity(2, 2, 2, EntityType.WOLF, PERSISTENCE_REQUIRED);

        helper.succeedWhen(
                helper1 -> helper1.getEntitiesInside().stream().anyMatch(
                        entity -> (entity.getType() == EntityType.SKELETON || entity.getType() == EntityType.WOLF)
                                && !entity.isAlive()),
                helper1 -> new GameTestAssertException("Expected dead skeleton or dead wolf!")
        );
    }
}
