package mctester;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import mctester.common.TestActions;
import mctester.common.TestConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NoteBlock;
import net.minecraft.test.GameTest;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class Templates {
    public static final Object2ReferenceOpenHashMap<String, Function<TestConfig, Stream<TestConfig>>> TEST_TEMPLATES = new Object2ReferenceOpenHashMap<>();

    static {
        //replaces red terracotta with redstone block as start and succeeds if noteblock on top of emerald block is powered
        TEST_TEMPLATES.put("test_redstone", Templates::test_redstone);
    }

    /**
     * A test function that can be used to create tests with a simple redstone interface.
     */
    public static Stream<TestConfig> test_redstone(TestConfig testConfig) {
        //Need to use a GameTest->Object map here to avoid reusing the list when a test config is run again!
        WeakHashMap<GameTest, ArrayList<BlockPos>> emeraldBlockListStorage = new WeakHashMap<>();

        //10 tick cooldown at the start to avoid accidental success condition activation due to redstone flickering
        testConfig.repeatedActionsStart(10);

        //Replace all red terracotta with redstone block at the start
        testConfig.addAction(0, gameTest -> {
            ArrayList<BlockPos> emeraldBlockList = emeraldBlockListStorage.computeIfAbsent(gameTest, (testConfig1) -> new ArrayList<>());

            TestActions.streamPositions(gameTest).forEach(blockPos -> {
                BlockState blockState = gameTest.getWorld().getBlockState(blockPos);
                if (blockState.isOf(Blocks.RED_TERRACOTTA)) {
                    gameTest.getWorld().setBlockState(blockPos, Blocks.REDSTONE_BLOCK.getDefaultState());
                }
                if (blockState.isOf(Blocks.EMERALD_BLOCK)) {
                    emeraldBlockList.add(blockPos.toImmutable());
                }
            });
        });

        //Succeed when any powered note block is on top of an emerald block. Assume the emerald block doesn't move etc.
        testConfig.addSuccessCondition(
                gameTest -> emeraldBlockListStorage.get(gameTest).stream().anyMatch(blockPos -> {
                    BlockState blockState = gameTest.getWorld().getBlockState(blockPos.up());
                    return blockState.isOf(Blocks.NOTE_BLOCK) && blockState.get(NoteBlock.POWERED) &&
                            gameTest.getWorld().getBlockState(blockPos).isOf(Blocks.EMERALD_BLOCK);
                })
        );

        return Stream.of(testConfig);
    }
}
