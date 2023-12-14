package mctester.tests;

import mctester.common.test.creation.GameTestHelper;
import mctester.common.util.GameTestUtil;
import net.minecraft.block.Blocks;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

public class BoxFill {
    @GameTest(
            rotation = 5
    )
    public static void fillall(TestContext context) {
        GameTestHelper helper = GameTestHelper.get(context);
        GameTestUtil.streamPositions(helper.gameTest).forEach(blockPos ->
                helper.gameTest.getWorld().setBlockState(blockPos, Blocks.STONE.getDefaultState()));
        helper.succeedWhen(
                helper1 -> true
        );
    }
}
