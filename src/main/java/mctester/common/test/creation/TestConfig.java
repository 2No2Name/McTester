package mctester.common.test.creation;

import mctester.mixin.accessor.TestContextAccessor;
import mctester.common.util.TestFunctionWithVariant;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.BlockRotation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class TestConfig {

    private String batchId = "defaultbatch";
    private String structurePath;
    private String structureName;
    private long cooldown = 0; //time between loading the structure and starting the test
    private int timeout = 400; //time after which the test automatically fails
    private boolean required = true;
    private BlockRotation rotation = BlockRotation.NONE;
    private int repetitions;
    private int requiredSuccessCount;
    
    private Consumer<TestContext> starter;

    private int variant;


    public TestConfig(@NotNull Consumer<GameTestHelper> starter) {
        this.starter = startupParameter -> starter.accept(
                ((GameTestHelper.GameTestAccess) ((TestContextAccessor) startupParameter).getTest()).mcTester$getGameTestHelper());
    }
    private TestConfig() {}

    public static TestConfig from(TestFunction testFunction) {
        TestConfig testConfig = new TestConfig();
        testConfig.batchId = testFunction.batchId();
        testConfig.structurePath = testFunction.templatePath();
        testConfig.structureName = testFunction.templateName();
        testConfig.cooldown = testFunction.setupTicks();
        testConfig.timeout = testFunction.tickLimit();
        testConfig.required = testFunction.required();
        testConfig.rotation = testFunction.rotation();
        testConfig.repetitions = testFunction.maxAttempts();
        testConfig.requiredSuccessCount = testFunction.requiredSuccesses();
        testConfig.starter = testFunction.starter();
        return testConfig;
    }

    public TestFunction toTestFunction() {
        TestFunction testFunction = new TestFunction(this.batchId, this.structurePath, this.structureName, this.rotation, this.timeout, this.cooldown, this.required, this.requiredSuccessCount, this.repetitions, this.starter);
        ((TestFunctionWithVariant) testFunction).mcTester$setVariant(this.variant);
        return testFunction;
    }

    public TestConfig batchId(String batchId) {
        this.batchId = batchId;
        return this;
    }

    public TestConfig structureName(String structurePath) {
        this.structurePath = structurePath;
        this.structureName = structurePath;
        return this;
    }

    public TestConfig required(boolean required) {
        this.required = required;
        return this;
    }

    public TestConfig structurePlaceCooldown(long ticks) {
        this.cooldown = ticks;
        return this;
    }

    public TestConfig timeout(int ticks) {
        this.timeout = ticks;
        return this;
    }

    public TestConfig rotation(BlockRotation rotation) {
        this.rotation = rotation;
        return this;
    }

    public TestConfig requiredSuccessCount(int requiredSuccessCount) {
        this.requiredSuccessCount = requiredSuccessCount;
        return this;
    }

    public TestConfig repetitions(int repetitions) {
        this.repetitions = repetitions;
        return this;
    }

    public TestConfig variant(int variant) {
        this.variant = variant;
        return this;
    }
}
