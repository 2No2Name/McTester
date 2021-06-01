package mctester.common.test.creation;

import mctester.annotation.GameTest;
import mctester.mixin.accessor.StartupParameterAccessor;
import net.minecraft.test.TestContext;
import net.minecraft.test.TestFunction;
import net.minecraft.util.BlockRotation;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TestConfig {

    private String batchId = "defaultbatch";
    private String structureName;
    private long cooldown = 0; //time between loading the structure and starting the test
    private int timeout = 400; //time after which the test automatically fails
    private boolean required = true;
    private BlockRotation rotation = BlockRotation.NONE;
    private int repetitions;
    private int requiredSuccessCount;
    @NotNull
    private final Consumer<TestContext> starter;


    public TestConfig(Consumer<GameTestHelper> testStartupFunction) {
        this.starter = startupParameter -> testStartupFunction.accept(
                ((GameTestHelper.GameTestAccess) ((StartupParameterAccessor) startupParameter).getTest()).getGameTestHelper()
        );
    }

    public static Stream<TestConfig> from(GameTest annotation, String structurePathName, Consumer<GameTestHelper> testStartupFunction) {
        return Arrays.stream(annotation.rotation()).map(
                blockRotation -> new TestConfig(testStartupFunction)
                        .rotation(blockRotation)
                        .required(annotation.required())
                        .batchId(annotation.batchId())
                        .structurePlaceCooldown(annotation.cooldown())
//                .structurePath(structurePath) //for some reason using path = name seems to work better at the time of writing (1.17 snapshots)
                        .structureName(structurePathName)
                        .timeout(annotation.timeoutTicks())
                        .repetitions(annotation.repetitions()).requiredSuccessCount(annotation.requiredSuccessCount())
        );
    }

    public TestFunction toTestFunction() {
        //todo fix using structureName twice here! But for some reason it is actually working best like this.
        return new TestFunction(this.batchId, this.structureName, this.structureName, this.rotation, this.timeout, this.cooldown, this.required, this.requiredSuccessCount, this.repetitions, this.starter);
//        try {
//            TestFunctionAccessor testFunction = (TestFunctionAccessor) theUnsafe.allocateInstance(TestFunction.class);
//            testFunction.setBatchId(batchId);
//            testFunction.setStructurePath(structurePath);
//            testFunction.setStructureName(structureName);
//            testFunction.setRequired(required);
//            testFunction.setStarter(starter);
//            testFunction.setTickLimit(tickLimit);
//            testFunction.setDuration(duration);
//            testFunction.setRotation(rotation);
//            testFunction.setRepetitions(repetitions);
//            testFunction.setRequiredSuccessCount(requiredSuccessCount);
//            return (TestFunction) testFunction;
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    public TestConfig batchId(String batchId) {
        this.batchId = batchId;
        return this;
    }

    public TestConfig structureName(String structureName) {
        this.structureName = structureName;
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
}
