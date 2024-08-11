package net.infernal_coding.villager_realism.mixins.tasks;

import net.minecraft.entity.ai.brain.task.UpdateActivityTask;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(UpdateActivityTask.class)
public abstract class UpdateActivityTaskMixin {
    /**
     * @author Infernal_Coding
     * @reason Meant to make a villager try sleep if too tired
     */
   /* @Overwrite
    protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
        if (entityIn instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) entityIn;
            IVillagerCapability capability = villager.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().get();
            Activity previousActivity = capability.getCurrentActivity();
            long ticksActive = gameTimeIn - capability.getActivityStartTick();

            if (previousActivity != Activity.WORK && Util.tickToDrowsinessMap.containsKey(previousActivity)) {
                double drowsiness = Util.tickToDrowsinessMap.get(previousActivity).apply(ticksActive);
                capability.increaseDrowsiness(drowsiness);
            } else if (previousActivity == Activity.WORK) {
                double drowsiness = Util.getWorkDrowsinessIncrease(ticksActive, villager.getVillagerData().getProfession());
                capability.increaseDrowsiness(drowsiness);
            }

            if (capability.getDrowsiness() >= 6) {
                try {
                    Util.switchActivity.invoke(villager.getBrain(), Activity.REST);
                    capability.setCurrentActivity(Activity.REST);
                    capability.setActivityStartTick(gameTimeIn);
                    return;
                } catch (Exception e) {
                    VillagerRealism.LOGGER.error(e.getMessage());
                }
            }
            entityIn.getBrain().updateActivity(worldIn.getDayTime(), worldIn.getGameTime());
            capability.setCurrentActivity(entityIn.getBrain().getSchedule().getScheduledActivity((int) worldIn.getDayTime()));
            capability.setActivityStartTick(worldIn.getGameTime());
            return;
        }
        entityIn.getBrain().updateActivity(worldIn.getDayTime(), worldIn.getGameTime());

    }*/

}
