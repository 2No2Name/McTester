package mctester.mixin.accessor;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.test.GameTestState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameTestState.class)
public interface GameTestAccessor {
    @Accessor("ticksByRunnables")
    Object2LongMap<Runnable> getTicksByRunnables();

    @Accessor("structureBlockEntity")
    StructureBlockBlockEntity getStructureBlockBlockEntity();
}
