package mctester.mixin.fixes.function_lookup;

import mctester.common.util.TestFunctionIdentification;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.server.command.TestCommand;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TestCommand.class)
public class TestCommandMixin {

    @Redirect(
            method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/test/TestSet;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/entity/StructureBlockBlockEntity;getStructurePath()Ljava/lang/String;"
            )
    )
    private static String getTestFunctionIdentifier(StructureBlockBlockEntity structureBlockBlockEntity) {
        String testFunctionIdentifierFromMetaData = TestFunctionIdentification.getTestFunctionIdentifierFromMetaData(structureBlockBlockEntity);
        if (testFunctionIdentifierFromMetaData != null) {
            return testFunctionIdentifierFromMetaData;
        } else {
            return structureBlockBlockEntity.getStructurePath();
        }
    }

    @Redirect(
            method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/test/TestSet;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/test/TestFunctions;getTestFunctionOrThrow(Ljava/lang/String;)Lnet/minecraft/test/TestFunction;"
            )
    )
    private static TestFunction getTestFunction(String structurePath) {
        TestFunction testFunction = TestFunctionIdentification.identifier2TestFunction.getOrDefault(structurePath, null);
        if (testFunction != null) {
            return testFunction;
        }
        return TestFunctions.getTestFunctionOrThrow(structurePath);
    }
}
