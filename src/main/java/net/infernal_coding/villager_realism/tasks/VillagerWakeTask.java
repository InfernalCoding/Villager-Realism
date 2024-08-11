package net.infernal_coding.villager_realism.tasks;

import com.google.common.collect.ImmutableMap;
import net.infernal_coding.villager_realism.capability.IVillagerCapability;
import net.infernal_coding.villager_realism.capability.Providers;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.world.server.ServerWorld;
public class VillagerWakeTask extends Task<VillagerEntity> {
    public VillagerWakeTask() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
        IVillagerCapability capability = owner.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().get();

        return !owner.getBrain().hasActivity(Activity.REST) && owner.isSleeping() &&
                capability.getDrowsiness() < 5;
    }

    @Override
    protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
        entityIn.wakeUp();
    }
}
