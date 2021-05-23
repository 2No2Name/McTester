package mctester.mixin.accessor;

import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.BlockRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;

@Mixin(TestFunction.class)
public interface TestFunctionAccessor {
    @Mutable
    @Accessor("batchId")
    void setBatchId(String batchId);
    @Mutable
    @Accessor("structurePath")
    void setStructurePath(String structurePath);
    @Mutable
    @Accessor("structureName")
    void setStructureName(String structureName);

    @Mutable
    @Accessor("required")
    void setRequired(boolean required);

    @Mutable
    @Accessor("starter")
    void setStarter(Consumer<TestContext> starter);

    @Mutable
    @Accessor("tickLimit")
    void setTickLimit(int tickLimit);
    @Mutable
    @Accessor("duration")
    void setDuration(long duration);
    @Mutable
    @Accessor("rotation")
    void setRotation(BlockRotation field_25306);
    @Mutable
    @Accessor("maxAttempts")
    void setRepetitions(int repetitions);
    @Mutable
    @Accessor("requiredSuccesses")
    void setRequiredSuccessCount(int repetitions);
}
