package mctester.common.test;

import mctester.common.test.creation.GameTestHelper;

import java.util.function.BiConsumer;

public class SuccessCondition {
    BiConsumer<GameTestHelper, Long> successCondition;
}
