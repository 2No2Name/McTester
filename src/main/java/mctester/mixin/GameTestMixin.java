package mctester.mixin;

import mctester.common.test.creation.GameTestHelper;
import net.minecraft.test.GameTestState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.LongConsumer;

@Mixin(GameTestState.class)
public class GameTestMixin implements GameTestHelper.GameTestAccess {
    private final GameTestHelper gameTestHelper = new GameTestHelper((GameTestState) (Object) this);
    @Shadow
    private long tick;
    private LongConsumer tickCallback;

    @Override
    public GameTestHelper getGameTestHelper() {
        return this.gameTestHelper;
    }

    @Override
    public void setTickCallback(LongConsumer handler) {
        this.tickCallback = handler;
    }

    @Inject(
            method = "method_33315",
            at = @At(
                    value = "INVOKE",
                    target = "Lit/unimi/dsi/fastutil/objects/Object2LongMap;object2LongEntrySet()Lit/unimi/dsi/fastutil/objects/ObjectSet;",
                    shift = At.Shift.BEFORE
            )
    )
    private void tickGameTestHelper(CallbackInfo ci) {
        this.tickCallback.accept(this.tick);
    }
}
