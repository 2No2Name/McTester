package mctester.mixin;

import mctester.common.test.creation.GameTestHelper;
import net.minecraft.test.GameTestState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.LongConsumer;

@Mixin(GameTestState.class)
public class GameTestStateMixin implements GameTestHelper.GameTestAccess {
    @Unique
    private final GameTestHelper gameTestHelper = new GameTestHelper((GameTestState) (Object) this);
    @Shadow
    private long tick;
    @Unique
    private LongConsumer tickCallback;

    @Override
    public GameTestHelper mcTester$getGameTestHelper() {
        return this.gameTestHelper;
    }

    @Override
    public void mcTester$setTickCallback(LongConsumer handler) {
        this.tickCallback = handler;
    }

    @Inject(
            method = "tickTests()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lit/unimi/dsi/fastutil/objects/Object2LongMap;object2LongEntrySet()Lit/unimi/dsi/fastutil/objects/ObjectSet;",
                    shift = At.Shift.BEFORE,
                    remap = false
            )
    )
    private void tickGameTestHelper(CallbackInfo ci) {
        this.tickCallback.accept(this.tick);
    }
}
