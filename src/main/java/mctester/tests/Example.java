package mctester.tests;

import mctester.annotation.GameTest;
import mctester.common.copy.PositionedException2;
import mctester.common.test.creation.GameTestHelper;
import mctester.common.test.exceptions.GameTestAssertException;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

public class Example {
    public static final NbtCompound PERSISTENCE_REQUIRED = new NbtCompound();

    static {
        PERSISTENCE_REQUIRED.putBoolean("PersistenceRequired", true);
    }

    @GameTest
    public static void cowOnCactus(GameTestHelper helper) {
        CowEntity cowEntity = helper.spawnEntity(2, 4, 2, EntityType.COW, PERSISTENCE_REQUIRED);
        helper.succeedWhen(
                helper1 -> !cowEntity.isAlive(),
                helper1 -> new PositionedException2("Expected dead cow", helper1.gameTest, new BlockPos(2, 4, 2), helper1.currTick)
        );
    }

    @GameTest(
            rotation = {BlockRotation.NONE, BlockRotation.CLOCKWISE_90, BlockRotation.CLOCKWISE_180, BlockRotation.COUNTERCLOCKWISE_90},
            timeoutTicks = 100, //example: shorter cooldown, fail after 5 seconds of no success
            cooldown = 0 //set a cooldown to allow firing observers etc. to stop before starting the test. We don't need that for waterflow.
    )
    public static void waterflow1(GameTestHelper helper) {
        helper.setBlockState(4, 3, 2, Blocks.WATER.getDefaultState());
        helper.succeedWhen(
                helper1 -> helper.getBlockState(2, 2, 2).getFluidState().isIn(FluidTags.WATER),
                helper1 -> new PositionedException2("Expected water", helper1.gameTest, new BlockPos(2, 2, 2), helper1.currTick)
        );
    }

    @GameTest
    public static void wolf_skeleton_fight(GameTestHelper helper) {
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
