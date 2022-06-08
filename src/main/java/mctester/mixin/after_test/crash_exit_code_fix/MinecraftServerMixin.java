package mctester.mixin.after_test.crash_exit_code_fix;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin makes the server crashing use System.exit(1) instead of System.exit(0). For example gradle can use this to
 * detect the failure.
 */
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    private boolean systemExit1WithServerThread = false;

    @Inject(method = "runServer()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;setCrashReport(Lnet/minecraft/util/crash/CrashReport;)V"
            )
    )
    private void rememberCrash(CallbackInfo ci) {
        this.systemExit1WithServerThread = true;
    }

    @Inject(method = "runServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;exit()V",
                    shift = At.Shift.AFTER
            )
    )
    private void exitWithStatusCode(CallbackInfo ci) {
        if (this.systemExit1WithServerThread) {
            System.exit(1);
        }
    }

    @Redirect(method = "stop(Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Thread;join()V"
            )
    )
    private void join(Thread thread) throws InterruptedException {
        //a thread that called System.exit() should not be waited for in the shutdown hook
        if (!this.systemExit1WithServerThread) {
            thread.join();
        }
    }


}
