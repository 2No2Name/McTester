package mctester.mixin.accessor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.Task;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

@Mixin(Brain.class)
public interface BrainAccessor<E extends LivingEntity> {
    @Accessor("tasks")
    Map<Integer, Map<Activity, Set<Task<? super E>>>> getTasks();
}
