package mctester.templates;

import mctester.annotation.GameTest;
import mctester.annotation.GameTestTemplate;
import mctester.common.test.creation.GameTestHelper;
import mctester.common.test.creation.TestConfig;
import mctester.common.test.exceptions.GameTestAssertException;
import mctester.common.util.GameTestUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.test.PositionedException;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.stream.Stream;

public class TestRedstoneTemplate {

    @GameTestTemplate(name = "test_redstone")
    public static Stream<TestConfig> testFromStructure(String structureName) {
        TestConfig testConfig = new TestConfig(TestRedstoneTemplate::test_redstone).structureName(structureName);
        //10 tick delay at the start to avoid accidental success condition activation due to redstone flickering
        testConfig.structurePlaceCooldown(10);
        //todo postprocessing options, e.g. a 2nd dot in the name
        // e.g. "test_redstone.rotate.mytest123" could mean that mytest123 should be rotated freely

        return Stream.of(testConfig);
    }

    private static boolean isSuccessBlock(BlockState blockState) {
        return blockState.isOf(Blocks.EMERALD_BLOCK) || blockState.isOf(Blocks.GREEN_WOOL) || blockState.isOf(Blocks.LIME_WOOL);
    }

    private static boolean isFailureBlock(BlockState blockState) {
        return blockState.isOf(Blocks.RED_WOOL);
    }

    /**
     * A test function that can be used to create tests with a simple redstone interface.
     */
    @GameTest
    public static void test_redstone(GameTestHelper helper) {
        ArrayList<BlockPos> successBlocks = new ArrayList<>();
        ArrayList<BlockPos> failureBlocks = new ArrayList<>();

        //Replace all red terracotta with redstone block at the start and fill the condition block lists
        GameTestUtil.streamPositions(helper.gameTest).forEach(blockPos -> {
            BlockState blockState = helper.gameTest.getWorld().getBlockState(blockPos);
            if (blockState.isOf(Blocks.RED_TERRACOTTA)) {
                helper.gameTest.getWorld().setBlockState(blockPos, Blocks.REDSTONE_BLOCK.getDefaultState());
            }
            if (isSuccessBlock(blockState)) successBlocks.add(blockPos.toImmutable());
            if (isFailureBlock(blockState)) failureBlocks.add(blockPos.toImmutable());
        });
        if (successBlocks.isEmpty()) {
            throw new GameTestAssertException("Expected success condition blocks anywhere inside the test. test_redstone requires green or lime wool or emerald blocks for the success condition");
        }

        //Succeed when any powered note block is on top of a success condition block. Assume the success condition block doesn't move etc.
        helper.succeedWhen(
                helper1 -> {
                    //Always check the failure condition blocks before the success conditions. Failed tests throw exceptions.
                    if (
                            failureBlocks.stream().anyMatch(blockPos -> {
                                BlockState blockState = helper1.gameTest.getWorld().getBlockState(blockPos.up());
                                return blockState.isOf(Blocks.NOTE_BLOCK) && blockState.get(NoteBlock.POWERED) &&
                                        isFailureBlock(helper1.gameTest.getWorld().getBlockState(blockPos));
                            })) {
                        BlockPos blockPos = helper1.gameTest.getPos();
                        BlockPos absolutePos = successBlocks.get(0);
                        BlockPos relativePos = StructureTemplate.transformAround(absolutePos, BlockMirror.NONE, GameTestUtil.getInverse(helper1.gameTest.getRotation()), blockPos).add(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
                        throw new PositionedException("Failure condition was met with powered noteblock on top of a failure condition block", absolutePos, relativePos, helper1.currTick);
                    }
                    return successBlocks.stream().anyMatch(blockPos -> {
                        BlockState blockState = helper1.gameTest.getWorld().getBlockState(blockPos.up());
                        return blockState.isOf(Blocks.NOTE_BLOCK) && blockState.get(NoteBlock.POWERED) &&
                                isSuccessBlock(helper1.gameTest.getWorld().getBlockState(blockPos));
                    });
                },
                helper1 -> {
                    BlockPos blockPos = helper1.gameTest.getPos();
                    BlockPos absolutePos = successBlocks.get(0);
                    BlockPos relativePos = StructureTemplate.transformAround(absolutePos, BlockMirror.NONE, GameTestUtil.getInverse(helper1.gameTest.getRotation()), blockPos).add(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
                    return new PositionedException("Expected powered noteblock on top of an success condition block. For example", absolutePos, relativePos, helper1.currTick);
                }
        );
    }
}
