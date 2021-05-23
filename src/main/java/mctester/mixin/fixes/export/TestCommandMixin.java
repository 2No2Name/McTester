package mctester.mixin.fixes.export;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.command.TestCommand;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TestCommand.class)
public class TestCommandMixin {
    @Redirect(
            method = "executeExport(Lnet/minecraft/server/command/ServerCommandSource;)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/StructureBlockBlockEntity;getStructurePath()Ljava/lang/String;")
    )
    private static String getNamespacedStructurePath(StructureBlockBlockEntity structureBlockBlockEntity) {
        return structureBlockBlockEntity.getStructureName();
    }

    @Redirect(
            method = "executeExport(Lnet/minecraft/server/command/ServerCommandSource;Ljava/lang/String;)I",
            at = @At(value = "NEW", target = "net/minecraft/util/Identifier")
    )
    private static Identifier getStructureIdentifier(String namespace, String path) {
        if (path.contains(":")) {
            return new Identifier(path);
        } else {
            return new Identifier(namespace, path);
        }
    }

    @ModifyVariable(
            method = "executeExport(Lnet/minecraft/server/command/ServerCommandSource;Ljava/lang/String;)I",
            at = @At(
                    shift = At.Shift.BEFORE,
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/command/ServerCommandSource;getWorld()Lnet/minecraft/server/world/ServerWorld;"
            )
    )
    private static String removeMinecraftPrefix(String structureIdentifier) {
        if (structureIdentifier.startsWith("minecraft:")) {
            return structureIdentifier.replaceFirst("minecraft:", "");
        }
        return structureIdentifier;
    }
}
