package mctester.mixin;

import mctester.McTesterMod;
import net.minecraft.server.command.TestCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TestCommand.class)
public class CheckAndPrintResults_TestCommandMixin {
    private static final Logger LOGGER = LogManager.getLogger();

    @Inject(at = @At(value = "HEAD"), method = "sendMessage(Lnet/minecraft/server/world/ServerWorld;Ljava/lang/String;Lnet/minecraft/util/Formatting;)V")
    private static void sendToConsole(ServerWorld world, String message, Formatting formatting, CallbackInfo ci) {
        LOGGER.info(message);
        if (message.endsWith(" required tests failed :(")) {
            if (McTesterMod.shouldCrashOnFail()) {
                throw new CrashException(new CrashReport("Automatically triggered crash due to failed tests", new Throwable()));
            } else if (McTesterMod.shouldShutdownAfterTest() && !McTesterMod.shouldStayUpAfterFail()) {
                world.getServer().stop(false);
            }
        } else if (message.endsWith("All required tests passed :)")) {
            if (McTesterMod.shouldShutdownAfterTest()) {
                world.getServer().stop(false);
            }
        }
    }
}
