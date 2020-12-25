package mctester.util;

import mctester.mixin.GameTestAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.test.GameTest;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class GameTestUtil {
    public static BlockBox getTestBlockBox(GameTest gameTest) {
        return StructureTestUtil.method_29410(((GameTestAccessor)gameTest).getStructureBlockBlockEntity());
    }

    public static Box getTestBox(GameTest gameTest) {
        return StructureTestUtil.getStructureBoundingBox(((GameTestAccessor)gameTest).getStructureBlockBlockEntity());
    }

    public static Box transformBox(GameTest gameTest, Box box) {
        ServerWorld serverWorld = gameTest.getWorld();
        BlockPos blockPos = gameTest.getPos();
        Vec3d corner1 = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d corner2 = new Vec3d(box.maxX, box.maxY, box.maxZ);


        //transform the position so the spawning works
        corner1 = Structure.transformAround(corner1.add(blockPos.getX(), blockPos.getY(), blockPos.getZ()), BlockMirror.NONE, gameTest.method_29402(), blockPos);
        corner2 = Structure.transformAround(corner2.add(blockPos.getX(), blockPos.getY(), blockPos.getZ()), BlockMirror.NONE, gameTest.method_29402(), blockPos);
        return new Box(corner1, corner2);
    }


    public static List<Entity> getEntitiesInTestArea(GameTest e) {
        return e.getWorld().getOtherEntities(null, GameTestUtil.getTestBox(e));
    }

    public static List<Entity> getEntitiesInBox(GameTest e, Box box) {
        return e.getWorld().getOtherEntities(null, GameTestUtil.transformBox(e, box));
    }

    public static void spawnEntityWithTransforms(GameTest gameTest, double x, double y, double z, EntityType<? extends Entity> entityType, CompoundTag entityTag) {
        ServerWorld serverWorld = gameTest.getWorld();
        Entity entity = entityType.create(serverWorld);
        if (entity == null) {
            throw new IllegalStateException("Could not create entity of Type");
        }
        if (entity instanceof MobEntity) {
            ((MobEntity)entity).initialize(serverWorld, serverWorld.getLocalDifficulty(entity.getBlockPos()), SpawnReason.COMMAND, (EntityData)null, (CompoundTag)null);
        }

        if (entityTag != null) {
            CompoundTag newTag = NbtPredicate.entityToTag(entity).copy().copyFrom(entityTag);
            entity.fromTag(newTag);
        }

        BlockPos blockPos = gameTest.getPos();
        Vec3d pos2 = new Vec3d(x, y, z);
        //transform the position so the spawning works
        Vec3d pos3 = Structure.transformAround(pos2.add(blockPos.getX(), blockPos.getY(), blockPos.getZ()), BlockMirror.NONE, gameTest.method_29402(), blockPos);
        entity.refreshPositionAfterTeleport(pos3.getX(), pos3.getY(), pos3.getZ());
        //todo rotate entity, gameTest.method_29402()
        serverWorld.spawnEntity(entity);
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
