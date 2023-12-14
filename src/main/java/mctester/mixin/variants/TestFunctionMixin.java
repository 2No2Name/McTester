package mctester.mixin.variants;

import mctester.common.util.TestFunctionWithVariant;
import net.minecraft.test.TestFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TestFunction.class)
public class TestFunctionMixin implements TestFunctionWithVariant {

    @Unique
    private int variant;

    @Override
    @Unique
    public int mcTester$getVariant() {
        return variant;
    }

    @Override
    @Unique
    public void mcTester$setVariant(int variant) {
        this.variant = variant;
    }
}
