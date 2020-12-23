package mctester.test;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.test.GameTest;
import net.minecraft.test.StartupParameter;
import net.minecraft.test.TestFunction;
import net.minecraft.test.TestFunctions;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class TestHelper {

    //work around fields being private
    private static final Field startupParameterGameTest = StartupParameter.class.getDeclaredFields()[0];
    private static final Field gameTestRunnableMap = GameTest.class.getDeclaredFields()[6];

    static {
        startupParameterGameTest.setAccessible(true);
        gameTestRunnableMap.setAccessible(true);
    }

    public static void registerTest(TestFunction testFunction) {
        TestFunctions.getTestFunctions().add(testFunction);
    }

    /**
     * Adds the runnable to the test by adding the add operation to the test startup function.
     * @param consumer test startup function
     * @param runnable the runnable to add, we allow consuming the GameTest to allow more flexible runnables
     * @param tick the tick after startup the runnable should be run
     * @return the startup function that will also add the runnable to the GameTest
     */
    public static Consumer<StartupParameter> addRunnableToTick(Consumer<StartupParameter> consumer, Consumer<GameTest> runnable, long tick) {
        return (StartupParameter startupParameter) -> {
            try {
                GameTest gameTest = (GameTest) startupParameterGameTest.get(startupParameter);
                //noinspection unchecked
                Object2LongMap<Runnable> runnableToTickMap = (Object2LongMap<Runnable>) gameTestRunnableMap.get(gameTest);
                runnableToTickMap.put(() -> runnable.accept(gameTest), tick);
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
            consumer.accept(startupParameter);
        };
    }

    public static Consumer<StartupParameter> addRunnableToTickRange(Consumer<StartupParameter> consumer, Consumer<GameTest> runnable, int start, int end) {
        return (StartupParameter startupParameter) -> {
            try {
                GameTest gameTest = (GameTest) startupParameterGameTest.get(startupParameter);
                //noinspection unchecked
                Object2LongMap<Runnable> runnableToTickMap = (Object2LongMap<Runnable>) gameTestRunnableMap.get(gameTest);
                Runnable runnable1 = () -> runnable.accept(gameTest);
                for (int i = start; i < end + 1; i++ ) {
                    runnableToTickMap.put(runnable1, i);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
            consumer.accept(startupParameter);
        };
    }

    public static void addRunnableToTick(StartupParameter startupParameter, Consumer<GameTest> runnable, long tick) {
        try {
            GameTest gameTest = (GameTest) startupParameterGameTest.get(startupParameter);
            //noinspection unchecked
            Object2LongMap<Runnable> runnableToTickMap = (Object2LongMap<Runnable>) gameTestRunnableMap.get(gameTest);
            runnableToTickMap.put(() -> runnable.accept(gameTest), tick);
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
    }

    public static void addRunnableToTickRange(StartupParameter startupParameter, Consumer<GameTest> runnable, int start, int end) {
        try {
            GameTest gameTest = (GameTest) startupParameterGameTest.get(startupParameter);
            //noinspection unchecked
            Object2LongMap<Runnable> runnableToTickMap = (Object2LongMap<Runnable>) gameTestRunnableMap.get(gameTest);
            for (int i = start; i <= end; i++ ) {
                Runnable runnable1 = () -> runnable.accept(gameTest);
                runnableToTickMap.put(runnable1, i);
            }
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
    }

    public static void setBlockStateWithTransforms(GameTest gameTest, int x, int y, int z, BlockState blockState) {
        ServerWorld serverWorld = gameTest.getWorld();
        BlockPos blockPos = gameTest.getPos();
        BlockPos blockPos2 = new BlockPos(x, y, z);
        BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, gameTest.method_29402(), blockPos);
        serverWorld.setBlockState(blockPos3, blockState.rotate(gameTest.method_29402()));

    }

    public static BlockState getBlockStateWithTransforms(GameTest gameTest, int x, int y, int z) {
        ServerWorld serverWorld = gameTest.getWorld();
        BlockPos blockPos = gameTest.getPos();
        BlockPos blockPos2 = new BlockPos(x, y, z);
        BlockPos blockPos3 = Structure.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, gameTest.method_29402(), blockPos);
        return serverWorld.getBlockState(blockPos3).rotate(getInverse(gameTest.method_29402()));
    }

    public static BlockRotation getInverse(BlockRotation blockRotation) {
        switch (blockRotation) {
            case NONE:
                return BlockRotation.NONE;
            case CLOCKWISE_90:
                return BlockRotation.COUNTERCLOCKWISE_90;
            case CLOCKWISE_180:
                return BlockRotation.CLOCKWISE_180;
            case COUNTERCLOCKWISE_90:
                return BlockRotation.CLOCKWISE_90;
            default:
                throw new IllegalStateException("Unexpected value: " + blockRotation);
        }
    }
}
