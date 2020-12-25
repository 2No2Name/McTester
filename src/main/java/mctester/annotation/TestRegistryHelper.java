package mctester.annotation;

import mctester.test.TestConfig;
import mctester.test.TestHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TestRegistryHelper {
    public static void createTestsFromClass(Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredMethods()).filter(m -> m.getAnnotation(Test.class) != null || m.getAnnotation(Tests.class) != null).forEach(TestRegistryHelper::createTestFromMethod);
    }

    public static void createTestFromMethod(Method method) {
        Tests multiAnnotation = method.getAnnotation(Tests.class);
        if (multiAnnotation != null) {
            for (Test annotation : multiAnnotation.value()) {
                createTest(method, annotation);
            }
        } else {
            Test annotation = method.getAnnotation(Test.class);
            createTest(method, annotation);
        }
    }

    public static void createTest(Method method, Test annotation) {
        TestConfig testConfig = TestConfig.from(annotation);
        try {
            method.invoke(null, testConfig);
            TestHelper.registerTest(testConfig.build());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
