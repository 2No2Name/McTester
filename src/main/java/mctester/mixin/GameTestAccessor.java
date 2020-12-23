package mctester.mixin;

import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.test.GameTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameTest.class)
public interface GameTestAccessor {
    @Accessor("field_27805")
    StructureBlockBlockEntity getStructureBlockBlockEntity();
}
