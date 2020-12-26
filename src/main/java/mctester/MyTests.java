package mctester;

import mctester.annotation.Test;
import mctester.test.TestActions;
import mctester.test.TestConfig;
import mctester.util.GameTestUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class MyTests {
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


    /**
     * A test that can be used to create tests with a simple redstone interface.
     */
    public static void simple_redstone_test(TestConfig testConfig) {
        ArrayList<BlockPos> emeraldBlockList = new ArrayList<>();

        //Replace all red terracotta with redstone block at the start
        testConfig.addAction(0, gameTest -> TestActions.streamPositions(gameTest).forEach(blockPos -> {
            BlockState blockState = gameTest.getWorld().getBlockState(blockPos);
            if (blockState.isOf(Blocks.RED_TERRACOTTA)) {
                gameTest.getWorld().setBlockState(blockPos, Blocks.REDSTONE_BLOCK.getDefaultState());
            }
            if (blockState.isOf(Blocks.EMERALD_BLOCK)) {
                emeraldBlockList.add(blockPos.toImmutable());
            }
        }));

        //Succeed when any powered note block is on top of an emerald block. Assume the emerald block doesn't move etc.
        testConfig.addSuccessCondition(
                gameTest -> emeraldBlockList.stream().anyMatch(blockPos -> {
                    BlockState blockState = gameTest.getWorld().getBlockState(blockPos.up());
                    return blockState.isOf(Blocks.NOTE_BLOCK) && blockState.get(NoteBlock.POWERED) &&
                            gameTest.getWorld().getBlockState(blockPos).isOf(Blocks.EMERALD_BLOCK);
                })
        );
    }
}
