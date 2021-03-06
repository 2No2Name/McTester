package mctester.mixin.startup;

import mctester.McTesterConfig;
import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {
    @Shadow
    public static boolean isDevelopment;

    static {
        if (McTesterConfig.isDevelopment()) {
            isDevelopment = true;
        }
    }
}
