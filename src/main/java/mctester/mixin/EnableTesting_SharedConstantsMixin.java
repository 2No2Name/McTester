package mctester.mixin;

import net.minecraft.SharedConstants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SharedConstants.class)
public class EnableTesting_SharedConstantsMixin {
    @Shadow public static boolean isDevelopment;

    static {
        isDevelopment = true;
    }
}
