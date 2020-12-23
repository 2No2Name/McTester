package mctester.test;

import mctester.mixin.TestFunctionAccessor;
import mctester.util.UnsafeUtil;
import net.minecraft.test.StartupParameter;
import net.minecraft.test.TestFunction;
import net.minecraft.util.BlockRotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class TestFunctionCreator {

    public static TestFunction createTestFunction(String batchId, String structurePath, String structureName, boolean required, Consumer<StartupParameter> starter, int tickLimit, long duration, BlockRotation field_25306) {
        try {
            TestFunctionAccessor testFunction = (TestFunctionAccessor) UnsafeUtil.unsafe.allocateInstance(TestFunction.class);
            testFunction.setBatchId(batchId);
            testFunction.setStructurePath(structurePath);
            testFunction.setStructureName(structureName);
            testFunction.setRequired(required);
            testFunction.setStarter(starter);
            testFunction.setTickLimit(tickLimit);
            testFunction.setDuration(duration);
            testFunction.setRotation(field_25306);
            return (TestFunction) testFunction;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
