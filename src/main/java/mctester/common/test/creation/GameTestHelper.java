package mctester.common.test.creation;

import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import mctester.common.copy.PositionedException2;
import mctester.common.test.exceptions.GameTestAssertException;
import mctester.common.test.exceptions.NotEvaluatedException;
import mctester.common.util.GameTestUtil;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.test.GameTest;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.*;
import java.util.stream.Stream;

public class GameTestHelper {
    private static final Random RAND = new Random();

    public final GameTest gameTest;
    private final ArrayList<BiConsumer<GameTestHelper, Long>> repeatedActions;
    private final Long2ReferenceOpenHashMap<Consumer<GameTestHelper>> tickActions;
    private final ArrayList<Supplier<RuntimeException>> failReasons;
    public long currTick;

    public GameTestHelper(GameTest gameTest) {
        this.gameTest = gameTest;
        if (this.gameTest instanceof GameTestHelperAccess) {
            ((GameTestHelperAccess) this.gameTest).setTickCallback(this::handleTick);
        }
        this.repeatedActions = new ArrayList<>();
        this.tickActions = new Long2ReferenceOpenHashMap<>();
        this.failReasons = new ArrayList<>();
    }

    private void handleTick(long tick) {
        this.currTick = tick;
        Consumer<GameTestHelper> tickAction = this.tickActions.get(tick);
        if (tickAction != null) {
            tickAction.accept(this);
        }
        if (!this.repeatedActions.isEmpty()) {
            for (BiConsumer<GameTestHelper, Long> gameTestHelperReferenceLongConsumer : this.repeatedActions) {
                gameTestHelperReferenceLongConsumer.accept(this, tick);
            }
        }
        if (!this.gameTest.isCompleted() && this.gameTest.getTestFunction().getTickLimit() <= tick) {
            this.handleTimeout();
        }
    }

    private void handleTimeout() {
        if (!this.failReasons.isEmpty()) {
            this.gameTest.fail(this.failReasons.get(RAND.nextInt(this.failReasons.size())).get());
        }
    }

    public void addAction(long tick, Consumer<GameTestHelper> action) {
        Consumer<GameTestHelper> tickRunnable = this.tickActions.get(tick);
        if (tickRunnable == null) {
            tickRunnable = action;
        } else {
            tickRunnable = tickRunnable.andThen(action);
        }
        this.tickActions.put(tick, tickRunnable);
    }

    public void addRepeatedAction(BiConsumer<GameTestHelper, Long> runnable) {
        this.repeatedActions.add(runnable);
    }

    public void succeedWhen(BooleanSupplier successCondition) {
        this.addRepeatedAction(
                (GameTestHelper helper, Long tick) -> {
                    if (!helper.gameTest.isCompleted()) {
                        try {
                            boolean isSuccess = successCondition.getAsBoolean();
                            if (isSuccess) {
                                this.gameTest.fail(null);
                            }
                        } catch (NotEvaluatedException ignored) {
                        } catch (GameTestAssertException e) {
                            this.gameTest.fail(e);
                        }
                    }
                }
        );
    }

    public void succeedWhen(BooleanSupplier successCondition, Supplier<RuntimeException> timeoutException) {
        this.succeedWhen(successCondition);
        if (timeoutException != null) {
            this.failReasons.add(timeoutException);
        }
    }

    public void succeedWhen(Function<GameTestHelper, Boolean> successCondition) {
        this.addRepeatedAction(
                (GameTestHelper helper, Long tick) -> {
                    if (!helper.gameTest.isCompleted()) {
                        try {
                            boolean isSuccess = successCondition.apply(helper);
                            if (isSuccess) {
                                this.gameTest.fail(null);
                            }
                        } catch (NotEvaluatedException ignored) {
                        } catch (GameTestAssertException e) {
                            this.gameTest.fail(e);
                        }
                    }
                }
        );
    }

    public void succeedWhen(Function<GameTestHelper, Boolean> successCondition, Supplier<RuntimeException> timeoutException) {
        this.succeedWhen(successCondition);
        if (timeoutException != null) {
            this.failReasons.add(timeoutException);
        }
    }

    public void succeedWhen(BiFunction<GameTestHelper, Long, Boolean> successCondition) {
        this.addRepeatedAction(
                (GameTestHelper helper, Long tick) -> {
                    if (!helper.gameTest.isCompleted()) {
                        try {
                            boolean isSuccess = successCondition.apply(helper, tick);
                            if (isSuccess) {
                                this.gameTest.fail(null);
                            }
                        } catch (NotEvaluatedException ignored) {
                        } catch (GameTestAssertException e) {
                            this.gameTest.fail(e);
                        }
                    }
                }
        );
    }

    public void succeedWhen(BiFunction<GameTestHelper, Long, Boolean> successCondition, Supplier<RuntimeException> timeoutException) {
        this.succeedWhen(successCondition);
        if (timeoutException != null) {
            this.failReasons.add(timeoutException);
        }
    }

    public <T extends Entity> void assertEntityPresent(EntityType<T> entityType, BlockPos targetPos) {
        List<T> entitiesInBox = GameTestUtil.getEntitiesInBox(this.gameTest, entityType, new Box(targetPos, targetPos.add(1, 1, 1)));
        if (entitiesInBox.isEmpty()) {
            throw NotEvaluatedException.INSTANCE; //hack to get the same behavior as in the video (https://www.youtube.com/watch?v=vXaWOJTCYNg (11:15))
        }
    }

    public void walkTo(MobEntity mob, BlockPos targetPos) {
        //todo fix this, doesn't seem to work
        targetPos = GameTestUtil.transformPos(this.gameTest, targetPos);
        mob.getNavigation().startMovingTo(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D, 1D);
    }

    public void walkTo(MobEntity mob, double x, double y, double z) {
        Vec3d pos = GameTestUtil.transformPos(this.gameTest, x, y, z);
        mob.getNavigation().startMovingTo(pos.x, pos.y, pos.z, 1D);
    }

    public <T extends Entity> T spawnWithNoFreeWill(EntityType<T> entityType, int x, int y, int z) {
        T entity = spawnEntity(x, y, z, entityType);
        //todo make entity have no free will
        return entity;
    }

    public <T extends Entity> T spawnEntity(int x, int y, int z, EntityType<T> entity) {
        return GameTestUtil.spawnEntityWithTransforms(this.gameTest, x + 0.5D, y, z + 0.5D, entity, null);
    }

    public <T extends Entity> T spawnEntity(int x, int y, int z, EntityType<T> entity, CompoundTag tag) {
        return GameTestUtil.spawnEntityWithTransforms(this.gameTest, x + 0.5D, y, z + 0.5D, entity, tag);
    }

    public <T extends Entity> T spawnEntity(int x, int y, int z, EntityType<T> entity, CompoundTag... entityTags) {
        CompoundTag tag = new CompoundTag();
        for (CompoundTag entityTag : entityTags) {
            tag.copyFrom(entityTag);
        }
        return GameTestUtil.spawnEntityWithTransforms(this.gameTest, x + 0.5D, y, z + 0.5D, entity, tag);
    }

    public <T extends Entity> T spawnEntity(double x, double y, double z, EntityType<T> entity) {
        return GameTestUtil.spawnEntityWithTransforms(this.gameTest, x, y, z, entity, null);
    }

    public <T extends Entity> T spawnEntity(double x, double y, double z, EntityType<T> entity, CompoundTag entityTag) {
        return GameTestUtil.spawnEntityWithTransforms(this.gameTest, x, y, z, entity, entityTag);
    }

    public <T extends Entity> T spawnEntity(double x, double y, double z, EntityType<T> entity, CompoundTag... entityTags) {
        CompoundTag tag = new CompoundTag();
        for (CompoundTag entityTag : entityTags) {
            tag.copyFrom(entityTag);
        }
        return GameTestUtil.spawnEntityWithTransforms(this.gameTest, x, y, z, entity, tag);
    }

    public void pressButton(int x, int y, int z) {
        BlockState blockState = this.getBlockState(x, y, z);
        if (blockState.getBlock() instanceof AbstractButtonBlock) {
            BlockPos blockPos = GameTestUtil.transformPos(this.gameTest, x, y, z);
            blockState.onUse(this.gameTest.getWorld(), null, null, new BlockHitResult(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), Direction.DOWN, blockPos, false));
        } else {
            throw new PositionedException2("No pushable button found.", this.gameTest, new BlockPos(x, y, z), this.currTick);
        }
    }

    public <T extends Entity> void succeedWhenEntityPresent(EntityType<T> entityType, int x, int y, int z) {
        this.succeedWhen((GameTestHelper helper) ->
                        !GameTestUtil.getEntitiesInBox(
                                this.gameTest, entityType, new Box(x, y, z, x + 1, y + 1, z + 1)
                        ).isEmpty(),
                () -> new PositionedException2("Expected " + entityType.getName().getString(), this.gameTest, new BlockPos(x, y, z), this.currTick)
        );
    }

    public void setBlockState(int x, int y, int z, BlockState blockState) {
        GameTestUtil.setBlockStateWithTransforms(this.gameTest, x, y, z, blockState);
    }

    public BlockState getBlockState(int x, int y, int z) {
        return GameTestUtil.getBlockStateWithTransforms(this.gameTest, x, y, z);
    }

    public List<Entity> getEntitiesInside() {
        return GameTestUtil.getEntitiesInTestArea(this.gameTest);
    }

    public Stream<BlockPos> streamPositions() {
        return BlockPos.stream(GameTestUtil.getTestBlockBox(this.gameTest));
    }

    public interface GameTestHelperAccess {
        GameTestHelper getGameTestHelper();

        void setTickCallback(LongConsumer handler);
    }
}
