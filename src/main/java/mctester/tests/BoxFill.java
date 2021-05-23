package mctester.tests;

import mctester.annotation.GameTest;
import mctester.common.test.creation.GameTestHelper;
import mctester.common.util.GameTestUtil;
import net.minecraft.block.Blocks;
import net.minecraft.util.BlockRotation;

public class BoxFill {
    @GameTest(
            rotation = {BlockRotation.NONE, BlockRotation.CLOCKWISE_90, BlockRotation.CLOCKWISE_180, BlockRotation.COUNTERCLOCKWISE_90},
            timeoutTicks = 100, //example: shorter cooldown, fail after 5 seconds of no success
            cooldown = 0 //set a cooldown to allow firing observers etc. to stop before starting the test. We don't need that for waterflow.
    )
    public static void fillall(GameTestHelper helper) {
        GameTestUtil.streamPositions(helper.gameTest).forEach(blockPos -> {
            helper.gameTest.getWorld().setBlockState(blockPos, Blocks.STONE.getDefaultState());
        });
        helper.succeedWhen(
                helper1 -> true
        );
    }
}
