package mctester.common.test.creation;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import mctester.annotation.GameTest;
import mctester.common.util.UnsafeUtil;
import mctester.mixin.accessor.GameTestAccessor;
import mctester.mixin.accessor.StartupParameterAccessor;
import net.minecraft.test.StartupParameter;
import net.minecraft.test.TestFunction;
import net.minecraft.util.BlockRotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class TestConfig {

    private String batchId = "defaultbatch";
    private String structurePath = "";
    private String structureName;
    private long cooldown = 0; //time between loading the structure and starting the test
    private int timeout = 400; //time after which the test automatically fails
    private boolean required = true;
    private BlockRotation rotation = BlockRotation.NONE;
    private int repetitions;
    private int requiredSuccessCount;

    private final Int2ObjectOpenHashMap<ArrayList<Consumer<net.minecraft.test.GameTest>>> actionsByTick = new Int2ObjectOpenHashMap<>();
    private final ArrayList<Consumer<net.minecraft.test.GameTest>> repeatedActions = new ArrayList<>();
    private Consumer<StartupParameter> starter;
    private int repeatedActionsStart = 1;


    public TestConfig(Consumer<GameTestHelper> testStartupFunction) {
        this.starter = startupParameter -> testStartupFunction.accept(
                new GameTestHelper(((StartupParameterAccessor) startupParameter).getTest())
        );
    }

    public TestConfig(List<Consumer<GameTestHelper>> testStartupFunctions) {
        this.starter = startupParameter -> testStartupFunctions.forEach(
                gameTestHelperConsumer -> gameTestHelperConsumer.accept(new GameTestHelper(((StartupParameterAccessor) startupParameter).getTest()))
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
                        .repeatedActionsStart(annotation.repeatedActionsStart())
        );
    }

    /**
     * Adds the runnable to the test.
     *
     * @param startupParameter context of the test
     * @param runnable         the runnable to add, we allow consuming the GameTest to allow more flexible runnables
     * @param tick             the tick after startup the runnable should be run
     */
    @Deprecated
    public static void addRunnableToTick(StartupParameter startupParameter, Consumer<net.minecraft.test.GameTest> runnable, long tick) {
        net.minecraft.test.GameTest gameTest = ((StartupParameterAccessor) startupParameter).getTest();
        Object2LongMap<Runnable> runnableToTickMap = ((GameTestAccessor) gameTest).getField_21453();
        runnableToTickMap.put(() -> runnable.accept(gameTest), tick);
    }

    @Deprecated
    public static void addRunnableToTickRange(StartupParameter startupParameter, Consumer<net.minecraft.test.GameTest> runnable, int start, int end) {
        net.minecraft.test.GameTest gameTest = ((StartupParameterAccessor) startupParameter).getTest();
        Object2LongMap<Runnable> runnableToTickMap = ((GameTestAccessor) gameTest).getField_21453();
        for (int i = start; i <= end; i++) {
            //due to the usage of a Map we need to create a new runnable every time:
            Runnable runnable1 = () -> runnable.accept(gameTest);
            runnableToTickMap.put(runnable1, i);
        }
    }

    public TestFunction toTestFunction() {
        Consumer<StartupParameter> extendedStarter = (t) -> {
            //schedule the Runnables/Consumers we want to run during the test.
            for (int tick : this.actionsByTick.keySet()) {
                ArrayList<Consumer<net.minecraft.test.GameTest>> actions = this.actionsByTick.get(tick);
                if (actions != null && !actions.isEmpty()) {
                    addRunnableToTick(t, (net.minecraft.test.GameTest e) -> actions.forEach(action -> action.accept(e)), tick);
                }
            }
            //schedule the repeated actions to tick ranges
            if (!repeatedActions.isEmpty()) {
                addRunnableToTickRange(t, (net.minecraft.test.GameTest e) -> repeatedActions.forEach(action -> action.accept(e)), this.repeatedActionsStart, this.timeout + 1);
            }

            if (this.starter != null) {
                this.starter.accept(t);
            }
        };

        //todo fix using structureName twice here! But for some reason it is actually working best like this.
        return UnsafeUtil.createTestFunction(this.batchId, this.structureName, this.structureName, this.required, extendedStarter, this.timeout, this.cooldown, this.rotation, this.repetitions, this.requiredSuccessCount);
    }

    @Deprecated
    public TestConfig startWith(Consumer<StartupParameter> starter) {
        if (this.starter != null) {
            throw new IllegalStateException("Can only have one starter!");
        }
        this.starter = starter;
        return this;
    }

    public TestConfig batchId(String batchId) {
        this.batchId = batchId;
        return this;
    }

    public TestConfig structurePath(String structurePath) {
        this.structurePath = structurePath;
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

    @Deprecated
    public TestConfig addAction(int tick, Consumer<net.minecraft.test.GameTest> action) {
        ArrayList<Consumer<net.minecraft.test.GameTest>> actions = this.actionsByTick.computeIfAbsent(tick, (integer -> new ArrayList<>()));
        actions.add(action);
        return this;
    }

    public TestConfig repeatedActionsStart(int tick) {
        this.repeatedActionsStart = tick;
        return this;
    }

    @Deprecated
    public TestConfig addRepeatedAction(Consumer<net.minecraft.test.GameTest> action) {
        this.repeatedActions.add(action);
        return this;
    }

    @Deprecated
    public TestConfig addSuccessCondition(Function<net.minecraft.test.GameTest, Boolean> successCondition) {
        this.repeatedActions.add(e -> {
            if (successCondition.apply(e)) {
                e.fail(null);
            }
        });
        return this;
    }

    @Deprecated
    public TestConfig addFailCondition(Function<net.minecraft.test.GameTest, Boolean> failCondition, String message) {
        this.repeatedActions.add(e -> {
            if (failCondition.apply(e)) {
                e.fail(new Exception(message));
            }
        });
        return this;
    }
}
