package mctester.mixin.fixes;

import net.minecraft.test.FailureLoggingTestCompletionListener;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FailureLoggingTestCompletionListener.class)
public class FailureLoggingTestCompletionListenerMixin {
    @Redirect(
            method = "onTestFailed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Util;getInnermostMessage(Ljava/lang/Throwable;)Ljava/lang/String;"
            )
    )
    private String getMessage(Throwable t) {
        String innermostMessage = Util.getInnermostMessage(t);
        t.printStackTrace();
        return innermostMessage;
    }
}
