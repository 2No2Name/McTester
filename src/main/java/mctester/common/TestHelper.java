package mctester.common;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import mctester.mixin.GameTestAccessor;
import mctester.mixin.StartupParameterAccessor;
import net.minecraft.test.GameTest;
import net.minecraft.test.StartupParameter;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;

import java.util.function.Consumer;

public class TestHelper {

    public static void registerTest(TestFunction testFunction, String className) {
        TestFunctions.getTestFunctions().add(testFunction);
        if (!className.equals("")) {
            TestFunctions.getTestClasses().add(className);
        }
    }

    /**
     * Adds the runnable to the test.
     * @param startupParameter context of the test
     * @param runnable the runnable to add, we allow consuming the GameTest to allow more flexible runnables
     * @param tick the tick after startup the runnable should be run
     */
    public static void addRunnableToTick(StartupParameter startupParameter, Consumer<GameTest> runnable, long tick) {
        GameTest gameTest = ((StartupParameterAccessor)startupParameter).getTest();
        Object2LongMap<Runnable> runnableToTickMap = ((GameTestAccessor)gameTest).getField_21453();
        runnableToTickMap.put(() -> runnable.accept(gameTest), tick);
    }

    public static void addRunnableToTickRange(StartupParameter startupParameter, Consumer<GameTest> runnable, int start, int end) {
        GameTest gameTest = ((StartupParameterAccessor)startupParameter).getTest();
        Object2LongMap<Runnable> runnableToTickMap = ((GameTestAccessor)gameTest).getField_21453();
        for (int i = start; i <= end; i++ ) {
            Runnable runnable1 = () -> runnable.accept(gameTest);
            runnableToTickMap.put(runnable1, i);
        }
    }

}
