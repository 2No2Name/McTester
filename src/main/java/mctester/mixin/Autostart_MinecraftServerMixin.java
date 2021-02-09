package mctester.mixin;

import mctester.McTesterMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class Autostart_MinecraftServerMixin {
    @Shadow public abstract CommandManager getCommandManager();

    @Shadow public abstract ServerCommandSource getCommandSource();

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V"))
    private void runAllTests(CallbackInfo ci) {
        if (McTesterMod.shouldAutorun()) {
            this.getCommandManager().execute(this.getCommandSource(), "/test runall");
        }
    }
}
