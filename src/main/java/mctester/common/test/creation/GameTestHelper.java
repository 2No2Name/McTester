package mctester.common.test.creation;

import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import mctester.common.test.exceptions.GameTestAssertException;
import mctester.common.test.exceptions.PreconditionNotMetException;
import mctester.common.util.GameTestUtil;
import mctester.common.util.TestFunctionIdentification;
import mctester.mixin.accessor.BrainAccessor;
import mctester.mixin.accessor.GoalSelectorAccessor;
import mctester.mixin.accessor.MobEntityAccessor;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.test.GameTestException;
import net.minecraft.test.GameTestState;
import net.minecraft.test.PositionedException;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.*;

@SuppressWarnings("unused")
public class GameTestHelper {
    private static final Random RAND = new Random();

    public final GameTestState gameTest;
    private final ArrayList<BiConsumer<GameTestHelper, Long>> repeatedActions;
    private final Long2ReferenceOpenHashMap<Consumer<GameTestHelper>> tickActions;
    private final ArrayList<Function<GameTestHelper, RuntimeException>> failReasons;
    public long currTick;

    public GameTestHelper(GameTestState gameTest) {
        this.gameTest = gameTest;
        if (this.gameTest instanceof GameTestAccess) {
            ((GameTestAccess) this.gameTest).setTickCallback(this::handleTick);
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
        RuntimeException runtimeException = null;
        while (runtimeException == null) {
            if (this.failReasons.isEmpty()) {
                return;
            }
            runtimeException = this.failReasons.remove(RAND.nextInt(this.failReasons.size())).apply(this);
        }
        this.gameTest.fail(runtimeException);
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
                        } catch (PreconditionNotMetException ignored) {
                        } catch (Exception e) {
                            this.gameTest.fail(e);
                        }
                    }
                }
        );
    }

    public void succeedWhen(BooleanSupplier successCondition, Function<GameTestHelper, RuntimeException> timeoutException) {
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
                        } catch (PreconditionNotMetException ignored) {
                        } catch (GameTestAssertException | GameTestException e) {
                            this.gameTest.fail(e);
                        }
                    }
                }
        );
    }

    public void succeedWhen(Function<GameTestHelper, Boolean> successCondition, Function<GameTestHelper, RuntimeException> timeoutException) {
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
                        } catch (PreconditionNotMetException ignored) {
                        } catch (GameTestAssertException | GameTestException e) {
                            this.gameTest.fail(e);
                        }
                    }
                }
        );
    }

    public void succeedWhen(BiFunction<GameTestHelper, Long, Boolean> successCondition, Function<GameTestHelper, RuntimeException> timeoutException) {
        this.succeedWhen(successCondition);
        if (timeoutException != null) {
            this.failReasons.add(timeoutException);
        }
    }

    public <T extends Entity> void assertEntityPresent(EntityType<T> entityType, BlockPos targetPos) {
        List<T> entitiesInBox = GameTestUtil.getEntitiesInBox(this.gameTest, entityType, new Box(targetPos, targetPos.add(1, 1, 1)));
        if (entitiesInBox.isEmpty()) {
            throw PreconditionNotMetException.INSTANCE; //hack to get the same behavior as in the video (https://www.youtube.com/watch?v=vXaWOJTCYNg (11:15))
        }
    }

    public void walkTo(MobEntity mob, BlockPos targetPos) {
        mob.setOnGround(true);
        targetPos = GameTestUtil.transformRelativeToAbsolutePos(this.gameTest, targetPos);
        Path pathTo = mob.getNavigation().findPathTo(targetPos.getX() + 0.5D, targetPos.getY() + 1, targetPos.getZ() + 0.5D, 0);
        mob.getNavigation().startMovingAlong(pathTo, 1D);
    }

    public void walkTo(MobEntity mob, double x, double y, double z) {
        mob.setOnGround(true);
        Vec3d targetPos = GameTestUtil.transformRelativeToAbsolutePos(this.gameTest, x, y, z);
        Path pathTo = mob.getNavigation().findPathTo(targetPos.getX() + 0.5D, targetPos.getY() + 1, targetPos.getZ() + 0.5D, 0);
        mob.getNavigation().startMovingAlong(pathTo, 1D);
    }

    public <T extends Entity> T spawnWithNoFreeWill(EntityType<T> entityType, int x, int y, int z) {
        T entity = spawnEntity(x, y, z, entityType);

        //mobs have lots of brain, cut out the selection of tasks and goals
        if (entity instanceof MobEntity) {
            Brain<?> brain = ((MobEntity) entity).getBrain();
            ((BrainAccessor<?>) brain).getTasks().clear();
        }
        ((GoalSelectorAccessor) ((MobEntityAccessor) entity).getTargetSelector()).getGoals().clear();
        ((GoalSelectorAccessor) ((MobEntityAccessor) entity).getGoalSelector()).getGoals().clear();

        return entity;
    }

    public <T extends Entity> T spawnEntity(int x, int y, int z, EntityType<T> entity) {
        return GameTestUtil.spawnEntityWithTransforms(this.gameTest, x + 0.5D, y, z + 0.5D, entity, null);
    }

    public <T extends Entity> T spawnEntity(int x, int y, int z, EntityType<T> entity, NbtCompound tag) {
        return GameTestUtil.spawnEntityWithTransforms(this.gameTest, x + 0.5D, y, z + 0.5D, entity, tag);
    }

    public <T extends Entity> T spawnEntity(int x, int y, int z, EntityType<T> entity, NbtCompound... entityTags) {
        NbtCompound tag = new NbtCompound();
        for (NbtCompound entityTag : entityTags) {
            tag.copyFrom(entityTag);
        }
        return GameTestUtil.spawnEntityWithTransforms(this.gameTest, x + 0.5D, y, z + 0.5D, entity, tag);
    }

    public <T extends Entity> T spawnEntity(double x, double y, double z, EntityType<T> entity) {
        return GameTestUtil.spawnEntityWithTransforms(this.gameTest, x, y, z, entity, null);
    }

    public <T extends Entity> T spawnEntity(double x, double y, double z, EntityType<T> entity, NbtCompound entityTag) {
        return GameTestUtil.spawnEntityWithTransforms(this.gameTest, x, y, z, entity, entityTag);
    }

    public <T extends Entity> T spawnEntity(double x, double y, double z, EntityType<T> entity, NbtCompound... entityTags) {
        NbtCompound tag = new NbtCompound();
        for (NbtCompound entityTag : entityTags) {
            tag.copyFrom(entityTag);
        }
        return GameTestUtil.spawnEntityWithTransforms(this.gameTest, x, y, z, entity, tag);
    }

    public void pressButton(int x, int y, int z) {
        BlockState blockState = this.getBlockState(x, y, z);
        if (blockState.getBlock() instanceof AbstractButtonBlock) {
            BlockPos blockPos = GameTestUtil.transformRelativeToAbsolutePos(this.gameTest, x, y, z);
            blockState.onUse(this.gameTest.getWorld(), null, null, new BlockHitResult(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), Direction.DOWN, blockPos, false));
        } else {
            throw new PositionedException("No pushable button found.", GameTestUtil.transformRelativeToAbsolutePos(this.gameTest, new BlockPos(x, y, z)), new BlockPos(x, y, z), this.currTick);
        }
    }

    public <T extends Entity> void succeedWhenEntityCollides(EntityType<T> entityType, int x, int y, int z) {
        this.succeedWhen((GameTestHelper helper) ->
                        !GameTestUtil.getEntitiesInBox(
                                this.gameTest, entityType, new Box(x, y, z, x + 1, y + 1, z + 1)
                        ).isEmpty(),
                (GameTestHelper helper) -> new PositionedException("Expected " + entityType.getName().getString(), GameTestUtil.transformRelativeToAbsolutePos(this.gameTest, new BlockPos(x, y, z)), new BlockPos(x, y, z), helper.currTick)
        );
    }

    public <T extends Entity> void succeedWhenEntityCollides(EntityType<T> entityType, Box box) {
        this.succeedWhen((GameTestHelper helper) ->
                        !GameTestUtil.getEntitiesInBox(
                                this.gameTest, entityType, box
                        ).isEmpty(),
                (GameTestHelper helper) -> new PositionedException("Expected " + entityType.getName().getString(), GameTestUtil.transformRelativeToAbsolutePos(this.gameTest, new BlockPos(box.getCenter())), new BlockPos(box.getCenter()), helper.currTick)
        );
    }

    public <T extends Entity> void succeedWhenEntityPresent(EntityType<T> entityType, int x, int y, int z) {
        this.succeedWhen((GameTestHelper helper) -> {
                    List<T> entitiesInBox = GameTestUtil.getEntitiesInBox(
                            this.gameTest, entityType, new Box(x, y, z, x + 1, y + 1, z + 1)
                    );
                    BlockPos targetPos = GameTestUtil.transformRelativeToAbsolutePos(helper.gameTest, x, y, z);
                    for (int i = entitiesInBox.size() - 1; i >= 0; i--) {
                        T entity = entitiesInBox.get(i);
                        BlockPos blockPos = entity.getBlockPos();
                        if (targetPos.equals(blockPos)) {
                            return true;
                        }
                    }
                    return false;
                },
                (GameTestHelper helper) -> new PositionedException("Expected " + entityType.getName().getString(), GameTestUtil.transformRelativeToAbsolutePos(this.gameTest, new BlockPos(x, y, z)), new BlockPos(x, y, z), helper.currTick)
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

    public int getVariantIndex() {
        return TestFunctionIdentification.testFunction2VariantIndex.getOrDefault(this.gameTest.getTestFunction(), 0);
    }

    public interface GameTestAccess {
        GameTestHelper getGameTestHelper();

        void setTickCallback(LongConsumer handler);
    }
}
