package mctester.test;

import mctester.util.UnsafeUtil;
import net.minecraft.test.StartupParameter;
import net.minecraft.test.TestFunction;
import net.minecraft.util.BlockRotation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class TestFunctionCreator {

    private static final Field[] testFunctionFields;
    private static final ArrayList<String> fieldNames;


    static {
        fieldNames = new ArrayList<>();
        //todo allow usage of intermediary names too
        fieldNames.addAll(Arrays.asList("batchId", "structurePath", "structureName", "required", "starter", "tickLimit", "duration", "field_25306"));

        testFunctionFields = new Field[fieldNames.size()];
        Field[] declaredFields = TestFunction.class.getDeclaredFields();
        int i = fieldNames.size();
        for (Field f : declaredFields) {
            if (fieldNames.contains(f.getName())) {
                f.setAccessible(true);
                testFunctionFields[fieldNames.indexOf(f.getName())] = f;
                i--;
            }
        }
        if (i != 0) {
            throw new IllegalStateException("Could not initialize TestFunctionCreator!");
        }


    }

    public static TestFunction createTestFunction(String batchId, String structurePath, String structureName, boolean required, Consumer<StartupParameter> starter, int tickLimit, long duration, BlockRotation field_25306) {
        try {
            TestFunction testFunction = (TestFunction) UnsafeUtil.unsafe.allocateInstance(TestFunction.class);
            testFunctionFields[0].set(testFunction, batchId);
            testFunctionFields[1].set(testFunction, structurePath);
            testFunctionFields[2].set(testFunction, structureName);
            testFunctionFields[3].set(testFunction, required);
            testFunctionFields[4].set(testFunction, starter);
            testFunctionFields[5].set(testFunction, tickLimit);
            testFunctionFields[6].set(testFunction, duration);
            testFunctionFields[7].set(testFunction, field_25306);
            return testFunction;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
