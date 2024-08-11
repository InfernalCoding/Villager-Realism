package net.infernal_coding.villager_realism.util;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class Util {

    public static final UUID SLOW_FALLING_ID = UUID.fromString("A5B6CF2A-2F7C-31EF-9022-7C3E7D5E6ABA");
    public static final AttributeModifier SLOW_FALLING = new AttributeModifier(SLOW_FALLING_ID, "Slow falling acceleration reduction", -0.07, AttributeModifier.Operation.ADDITION); // Add -0.07 to 0.08 so we get the vanilla default of 0.01

    public static final Method switchActivity = ObfuscationReflectionHelper.findMethod(Brain.class, "func_233713_d_", Activity.class);
    public static final Method getWaterSlowDown = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "func_189749_co");

    public static final Map<Activity, Function<Long, Double>> tickToDrowsinessMap = new HashMap<>();
    public static final Map<VillagerProfession, Function<Double, Double>> workedTicksToDrowsinessMap = new HashMap<>();

    static {
        tickToDrowsinessMap.put(Activity.PLAY, ticks -> ticks / 1000.0 * 1.5);

        workedTicksToDrowsinessMap.put(VillagerProfession.ARMORER, mcHours -> 5.0/6 * mcHours + 7.0/6);
        workedTicksToDrowsinessMap.put(VillagerProfession.BUTCHER, mcHours -> 5.0/4 * mcHours + 13.0/4);
        workedTicksToDrowsinessMap.put(VillagerProfession.CARTOGRAPHER, mcHours -> 1.0/2 * mcHours + 5.0/2);
        workedTicksToDrowsinessMap.put(VillagerProfession.CLERIC, mcHours -> 1.0/2 * mcHours + 5.0/2);
        workedTicksToDrowsinessMap.put(VillagerProfession.FARMER, mcHours -> 5.0/6 * mcHours + 13.0/6);
        workedTicksToDrowsinessMap.put(VillagerProfession.FISHERMAN, mcHours -> 5.0/6 * mcHours + 13.0/6);
        workedTicksToDrowsinessMap.put(VillagerProfession.FLETCHER, mcHours -> 1.0/3 * mcHours + 7.0/3);
        workedTicksToDrowsinessMap.put(VillagerProfession.LEATHERWORKER, mcHours -> 5.0/6 * mcHours + 13.0/6);
        workedTicksToDrowsinessMap.put(VillagerProfession.LIBRARIAN, mcHours -> 1.0/3 * mcHours + 7.0/3);
        workedTicksToDrowsinessMap.put(VillagerProfession.MASON, mcHours -> 5.0/6 * mcHours + 13.0/6);
        workedTicksToDrowsinessMap.put(VillagerProfession.NITWIT, mcHours -> 1.0/3 * mcHours + 7.0/3);
        workedTicksToDrowsinessMap.put(VillagerProfession.SHEPHERD, mcHours -> 5.0/6 * mcHours + 13.0/6);
        workedTicksToDrowsinessMap.put(VillagerProfession.TOOLSMITH, mcHours -> 5.0/6 * mcHours + 13.0/6);
        workedTicksToDrowsinessMap.put(VillagerProfession.WEAPONSMITH, mcHours -> 5.0/6 * mcHours + 13.0/6);
    }

    public static double ticksToMcHours(long ticks) {
       return ticks / 1000.0;
    }

    /**Returns how much drowsiness an adult villager loses after waking up**/
    public static double getAdultDrowsinessDecrease(double sleptMcHours) {
        return abs(3.0 / 176 * pow(sleptMcHours, 3) - 115.0/528 * pow(sleptMcHours, 2) - 115.0/264 * sleptMcHours);
    }

    /**Returns how much drowsiness a child villager loses after waking up**/
    public static double getChildDrowsinessDecrease(double sleptMcHours) {
        return abs(29.0 / 2100 * pow(sleptMcHours, 3) - 101.0/448 * pow(sleptMcHours, 2) - 451.0/22445 * sleptMcHours);
    }

    public static double getWorkDrowsinessIncrease(long ticksActive, VillagerProfession job) {
        double mcHours = ticksActive / 1000.0;
        return workedTicksToDrowsinessMap.computeIfAbsent(job, prof ->
                hours -> 5.0/6 * mcHours + 7.0/6).apply(mcHours);
    }

    public static SoundEvent getFallSound(LivingEntity entity, int heightIn) {
        return heightIn > 4 ? SoundEvents.ENTITY_GENERIC_BIG_FALL : SoundEvents.ENTITY_GENERIC_SMALL_FALL;
    }

    public static BlockPos getPositionUnderneath(LivingEntity entity) {
        return new BlockPos(entity.getPositionVec().x, entity.getBoundingBox().minY - 0.5000001D, entity.getPositionVec().z);
    }

}
