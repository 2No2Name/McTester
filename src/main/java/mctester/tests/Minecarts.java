package mctester.tests;

import mctester.common.test.creation.GameTestHelper;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

import static net.minecraft.entity.EntityType.MINECART;

public class Minecarts {
    @GameTest
    public static void turn(TestContext context) {
        GameTestHelper helper = GameTestHelper.get(context);
        helper.pressButton(0, 3, 1);
        helper.succeedWhenEntityPresent(MINECART, 3, 2, 2);
    }
}
