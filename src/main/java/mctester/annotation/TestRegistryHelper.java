package mctester.annotation;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import mctester.common.test.creation.TestConfig;
import net.minecraft.test.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class TestRegistryHelper {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static boolean shouldWarnOnMissingStructureFile = true;
    public static final Object2ReferenceOpenHashMap<String, Function<String, Stream<TestConfig>>> TEST_TEMPLATES = new Object2ReferenceOpenHashMap<>();

    public static void createTestTemplateFromClass(Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.getAnnotation(GameTestTemplate.class) != null)
                .forEach(TestRegistryHelper::createTestTemplateFromMethod);
    }

    public static void createTestsFromClass(Class<?> clazz) {
        TestFunctions.register(clazz);
    }

    public static void createTemplatedTestsFromFiles() {
        String structuresDirectoryName = StructureTestUtil.testStructuresDirectoryName;
        File[] files = new File(structuresDirectoryName).listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (!file.isFile() || !file.canRead() || !fileName.endsWith(".snbt")) continue;
                String structureName = fileName.substring(0, fileName.length() - ".snbt".length());
                String templateName = getTemplateName(structureName);
                if (templateName == null) continue;
                Function<String, Stream<TestConfig>> testInitializer = getTestTemplate(templateName);
                if (testInitializer != null) {
                    createTestsForTemplatedStructure(structureName, templateName, testInitializer);
                }
            }
        }
    }

    public static Function<String, Stream<TestConfig>> getTestTemplate(String templateName) {
        return TEST_TEMPLATES.get(templateName);
    }

    @Nullable
    public static String getTemplateName(String structureName) {
        int dotIndex = structureName.indexOf(".");
        if (dotIndex < 0) {
            return null;
        }
        return structureName.substring(0, dotIndex);
    }

    @SuppressWarnings("unchecked")
    public static void createTestTemplateFromMethod(Method method) {
        GameTestTemplate annotation = method.getAnnotation(GameTestTemplate.class);
        TEST_TEMPLATES.put(annotation.name(), s -> { //Should prob just pass the method
            try {
                return (Stream<TestConfig>) method.invoke(null, s);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.warn("Unable to load test template: " + annotation.name());
            }
            return null;
        });
    }

    public static void createTestsForTemplatedStructure(String structureName, String templateName, Function<String, Stream<TestConfig>> testInitializer) {
        List<TestConfig> apply = testInitializer.apply(structureName).toList();
        for (TestConfig testConfig : apply) {
            TestFunction testFunction = testConfig.toTestFunction();
            registerTest(testFunction, templateName);
        }
    }

    public static void registerTest(TestFunction testFunction, String className) {
        TestFunctions.getTestFunctions().add(testFunction);
        if (!className.isEmpty()) {
            TestFunctions.getTestClasses().add(className);
        }
    }
}
