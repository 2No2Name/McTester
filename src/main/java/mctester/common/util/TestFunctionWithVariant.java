package mctester.common.util;

import org.spongepowered.asm.mixin.Unique;

public interface TestFunctionWithVariant {
    @Unique
    int mcTester$getVariant();

    @Unique
    void mcTester$setVariant(int variant);
}
