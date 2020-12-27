package mctester.annotation;

import mctester.Templates;
import mctester.test.TestConfig;
import mctester.test.TestHelper;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.test.StructureTestUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Consumer;

public class TestRegistryHelper {

    public static void convertAllNbtToSnbt() {
        String structuresDirectoryName = StructureTestUtil.testStructuresDirectoryName;
        File[] files = new File(structuresDirectoryName).listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (!file.isFile() || !file.canRead() || !fileName.endsWith(".nbt")) {
                continue;
            }
            String structureName = fileName.substring(0, fileName.length() - ".nbt".length());

            NbtProvider.convertNbtToSnbt(file.toPath(), structureName, Paths.get(structuresDirectoryName));
            boolean b = file.delete();
        }
    }

    public static void createTestsFromClass(Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredMethods()).filter(m -> m.getAnnotation(Test.class) != null || m.getAnnotation(Tests.class) != null).forEach(TestRegistryHelper::createTestFromMethod);
    }

    public static void createTemplatedTestsFromFiles() {
        String structuresDirectoryName = StructureTestUtil.testStructuresDirectoryName;
        File[] files = new File(structuresDirectoryName).listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (!file.isFile() || !file.canRead() || !fileName.endsWith(".snbt")) {
                continue;
            }
            String structureName = fileName.substring(0, fileName.length() - ".snbt".length());
            int dotIndex = structureName.indexOf(".");
            if (dotIndex < 0) {
                continue;
            }
            String templateName = structureName.substring(0, dotIndex);
            Consumer<TestConfig> testConfigConsumer = Templates.TEST_TEMPLATES.get(templateName);
            if (testConfigConsumer != null) {
                createTestForFile(structureName, templateName, testConfigConsumer);
            }
        }
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

    public static void createTestForFile(String structureName, String template, Consumer<TestConfig> method) {
        TestConfig testConfig = new TestConfig().structureName(structureName);
        method.accept(testConfig);
        TestHelper.registerTest(testConfig.build(), template);
    }

    public static void createTest(Method method, Test annotation) {
        TestConfig testConfig = TestConfig.from(annotation);
        try {
            method.invoke(null, testConfig);
            TestHelper.registerTest(testConfig.build(), annotation.groupName());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
