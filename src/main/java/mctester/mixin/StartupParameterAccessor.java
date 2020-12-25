package mctester.mixin;

import net.minecraft.test.GameTest;
import net.minecraft.test.StartupParameter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StartupParameter.class)
public interface StartupParameterAccessor {
    @Accessor("test")
    GameTest getTest();
}
