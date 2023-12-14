package mctester.mixin.accessor;

import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Consumer;

@Mixin(TestFunction.class)
public interface TestFunctionAccessor {

    @Accessor("starter")
    Consumer<TestContext> getStarter();

}
