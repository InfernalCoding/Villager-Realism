package net.infernal_coding.villager_realism;

import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registry {

    static final DeferredRegister<Activity> activities = DeferredRegister.create(ForgeRegistries.ACTIVITIES, VillagerRealism.MOD_ID);
    public static final RegistryObject<Activity> STARVE = activities.register("starve", () ->
           new Activity("starve"));

    static void register(IEventBus eventBus) {
       activities.register(eventBus);
   }

}
