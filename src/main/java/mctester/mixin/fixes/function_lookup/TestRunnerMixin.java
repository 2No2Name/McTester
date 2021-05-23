package mctester.mixin.fixes.function_lookup;

import mctester.common.util.TestFunctionIdentification;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestRunner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Mixin(TestRunner.class)
public class TestRunnerMixin {
    @Inject(
            method = "method_29401",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/test/StructureTestUtil;createStructure(Ljava/lang/String;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/BlockRotation;ILnet/minecraft/server/world/ServerWorld;Z)Lnet/minecraft/block/entity/StructureBlockBlockEntity;",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void addTestFunctionIdentifier(Collection<?> collection, CallbackInfoReturnable<Map<?, ?>> cir, Map<?, ?> map, int i, Box box, Iterator<?> var5, GameTest gameTest, BlockPos blockPos, StructureBlockBlockEntity structureBlockBlockEntity) {
        TestFunctionIdentification.setMetaData(structureBlockBlockEntity, gameTest.getTestFunction());
    }
}
