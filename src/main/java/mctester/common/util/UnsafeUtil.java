package mctester.common.util;

import mctester.mixin.accessor.TestFunctionAccessor;
import net.minecraft.test.StartupParameter;
import net.minecraft.test.TestFunction;
import net.minecraft.util.BlockRotation;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class UnsafeUtil {
    private static Unsafe unsafe;

    static {
        Field unsafeField;
        try {
            unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            UnsafeUtil.unsafe = (Unsafe) unsafeField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalStateException("McTester: Could not get Unsafe!", e);
        }
    }

    public static TestFunction createTestFunction(String batchId, String structurePath, String structureName, boolean required, Consumer<StartupParameter> starter, int tickLimit, long duration, BlockRotation field_25306, int repetitions, int requiredSuccessCount) {
        try {
            TestFunctionAccessor testFunction = (TestFunctionAccessor) unsafe.allocateInstance(TestFunction.class);
            testFunction.setBatchId(batchId);
            testFunction.setStructurePath(structurePath);
            testFunction.setStructureName(structureName);
            testFunction.setRequired(required);
            testFunction.setStarter(starter);
            testFunction.setTickLimit(tickLimit);
            testFunction.setDuration(duration);
            testFunction.setRotation(field_25306);
            testFunction.setRepetitions(repetitions);
            testFunction.setRequiredSuccessCount(requiredSuccessCount);
            return (TestFunction) testFunction;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
