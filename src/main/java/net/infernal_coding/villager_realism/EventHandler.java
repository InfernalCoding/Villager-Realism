package net.infernal_coding.villager_realism;

import net.infernal_coding.villager_realism.capability.IVillagerCapability;
import net.infernal_coding.villager_realism.capability.Providers;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = VillagerRealism.MOD_ID)
public class EventHandler {

    @SubscribeEvent
    public static void addMobCapabilities(AttachCapabilitiesEvent<Entity> event) {

        if (event.getObject() instanceof VillagerEntity) {
            event.addCapability(new ResourceLocation(VillagerRealism.MOD_ID, "villager"),
                    new Providers.VillagerCapabilityProvider());
        }
    }

}
