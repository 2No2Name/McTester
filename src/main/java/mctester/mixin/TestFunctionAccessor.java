package mctester.mixin;

import net.minecraft.test.StartupParameter;
import net.minecraft.test.TestFunction;
import net.minecraft.util.BlockRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;

@Mixin(TestFunction.class)
public interface TestFunctionAccessor {
    @Accessor("batchId")
    void setBatchId(String batchId);
    @Accessor("structurePath")
    void setStructurePath(String structurePath);
    @Accessor("structureName")
    void setStructureName(String structureName);
    @Accessor("required")
    void setRequired(boolean required);
    @Accessor("starter")
    void setStarter(Consumer<StartupParameter> starter);
    @Accessor("tickLimit")
    void setTickLimit(int tickLimit);
    @Accessor("duration")
    void setDuration(long duration);
    @Accessor("field_25306")
    void setRotation(BlockRotation field_25306);
    @Accessor("field_27814")
    void setRepetitions(int repetitions);
    @Accessor("field_27815")
    void setRequiredSuccessCount(int repetitions);
}
