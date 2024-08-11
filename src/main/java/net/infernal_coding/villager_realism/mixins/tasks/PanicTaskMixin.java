package net.infernal_coding.villager_realism.mixins.tasks;

import net.infernal_coding.villager_realism.Registry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.PanicTask;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static net.minecraft.entity.ai.brain.task.PanicTask.hasBeenHurt;
import static net.minecraft.entity.ai.brain.task.PanicTask.hostileNearby;

@Mixin(PanicTask.class)
public class PanicTaskMixin{
    /**
     * @author Infernal_Coding
     * @reason Meant to make villagers eat when starving
     */
    @Overwrite
    protected void startExecuting(ServerWorld world, VillagerEntity villager, long gameTimeIn) {
        if (hasBeenHurt(villager) || hostileNearby(villager)) {
            Brain<?> brain = villager.getBrain();

            if (!brain.hasActivity(Activity.PANIC)) {
                brain.removeMemory(MemoryModuleType.PATH);
                brain.removeMemory(MemoryModuleType.WALK_TARGET);
                brain.removeMemory(MemoryModuleType.LOOK_TARGET);
                brain.removeMemory(MemoryModuleType.BREED_TARGET);
                brain.removeMemory(MemoryModuleType.INTERACTION_TARGET);
            }

            if (villager.getBrain().getMemory(MemoryModuleType.HURT_BY).orElse(null) == DamageSource.STARVE) {
                brain.switchTo(Registry.STARVE.get());
                return;
            }
            brain.switchTo(Activity.PANIC);
        }
    }

}
