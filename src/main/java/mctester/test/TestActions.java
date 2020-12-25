package mctester.test;

import mctester.util.GameTestUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.test.GameTest;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class TestActions {

    public static Consumer<GameTest> setBlockState(int x, int y, int z, BlockState blockState) {
        return new SetBlockState(x, y, z, blockState);
    }
    public static Consumer<GameTest> getBlockState(int x, int y, int z, BiConsumer<GameTest,BlockState> blockStateConsumer) {
        return new GetBlockState(x, y, z, blockStateConsumer);
    }
    public static Consumer<GameTest> getEntities(BiConsumer<GameTest, List<Entity>> entityConsumer) {
        return new GetEntities(entityConsumer);
    }
    public static Consumer<GameTest> spawnEntity(int x, int y, int z, EntityType<?> entity) {
        return new SpawnEntityAction(x + 0.5D, y + 0.5D, z + 0.5D, entity);
    }
    public static Consumer<GameTest> spawnEntity(int x, int y, int z, EntityType<?> entity, CompoundTag entityTag) {
        return new SpawnEntityAction(x + 0.5F, y + 0.5F, z + 0.5F, entity, entityTag);
    }
    public static Consumer<GameTest> spawnEntity(double x, double y, double z, EntityType<?> entity) {
        return new SpawnEntityAction(x, y, z, entity);
    }
    public static Consumer<GameTest> spawnEntity(double x, double y, double z, EntityType<?> entity, CompoundTag entityTag) {
        return new SpawnEntityAction(x, y, z, entity, entityTag);
    }

    public static Stream<BlockPos> streamPositions(GameTest gameTest) {
        return BlockPos.stream(GameTestUtil.getTestBlockBox(gameTest));
    }


    public static class SetBlockState implements Consumer<GameTest> {
        final int x, y, z;
        final BlockState blockState;

        public SetBlockState(int x, int y, int z, BlockState blockState) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockState = blockState;
        }

        @Override
        public void accept(GameTest e) {
            GameTestUtil.setBlockStateWithTransforms(e, x, y, z, blockState);
        }
    }

    public static class GetBlockState implements Consumer<GameTest> {
        final int x, y, z;
        final BiConsumer<GameTest, BlockState> blockStateConsumer;

        public GetBlockState(int x, int y, int z, BiConsumer<GameTest,BlockState> blockStateConsumer) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockStateConsumer = blockStateConsumer;
        }

        @Override
        public void accept(GameTest e) {
            blockStateConsumer.accept(e, GameTestUtil.getBlockStateWithTransforms(e, x, y, z));
        }
    }

    public static class GetEntities implements Consumer<GameTest> {

        private final BiConsumer<GameTest, List<Entity>> entityConsumer;

        public GetEntities(BiConsumer<GameTest, List<Entity>> entityConsumer) {
            this.entityConsumer = entityConsumer;
        }

        @Override
        public void accept(GameTest e) {
            entityConsumer.accept(e, GameTestUtil.getEntitiesInTestArea(e));
        }
    }

    public static class SpawnEntityAction implements Consumer<GameTest> {
        final double x;
        final double y;
        final double z;
        final EntityType<? extends Entity> entityType;
        final CompoundTag entityTag;

        public SpawnEntityAction(double x, double y, double z, EntityType<? extends Entity> entityType) {
            this(x, y, z, entityType, null);
        }

        public SpawnEntityAction(double x, double y, double z, EntityType<? extends Entity> entityType, CompoundTag entityTag) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.entityType = entityType;
            this.entityTag = entityTag;
        }

        @Override
        public void accept(GameTest e) {
            GameTestUtil.spawnEntityWithTransforms(e, x, y, z, entityType, entityTag);
        }

    }
}
