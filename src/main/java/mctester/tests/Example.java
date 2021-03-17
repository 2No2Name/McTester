package mctester.tests;

import mctester.annotation.GameTest;
import mctester.common.test.creation.GameTestHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BlockRotation;

public class Example {
    public static final CompoundTag PERSISTENCE_REQUIRED = new CompoundTag();

    static {
        PERSISTENCE_REQUIRED.putBoolean("PersistenceRequired", true);
    }

    @GameTest(structureName = "cow_on_cactus", groupName = "example")
    public static void cowOnCactus(GameTestHelper helper) {
        CowEntity cowEntity = helper.spawnEntity(2, 4, 2, EntityType.COW, PERSISTENCE_REQUIRED);
        helper.succeedWhen(
                helper1 -> !cowEntity.isAlive()
        );
    }

    @GameTest(
            structureName = "waterflow1",
            groupName = "example",
            rotation = {BlockRotation.CLOCKWISE_90, BlockRotation.CLOCKWISE_180, BlockRotation.COUNTERCLOCKWISE_90, BlockRotation.NONE},
            timeoutTicks = 100, //example: shorter cooldown, fail after 5 seconds of no success
            cooldown = 0 //set a cooldown to allow firing observers etc. to stop before starting the test. We don't need that for waterflow.
    )
    public static void waterflow1(GameTestHelper helper) {
        helper.setBlockState(4, 3, 2, Blocks.WATER.getDefaultState());
        helper.succeedWhen(helper1 -> helper.getBlockState(2, 2, 2).getFluidState().isIn(FluidTags.WATER));
    }

    @GameTest(structureName = "wolf_skeleton_fight", groupName = "example")
    public static void wolf_skeleton_fight(GameTestHelper helper) {
        helper.spawnEntity(2, 2, 2, EntityType.SKELETON, PERSISTENCE_REQUIRED);
        helper.spawnEntity(2, 2, 2, EntityType.WOLF, PERSISTENCE_REQUIRED);

        helper.succeedWhen(
                helper1 -> helper1.getEntitiesInside().stream().anyMatch(
                        entity -> (entity.getType() == EntityType.SKELETON || entity.getType() == EntityType.WOLF)
                                && !entity.isAlive())
        );
    }
}
