package mctester.common.util;

import mctester.mixin.accessor.GameTestAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.test.GameTestState;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.*;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class GameTestUtil {
    public static BlockBox getTestBlockBox(GameTestState gameTest) {
        return getTestBlockBox(((GameTestAccessor) gameTest).getStructureBlockBlockEntity());
    }

    public static Box getTestBox(GameTestState gameTest) {
        return getTestBox(((GameTestAccessor) gameTest).getStructureBlockBlockEntity());
    }

    private static BlockBox getTestBlockBox(StructureBlockBlockEntity structureBlockEntity) {
        BlockPos blockPos = structureBlockEntity.getPos().add(structureBlockEntity.getOffset());
        BlockPos blockPos2 = blockPos.add(structureBlockEntity.getSize().add(-1, -1, -1));
        BlockPos blockPos3 = StructureTemplate.transformAround(blockPos2, BlockMirror.NONE, structureBlockEntity.getRotation(), blockPos);
        return BlockBox.create(blockPos, blockPos3);
    }

    private static Box getTestBox(StructureBlockBlockEntity structureBlockEntity) {
        BlockPos blockPos = structureBlockEntity.getPos().add(structureBlockEntity.getOffset());
        BlockPos blockPos2 = blockPos.add(structureBlockEntity.getSize().add(-1, -1, -1));
        BlockPos blockPos3 = StructureTemplate.transformAround(blockPos2, BlockMirror.NONE, structureBlockEntity.getRotation(), blockPos);
        return Box.enclosing(blockPos, blockPos3);
    }


    public static Box transformBox(GameTestState gameTest, Box box) {
        BlockPos blockPos = gameTest.getPos();
        Vec3d corner1 = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d corner2 = new Vec3d(box.maxX, box.maxY, box.maxZ);

        corner1 = StructureTemplate.transformAround(corner1.add(blockPos.getX(), blockPos.getY(), blockPos.getZ()), BlockMirror.NONE, gameTest.getRotation(), blockPos);
        corner2 = StructureTemplate.transformAround(corner2.add(blockPos.getX(), blockPos.getY(), blockPos.getZ()), BlockMirror.NONE, gameTest.getRotation(), blockPos);
        return new Box(corner1, corner2);
    }

    public static Vec3d transformRelativeToAbsolutePos(GameTestState gameTest, Vec3d pos) {
        BlockPos basePos = gameTest.getPos();
        return StructureTemplate.transformAround(pos.add(basePos.getX(), basePos.getY(), basePos.getZ()), BlockMirror.NONE, gameTest.getRotation(), basePos);
    }

    public static Vec3d transformRelativeToAbsolutePos(GameTestState gameTest, double x, double y, double z) {
        return transformRelativeToAbsolutePos(gameTest, new Vec3d(x, y, z));
    }

    public static BlockPos transformRelativeToAbsolutePos(GameTestState gameTest, Vec3i pos) {
        BlockPos blockPos = gameTest.getPos();
        return StructureTemplate.transformAround(blockPos.add(pos), BlockMirror.NONE, gameTest.getRotation(), blockPos);
    }

    public static BlockPos transformRelativeToAbsolutePos(GameTestState gameTest, int x, int y, int z) {
        BlockPos blockPos = gameTest.getPos();
        return StructureTemplate.transformAround(blockPos.add(x, y, z), BlockMirror.NONE, gameTest.getRotation(), blockPos);
    }

    public static List<Entity> getEntitiesInTestArea(GameTestState e) {
        return e.getWorld().getOtherEntities(null, GameTestUtil.getTestBox(e));
    }

    public static List<Entity> getEntitiesInBox(GameTestState e, Box box) {
        box = transformBox(e, box);
        return e.getWorld().getOtherEntities(null, GameTestUtil.transformBox(e, box));
    }

    public static <T extends Entity> List<T> getEntitiesInBox(GameTestState e, EntityType<T> entityType, Box box) {
        box = transformBox(e, box);
        return e.getWorld().getEntitiesByType(entityType, box, t -> true);
    }

    public static <T extends Entity> List<T> getEntitiesInBox(GameTestState e, EntityType<T> entityType, Box box, Predicate<T> entityPredicate) {
        box = transformBox(e, box);
        if (entityPredicate == null) {
            entityPredicate = t -> true;
        }
        return e.getWorld().getEntitiesByType(entityType, box, entityPredicate);
    }

    public static <T extends Entity> T spawnEntityWithTransforms(GameTestState gameTest, double x, double y, double z, EntityType<T> entityType, NbtCompound entityTag) {
        ServerWorld serverWorld = gameTest.getWorld();
        T entity = entityType.create(serverWorld);
        if (entity == null) {
            throw new IllegalStateException("Could not create entity of Type " + entityType);
        }
        if (entity instanceof MobEntity) {
            ((MobEntity) entity).initialize(serverWorld, serverWorld.getLocalDifficulty(entity.getBlockPos()), SpawnReason.COMMAND, null);
        }

        if (entityTag != null) {
            NbtCompound newTag = NbtPredicate.entityToNbt(entity).copy().copyFrom(entityTag);
            entity.readNbt(newTag);
        }

        BlockPos blockPos = gameTest.getPos();
        Vec3d pos2 = new Vec3d(x, y, z);
        //transform the position so the spawning works
        BlockRotation blockRotation = gameTest.getRotation();
        Vec3d pos3 = StructureTemplate.transformAround(pos2.add(blockPos.getX(), blockPos.getY(), blockPos.getZ()), BlockMirror.NONE, blockRotation, blockPos);
        float yaw = entity.getYaw();
        switch (blockRotation) {
            case NONE:
                break;
            case CLOCKWISE_90:
                yaw = (yaw + 90f) % 360;
                break;
            case CLOCKWISE_180:
                yaw = (yaw + 180f) % 360;
                break;
            case COUNTERCLOCKWISE_90:
                yaw = (yaw - 90f) % 360;
                break;
        }
        entity.refreshPositionAndAngles(pos3.getX(), pos3.getY(), pos3.getZ(), yaw, entity.getPitch());
        serverWorld.spawnEntity(entity);
        return entity;
    }

    public static void setBlockStateWithTransforms(GameTestState gameTest, int x, int y, int z, BlockState blockState) {
        ServerWorld serverWorld = gameTest.getWorld();
        BlockPos blockPos = gameTest.getPos();
        BlockPos blockPos2 = new BlockPos(x, y, z);
        BlockPos blockPos3 = StructureTemplate.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, gameTest.getRotation(), blockPos);
        serverWorld.setBlockState(blockPos3, blockState.rotate(gameTest.getRotation()));
    }

    public static BlockState getBlockStateWithTransforms(GameTestState gameTest, int x, int y, int z) {
        ServerWorld serverWorld = gameTest.getWorld();
        BlockPos blockPos = gameTest.getPos();
        BlockPos blockPos2 = new BlockPos(x, y, z);
        BlockPos blockPos3 = StructureTemplate.transformAround(blockPos.add(blockPos2), BlockMirror.NONE, gameTest.getRotation(), blockPos);
        return serverWorld.getBlockState(blockPos3).rotate(getInverse(gameTest.getRotation()));
    }

    public static Stream<BlockPos> streamPositions(GameTestState gameTest) {
        return BlockPos.stream(GameTestUtil.getTestBlockBox(gameTest));
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
