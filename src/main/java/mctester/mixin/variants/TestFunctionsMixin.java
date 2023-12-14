package mctester.mixin.variants;


import mctester.annotation.GameTestExtra;
import mctester.common.test.creation.TestConfig;
import mctester.common.util.BlockRotationUtil;
import net.minecraft.test.GameTest;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import net.minecraft.util.BlockRotation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;


@Mixin(TestFunctions.class)
public abstract class TestFunctionsMixin {

    @Shadow @Final private static Collection<TestFunction> TEST_FUNCTIONS;

    @Shadow
    private static TestFunction getTestFunction(Method method) {
        throw new AssertionError();
    }

    @Redirect(
            method = "register(Ljava/lang/reflect/Method;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/test/TestFunctions;getTestFunction(Ljava/lang/reflect/Method;)Lnet/minecraft/test/TestFunction;", ordinal = 0)
    )
    private static TestFunction registerVariants(Method method) {
        GameTestExtra gameTestExtra = method.getAnnotation(GameTestExtra.class);
        int numVariants = gameTestExtra == null ? 1 : gameTestExtra.variants();

        GameTest gameTest = method.getAnnotation(GameTest.class);
        if (numVariants != 1 || BlockRotationUtil.isCustom(gameTest.rotation())) {
            registerVariants(gameTest, method, numVariants);
            return null;
        }
        return getTestFunction(method);
    }

    @Unique
    private static void registerVariants(GameTest gameTest, Method method, int variants) {
        BlockRotation[] rotations = BlockRotationUtil.getRotations(gameTest.rotation());
        for (BlockRotation rotation : rotations) {
            for (int i = 0; i < variants; i++) {
                TestFunction testFunction = getTestFunction(method);
                testFunction = TestConfig.from(testFunction).rotation(rotation).variant(i).toTestFunction();
                TEST_FUNCTIONS.add(testFunction);
            }
        }
    }

    @Redirect(
            method = "register(Ljava/lang/reflect/Method;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Collection;add(Ljava/lang/Object;)Z", ordinal = 0)
    )
    private static <E> boolean skipNull(Collection<E> instance, E e) {
        if (e != null) {
            return instance.add(e);
        }
        return false;
    }

    @Redirect(
            method = "getTestFunction(Ljava/lang/reflect/Method;)Lnet/minecraft/test/TestFunction;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/test/StructureTestUtil;getRotation(I)Lnet/minecraft/util/BlockRotation;", ordinal = 0)
    )
    private static BlockRotation handleOutOfRange(int steps) {
        if (BlockRotationUtil.isCustom(steps)) {
            return BlockRotation.NONE;
        }
        return StructureTestUtil.getRotation(steps);
    }


    //Todo alternatively / better: Store the variant of the test function in the structure block
    @Redirect(
            method = "getTestFunction(Ljava/lang/String;)Ljava/util/Optional;",
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;findFirst()Ljava/util/Optional;")
    )
    private static Optional<TestFunction> findRandom(Stream<TestFunction> instance) {
        List<TestFunction> list = instance.toList();
        if (list.isEmpty()) {
            return Optional.empty();
        }
        Random random = new Random();
        return Optional.of(list.get(random.nextInt(list.size())));
    }
}
