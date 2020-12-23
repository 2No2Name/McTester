package mctester.test;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestFunction;
import net.minecraft.util.BlockRotation;

public class MyGameTest extends GameTest {
    public MyGameTest(TestFunction testFunction, BlockRotation blockRotation, ServerWorld serverWorld) {
        super(testFunction, blockRotation, serverWorld);
    }
}
