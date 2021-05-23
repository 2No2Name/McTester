package mctester.mixin.fixes.rotation;

import mctester.common.util.BlockRotationUtil;
import net.minecraft.server.command.TestCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestFunction;
import net.minecraft.util.BlockRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TestCommand.class)
public class TestCommandMixin {
    @Redirect(
            method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/test/TestSet;)V",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/test/GameTest"
            )
    )
    private static GameTestState fixRotation(TestFunction testFunction, BlockRotation rotation, ServerWorld world) {
        //The structure block is already rotated by the rotation of the test function in this code path, as it was previously run with the added rotation
        //we rotate by the inverse to cancel the extra rotation that the GameTest constructor applies otherwise
        return new GameTestState(testFunction, rotation.rotate(BlockRotationUtil.inverseOf(testFunction.getRotation())), world);
    }
}
