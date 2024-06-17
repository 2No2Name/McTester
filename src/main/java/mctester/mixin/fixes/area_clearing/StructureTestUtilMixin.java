package mctester.mixin.fixes.area_clearing;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(StructureTestUtil.class)
public class StructureTestUtilMixin {

    @Inject(
            method = "clearArea", at = @At("RETURN")
    )
    private static void clearEntitiesAgain(BlockBox area, ServerWorld world, CallbackInfo ci, @Local Box box) {
        List<Entity> list = world.getEntitiesByClass(Entity.class, box, entity -> !(entity instanceof PlayerEntity));
        list.forEach(e -> e.remove(Entity.RemovalReason.CHANGED_DIMENSION));
    }
}
