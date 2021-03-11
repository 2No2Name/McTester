package mctester.mixin.startup;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SharedConstants.class)
public class SharedConstantsMixin {
    @Shadow
    public static boolean isDevelopment;

    static {
        String isDevelopmentProperty = System.getProperty("mctester.isDevelopment");
        if (isDevelopmentProperty != null) {
            isDevelopment = Boolean.parseBoolean(isDevelopmentProperty);
        }
    }
}
