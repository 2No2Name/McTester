package mctester.mixin.fixes.test_exceptions;

import mctester.common.copy.PositionedException2;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.GameTest;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(targets = "net.minecraft.test.StructureTestListener")
public abstract class StructureTestListenerMixin {
    @Shadow
    private static native void addGameTestMarker(ServerWorld world, BlockPos pos, String message);

    @Inject(
            method = "finishFailedTest(Lnet/minecraft/test/GameTest;Ljava/lang/Throwable;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/test/TestFailureLogger;failTest(Lnet/minecraft/test/GameTest;)V",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void handleCustomException(GameTest test, Throwable output, CallbackInfo ci, String string, String string2, Throwable throwable) {
        if (throwable instanceof PositionedException2) {
            PositionedException2 positionedException2 = (PositionedException2) throwable;
            addGameTestMarker(test.getWorld(), positionedException2.getPos(), positionedException2.getDebugMessage());
        }
    }
}
