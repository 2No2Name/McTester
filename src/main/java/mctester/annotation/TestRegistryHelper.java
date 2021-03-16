package mctester.annotation;

import mctester.Templates;
import mctester.common.testcreation.TestConfig;
import net.minecraft.data.dev.NbtProvider;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class TestRegistryHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    public static boolean shouldWarnOnMissingStructureFile = true;

    /**
     * Convenience method to allow the user to put normal structure block nbt files into the gameteststructures folder.
     */
    public static void convertAllNbtToSnbt() {
        String structuresDirectoryName = StructureTestUtil.testStructuresDirectoryName;
        File[] files = new File(structuresDirectoryName).listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file == null) {
                continue;
            }
            String fileName = file.getName();
            if (!file.isFile() || !file.canRead() || !fileName.endsWith(".nbt")) {
                continue;
            }
            String structureName = fileName.substring(0, fileName.length() - ".nbt".length());

            Path path = NbtProvider.convertNbtToSnbt(file.toPath(), structureName, Paths.get(structuresDirectoryName));
            if (path != null) {
                //delete nbt file after successfully converting to snbt
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
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
            Function<TestConfig, Stream<TestConfig>> testVariantCreator = Templates.TEST_TEMPLATES.get(templateName);
            if (testVariantCreator != null) {
                createTestForFile(structureName, templateName, testVariantCreator);
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

    public static void createTestForFile(String structureName, String templateName, Function<TestConfig, Stream<TestConfig>> testVariantCreator) {
        TestConfig baseConfig = new TestConfig().structureName(structureName);
        testVariantCreator.apply(baseConfig).forEach(
                testConfig -> registerTest(testConfig.toTestFunction(), templateName)
        );
    }

    public static void createTest(Method method, Test annotation) {
        Stream<TestConfig> testConfigs = TestConfig.from(annotation);
        Path path = Paths.get(StructureTestUtil.testStructuresDirectoryName);
        Path structurePath = path.resolve(annotation.groupName() + "." + annotation.structureName() + ".snbt");
        if (!structurePath.toFile().exists()) {
            if (shouldWarnOnMissingStructureFile) {
                LOGGER.warn("Structure for test not found: Method name: " + method.getName() + " Structure path: " + structurePath + " . Removing test!");
            }
            return;
        }
        if (method.getReturnType() == void.class) {
            testConfigs.forEach(
                    (TestConfig testConfig) -> {
                        try {
                            method.invoke(null, testConfig);
                            registerTest(testConfig.toTestFunction(), annotation.groupName());
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException("Could not create tests!", e);
                        }
                    });
        } else {
            Function<TestConfig, Stream<TestConfig>> variantCreator = testConfig -> {
                try {
                    //noinspection unchecked
                    return (Stream<TestConfig>) method.invoke(null, testConfig);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Could not create tests!", e);
                }
            };
            createTests(testConfigs, variantCreator, annotation.groupName());
        }
    }

    public static void createTests(Stream<TestConfig> testConfigs, Function<TestConfig, Stream<TestConfig>> variantCreator, String groupName) {
        testConfigs.flatMap(variantCreator).forEach(
                testConfig -> registerTest(testConfig.toTestFunction(), groupName)
        );
    }

    public static void registerTest(TestFunction testFunction, String className) {
        TestFunctions.getTestFunctions().add(testFunction);
        if (!className.equals("")) {
            TestFunctions.getTestClasses().add(className);
        }
    }
}
