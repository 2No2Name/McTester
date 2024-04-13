package mctester.mixin.enable_testing;

import net.minecraft.structure.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StructureTemplateManager.class)
public class StructureTemplateManagerMixin {
    @Redirect(
            method = "<init>",
            at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z")
    )
    private boolean enableTests() {
        return true;
    }

}
