package mctester.mixin.test_function_templates;

import mctester.annotation.TestRegistryHelper;
import mctester.common.test.creation.TestConfig;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(TestFunctions.class)
public class TestFunctionsMixin {

    @Inject(
            method = "getTestFunction(Ljava/lang/String;)Ljava/util/Optional;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void getTestFunction(String structurePath, CallbackInfoReturnable<Optional<TestFunction>> cir) {
        if (cir.getReturnValue().isEmpty()) {
            String templateName = TestRegistryHelper.getTemplateName(structurePath);
            if (templateName == null) {
                return;
            }
            Function<String, Stream<TestConfig>> testTemplate = TestRegistryHelper.getTestTemplate(templateName);
            if (testTemplate != null) {
                Stream<TestConfig> apply = testTemplate.apply(structurePath);
                if (apply == null) {
                    return;
                }
                List<TestConfig> list = apply.toList();
                if (list.isEmpty()) {
                    return;
                }
                TestConfig testConfig = list.get((int) (list.size() * Math.random()));
                cir.setReturnValue(Optional.of(testConfig.toTestFunction()));
            }
        }
    }
}
