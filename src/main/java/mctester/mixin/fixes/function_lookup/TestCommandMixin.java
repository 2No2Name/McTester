package mctester.mixin.fixes.function_lookup;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.command.TestCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TestCommand.class)
public class TestCommandMixin {

    @Redirect(
            method = "find",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/StructureBlockBlockEntity;getMetadata()Ljava/lang/String;"
            )
    )
    private static String getTestFunctionIdentifier(StructureBlockBlockEntity structureBlockBlockEntity) {
        String retval = structureBlockBlockEntity.getMetadata();
        if (retval.isEmpty()) {
            retval = structureBlockBlockEntity.getTemplateName();
            if (retval.startsWith("minecraft:")) {
                retval = retval.substring("minecraft:".length());
            }
        }
        return retval;
    }
}
