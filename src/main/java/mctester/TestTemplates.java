package mctester;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import mctester.annotation.GameTest;
import mctester.common.copy.PositionedException2;
import mctester.common.test.creation.GameTestHelper;
import mctester.common.test.creation.TestConfig;
import mctester.common.test.exceptions.GameTestAssertException;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

public class TestTemplates {
    public static final Object2ReferenceOpenHashMap<String, Function<String, Stream<TestConfig>>> TEST_TEMPLATES = new Object2ReferenceOpenHashMap<>();

    static {
        //replaces red terracotta with redstone block as start and succeeds if noteblock on top of emerald block is powered
        TEST_TEMPLATES.put("test_redstone", TestTemplates::test_redstone_from_structure);
    }

    public static Stream<TestConfig> test_redstone_from_structure(String structureName) {
        TestConfig testConfig = new TestConfig(TestTemplates::test_redstone).structureName(structureName);
        //10 tick delay at the start to avoid accidental success condition activation due to redstone flickering
        testConfig.structurePlaceCooldown(10);
        //todo postprocessing options, e.g. a 2nd dot in the name
        // e.g. "test_redstone.rotate.mytest123" could mean that mytest123 should be rotated freely

        return Stream.of(testConfig);
    }

    /**
     * A test function that can be used to create tests with a simple redstone interface.
     */
    @GameTest()
    public static void test_redstone(GameTestHelper helper) {
        ArrayList<BlockPos> emeraldBlockList = new ArrayList<>();

        //Replace all red terracotta with redstone block at the start and fill the emerald block list
        helper.streamPositions().forEach(blockPos -> {
            BlockState blockState = helper.gameTest.getWorld().getBlockState(blockPos);
            if (blockState.isOf(Blocks.RED_TERRACOTTA)) {
                helper.gameTest.getWorld().setBlockState(blockPos, Blocks.REDSTONE_BLOCK.getDefaultState());
            }
            if (blockState.isOf(Blocks.EMERALD_BLOCK)) {
                emeraldBlockList.add(blockPos.toImmutable());
            }
        });
        if (emeraldBlockList.isEmpty()) {
            throw new GameTestAssertException("Expected emerald blocks anywhere inside the test. test_redstone requires emerald blocks for the success condition");
        }

        //Succeed when any powered note block is on top of an emerald block. Assume the emerald block doesn't move etc.
        helper.succeedWhen(() ->
                        emeraldBlockList.stream().anyMatch(blockPos -> {
                            BlockState blockState = helper.gameTest.getWorld().getBlockState(blockPos.up());
                            return blockState.isOf(Blocks.NOTE_BLOCK) && blockState.get(NoteBlock.POWERED) &&
                                    helper.gameTest.getWorld().getBlockState(blockPos).isOf(Blocks.EMERALD_BLOCK);
                        }),
                () -> new PositionedException2("Expected powered noteblock on top of an emerald block. For example", emeraldBlockList.get(0), helper.gameTest, helper.currTick)
        );
    }
}
