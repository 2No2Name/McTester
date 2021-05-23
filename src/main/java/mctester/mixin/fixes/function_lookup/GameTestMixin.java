package mctester.mixin.fixes.function_lookup;

import mctester.common.util.TestFunctionIdentification;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TestFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameTestState.class)
public abstract class GameTestMixin {
    @Shadow
    public abstract TestFunction getTestFunction();

    @Redirect(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/StructureBlockBlockEntity;setStructureName(Ljava/lang/String;)V"
            )
    )
    private void addTestFunctionIdentifier(StructureBlockBlockEntity structureBlockBlockEntity, String name) {
        TestFunctionIdentification.setMetaData(structureBlockBlockEntity, this.getTestFunction());
    }
}
