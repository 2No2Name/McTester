package mctester.annotation;

import com.mojang.logging.LogUtils;
import mctester.TestTemplates;
import mctester.common.test.creation.GameTestHelper;
import mctester.common.test.creation.TestConfig;
import mctester.common.util.TestFunctionIdentification;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import org.slf4j.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestRegistryHelper {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static boolean shouldWarnOnMissingStructureFile = true;

    public static void createTestsFromClass(Class<?> clazz) {
        Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.getAnnotation(GameTest.class) != null || m.getAnnotation(GameTests.class) != null)
                .forEach(TestRegistryHelper::createTestFromMethod);
    }

    public static void createTemplatedTestsFromFiles() {
        String structuresDirectoryName = StructureTestUtil.testStructuresDirectoryName;
        File[] files = new File(structuresDirectoryName).listFiles();
        if (files != null) {
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
                Function<String, Stream<TestConfig>> testInitializer = TestTemplates.TEST_TEMPLATES.get(templateName);
                if (testInitializer != null) {
                    createTestsForTemplatedStructure(structureName, templateName, testInitializer);
                }
            }
        }
    }

    public static void createTestFromMethod(Method method) {
        GameTests multiAnnotation = method.getAnnotation(GameTests.class);
        if (multiAnnotation != null) {
            for (GameTest annotation : multiAnnotation.value()) {
                createTest(method, annotation);
            }
        } else {
            GameTest annotation = method.getAnnotation(GameTest.class);
            createTest(method, annotation);
        }
    }

    public static void createTestsForTemplatedStructure(String structureName, String templateName, Function<String, Stream<TestConfig>> testInitializer) {
        List<TestConfig> apply = testInitializer.apply(structureName).collect(Collectors.toList());
        for (int i = 0; i < apply.size(); i++) {
            TestConfig testConfig = apply.get(i);
            TestFunction testFunction = testConfig.toTestFunction();

            TestFunctionIdentification.registerTestFunctionIdentifier(testFunction, i);
            registerTest(testFunction, templateName);
        }
    }

    public static void createTest(Method method, GameTest annotation) {
        String structurePathName = TestAnnotationHelper.getGroupName(annotation, method) + "." + TestAnnotationHelper.getStructureName(annotation, method);
        Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName);
        Path structurePath = path.resolve(structurePathName + ".snbt");
        if (!structurePath.toFile().exists()) {
            if (shouldWarnOnMissingStructureFile) {
                LOGGER.warn("Structure for test not found: Method name: " + method.getName() + " Structure path: " + structurePath + " . Removing test!");
            }
            return;
        }
        Consumer<GameTestHelper> startupFunction = gameTestHelper -> {
            try {
                method.invoke(null, gameTestHelper);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.warn("Startup function invocation crashes!");
                throw new RuntimeException("Error when invoking test startup function! Method name: " + method.getName() + " Structure path: " + structurePath, e);
            }
        };
        Stream<TestConfig> testConfigs = TestConfig.from(annotation, structurePathName, startupFunction);
        List<TestConfig> testConfigList = testConfigs.collect(Collectors.toList());
        for (TestConfig testConfig : testConfigList) {
            for (int i = 0; i < annotation.numVariants(); i++) {
                TestFunction testFunction = testConfig.toTestFunction();
                TestFunctionIdentification.registerTestFunctionIdentifier(testFunction, i);
                registerTest(testFunction, TestAnnotationHelper.getGroupName(annotation, method));
            }
        }
    }

    public static void registerTest(TestFunction testFunction, String className) {
        TestFunctions.getTestFunctions().add(testFunction);
        if (!className.equals("")) {
            TestFunctions.getTestClasses().add(className);
        }
    }
}
