package mctester.mixin.fixes.function_lookup;

import mctester.common.util.TestFunctionIdentification;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.test.GameTestState;
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
            method = "alignTestStructures",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/test/StructureTestUtil;getStructureBoundingBox(Lnet/minecraft/block/entity/StructureBlockBlockEntity;)Lnet/minecraft/util/math/Box;",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void addTestFunctionIdentifier(Collection<GameTestState> gameTests, CallbackInfoReturnable<Map<GameTestState, BlockPos>> cir, Map<?, ?> map, int i, Box box, Iterator<?> var5, GameTestState gameTestState, BlockPos blockPos, StructureBlockBlockEntity structureBlockBlockEntity) {
        TestFunctionIdentification.setMetaData(structureBlockBlockEntity, gameTestState.getTestFunction());
    }
}
