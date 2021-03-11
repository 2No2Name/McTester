package mctester.mixin.enable_testing;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Redirect(
            method = "tickWorlds",
            at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z")
    )
    private boolean enableTests(BooleanSupplier shouldKeepTicking) {
        return true;
    }
}
