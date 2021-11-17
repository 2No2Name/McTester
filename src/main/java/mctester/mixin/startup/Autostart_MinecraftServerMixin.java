package mctester.mixin.startup;

import mctester.McTesterConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(MinecraftServer.class)
public abstract class Autostart_MinecraftServerMixin {
    @Shadow public abstract CommandManager getCommandManager();

    @Shadow public abstract ServerCommandSource getCommandSource();

    @Shadow @Final private static Logger LOGGER;

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V"))
    private void runAllTests(CallbackInfo ci) {
        if (McTesterConfig.shouldAutorun()) {
            if (McTesterConfig.shouldShuffleBeforeAutorun()) {
                Collection<TestFunction> testFunctions = TestFunctions.getTestFunctions();
                long seed = McTesterConfig.shuffleSeed();
                Random random = new Random(seed);
                LOGGER.info("Shuffling tests with random seed: " + seed);
                if (testFunctions instanceof List) {
                    Collections.shuffle((List<?>) testFunctions, random);
                }
            }
            this.getCommandManager().execute(this.getCommandSource(), "/test runall");
        }
    }
}
