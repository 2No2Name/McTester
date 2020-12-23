package mctester.annotation;

import mctester.test.MyTests;
import mctester.test.TestConfig;
import mctester.test.TestHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TestRegistryHelper {
    public static void createTestsFromClass(Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredMethods()).filter(m -> m.getAnnotation(Test.class) != null).forEach(TestRegistryHelper::createTestFromMethod);
    }

    public static void createTestFromMethod(Method method) {
        Test annotation = method.getAnnotation(Test.class);
        TestConfig testConfig = TestConfig.from(annotation);
        try {
            method.invoke(null, testConfig);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        TestHelper.registerTest(testConfig.build());
    }
}
