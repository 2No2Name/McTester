package mctester.test;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mctester.annotation.Test;
import mctester.util.UnsafeUtil;
import net.minecraft.test.GameTest;
import net.minecraft.test.StartupParameter;
import net.minecraft.test.TestFunction;
import net.minecraft.util.BlockRotation;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class TestConfig {

    private String batchId = "defaultbatch";
    private String structurePath = "";
    private String structureName;
    private long cooldown = 0; //time between loading the structure and starting the test
    private int timeout = 400; //time after which the test automatically fails
    private boolean required = true;
    private BlockRotation rotation = BlockRotation.NONE;
    private Consumer<StartupParameter> starter;
    private int repetitions;
    private int requiredSuccessCount;

    private final Int2ObjectOpenHashMap<ArrayList<Consumer<GameTest>>> actionsByTick = new Int2ObjectOpenHashMap<>();
    private final ArrayList<Consumer<GameTest>> repeatedActions = new ArrayList<>();


    public TestConfig() {

    }

    public static TestConfig from(Test annotation) {
        String structurePath = annotation.groupName();
        if (!structurePath.equals("")) {
            structurePath = structurePath + ".";
        }
        structurePath = structurePath + annotation.structureName();

        return new TestConfig()
                .required(annotation.required())
                .batchId(annotation.batchId())
                .structurePlaceCooldown(annotation.cooldown())
//                .structurePath(structurePath) //todo for some reason using path as name is better too
                .structureName(structurePath)
                .timeout(annotation.timeout())
                .rotation(annotation.rotation()).repetitions(annotation.repetitions()).requiredSuccessCount(annotation.requiredSuccessCount());
    }

    public TestFunction build() {
        Consumer<StartupParameter> extendedStarter = (t) -> {
            //schedule the Runnables/Consumers we want to run during the test.
            for (int tick : this.actionsByTick.keySet()) {
                ArrayList<Consumer<GameTest>> actions = this.actionsByTick.get(tick);
                if (actions != null && !actions.isEmpty()) {
                    TestHelper.addRunnableToTick(t, (GameTest e) -> actions.forEach(action -> action.accept(e)), tick);
                }
            }
            //schedule the repeated actions to tick ranges
            if (!repeatedActions.isEmpty()) {
                TestHelper.addRunnableToTickRange(t, (GameTest e) -> repeatedActions.forEach(action -> action.accept(e)), 0, this.timeout + 1);
            }

            if (this.starter != null) {
                this.starter.accept(t);
            }
        };

        //todo fix using structureName twice here! But for some reason it is actually working best like this.
        return UnsafeUtil.createTestFunction(this.batchId, this.structureName, this.structureName, this.required, extendedStarter, this.timeout, this.cooldown, this.rotation, this.repetitions, this.requiredSuccessCount);
    }

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


    public TestConfig addAction(int tick, Consumer<GameTest> action) {
        ArrayList<Consumer<GameTest>> actions = this.actionsByTick.computeIfAbsent(tick, (integer -> new ArrayList<>()));
        actions.add(action);
        return this;
    }

    public TestConfig addRepeatedAction(Consumer<GameTest> action) {
        this.repeatedActions.add(action);
        return this;
    }

    public TestConfig addSuccessCondition(Function<GameTest, Boolean> successCondition) {
        this.repeatedActions.add(e -> {
            if (successCondition.apply(e)) {
                e.fail(null);
            }
        });
        return this;
    }

    public TestConfig addFailCondition(Function<GameTest, Boolean> failCondition, String message) {
        this.repeatedActions.add(e -> {
            if (failCondition.apply(e)) {
                e.fail(new Exception(message));
            }
        });
        return this;
    }
}
