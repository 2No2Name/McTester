package mctester.test;

import mctester.util.GameTestUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.test.GameTest;

import java.util.List;
import java.util.function.BiConsumer;

public class TestActions {
    public interface GameTestAction {
        void run(GameTest e);
    }

    public static class SetBlockState implements GameTestAction {
        final int x, y, z;
        final BlockState blockState;

        public SetBlockState(int x, int y, int z, BlockState blockState) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockState = blockState;
        }

        public void run(GameTest e) {
            TestHelper.setBlockStateWithTransforms(e, x, y, z, blockState);
        }
    }

    public static class GetBlockState implements GameTestAction {
        final int x, y, z;
        final BiConsumer<GameTest, BlockState> blockStateConsumer;

        public GetBlockState(int x, int y, int z, BiConsumer<GameTest,BlockState> blockStateConsumer) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockStateConsumer = blockStateConsumer;
        }

        public void run(GameTest e) {
            blockStateConsumer.accept(e, TestHelper.getBlockStateWithTransforms(e, x, y, z));
        }
    }

    public static class GetEntities implements GameTestAction {

        private final BiConsumer<GameTest, List<Entity>> entityConsumer;

        public GetEntities(BiConsumer<GameTest, List<Entity>> entityConsumer) {
            this.entityConsumer = entityConsumer;
        }

        public void run(GameTest e) {
            entityConsumer.accept(e, GameTestUtil.getEntitiesInTestArea(e));
        }
    }
}
