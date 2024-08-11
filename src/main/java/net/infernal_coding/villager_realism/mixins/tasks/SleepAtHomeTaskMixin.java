package net.infernal_coding.villager_realism.mixins.tasks;

import net.infernal_coding.villager_realism.util.Util;
import net.infernal_coding.villager_realism.capability.IVillagerCapability;
import net.infernal_coding.villager_realism.capability.Providers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.InteractWithDoorTask;
import net.minecraft.entity.ai.brain.task.SleepAtHomeTask;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(SleepAtHomeTask.class)
public class SleepAtHomeTaskMixin {

   @Shadow private long field_220552_a;

    /**
     * @author Infernal_Coding
     * @reason Method to determine if a villager should continue sleeping
     */
    @Overwrite
    protected boolean shouldContinueExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
        Optional<GlobalPos> optional = entityIn.getBrain().getMemory(MemoryModuleType.HOME);
        if (!optional.isPresent()) {
            return false;
        } else {
            BlockPos blockpos = optional.get().getPos();

            if (!(entityIn instanceof VillagerEntity)) {
                return entityIn.getBrain().hasActivity(Activity.REST) && entityIn.getPosY() > (double) blockpos.getY() + 0.4D && blockpos.withinDistance(entityIn.getPositionVec(), 1.14D);
            }
            VillagerEntity villager = (VillagerEntity) entityIn;
            IVillagerCapability capability = villager.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().get();
            double sleepingMcHours = Util.ticksToMcHours(capability.getSleepStartTick() - gameTimeIn);
            double drowsinessDecrease = villager.isChild() ? Util.getChildDrowsinessDecrease(sleepingMcHours) :
                    Util.getAdultDrowsinessDecrease(sleepingMcHours);
            double currentDrowsiness = capability.getDrowsiness() - drowsinessDecrease;
            return currentDrowsiness >= 6 || worldIn.isNightTime();
        }
    }

    /**
     * @author Infernal_Coding
     * @reason Meant to make tired villagers go back to beck when woken
     */
    @Overwrite
    protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
        if (gameTimeIn > this.field_220552_a || entityIn instanceof VillagerEntity) {
            InteractWithDoorTask.func_242294_a(worldIn, entityIn, null, null);
            entityIn.startSleeping(entityIn.getBrain().getMemory(MemoryModuleType.HOME).get().getPos());
        }

    }

}
