package net.infernal_coding.villager_realism.capability;

import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Storage {

    public static class VillagerStorage
            implements Capability.IStorage<IVillagerCapability> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<IVillagerCapability> capability, IVillagerCapability instance, Direction side) {
            CompoundNBT nbt = new CompoundNBT();
            Optional<Activity> optional = Optional.ofNullable(instance.getCurrentActivity());
            String activity = optional.map(Activity::getKey).orElse("");

            nbt.putDouble("drowsiness", instance.getDrowsiness());
            nbt.putLong("activityStartTick", instance.getActivityStartTick());
            nbt.putLong("sleepStartTick", instance.getSleepStartTick());
            nbt.putString("activity", activity);

            nbt.putInt("foodLevel", instance.getFoodLevel());
            nbt.putInt("foodTimer", instance.getFoodTimer());
            nbt.putFloat("saturationLevel", instance.getFoodSaturationLevel());
            nbt.putFloat("foodExhaustionLevel", instance.getFoodExhaustionLevel());




            return nbt;
        }

        @Override
        public void readNBT(Capability<IVillagerCapability> capability, IVillagerCapability instance, Direction side, INBT nbt) {
            CompoundNBT tag = (CompoundNBT) nbt;
            String activityKey = tag.getString("activity");
            Activity activity = Registry.ACTIVITY.getOrDefault(new ResourceLocation(activityKey));

            instance.setDrowsiness(tag.getDouble("drowsiness"));
            instance.setSleepStartTick(tag.getLong("sleepStartTick"));
            instance.setActivityStartTick(tag.getLong("activityStartTick"));
            instance.setCurrentActivity(activity);

            instance.setFoodLevel(tag.getInt("foodLevel"));
            instance.setFoodTimer(tag.getInt("foodTimer"));
            instance.setFoodSaturationLevel(tag.getFloat("saturationLevel"));
            instance.setFoodExhaustionLevel(tag.getFloat("foodExhaustionLevel"));
        }
    }
}
