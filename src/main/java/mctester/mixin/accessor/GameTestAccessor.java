package mctester.mixin.accessor;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.test.GameTestState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameTestState.class)
public interface GameTestAccessor {
    @Accessor("field_21453")
    Object2LongMap<Runnable> getField_21453();
    @Accessor("structureBlockEntity")
    StructureBlockBlockEntity getStructureBlockBlockEntity();
}
