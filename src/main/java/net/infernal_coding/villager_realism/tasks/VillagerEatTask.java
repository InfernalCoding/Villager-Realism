package net.infernal_coding.villager_realism.tasks;

import com.google.common.collect.ImmutableMap;
import net.infernal_coding.villager_realism.capability.IVillagerCapability;
import net.infernal_coding.villager_realism.capability.Providers;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;

public class VillagerEatTask extends Task<VillagerEntity> {
    public VillagerEatTask() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean shouldExecute(ServerWorld world, VillagerEntity villager) {
        IVillagerCapability capability = villager.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().get();
        return capability.getFoodLevel() < 12 && getFoodValueFromInventory(villager) != 0;
    }
    private int getFoodValueFromInventory(VillagerEntity villager) {
        Inventory inventory = villager.getVillagerInventory();
        ItemStack heldItem = villager.getHeldItemMainhand();
        int mainHandValue = VillagerEntity.FOOD_VALUES.getOrDefault(heldItem.getItem(), 0) * heldItem.getCount();

        return VillagerEntity.FOOD_VALUES.entrySet().stream().mapToInt((foodValueEntry) ->
                inventory.count(foodValueEntry.getKey()) * foodValueEntry.getValue()).sum() + mainHandValue;
    }

    @Override
    protected void updateTask(ServerWorld world, VillagerEntity villager, long gameTime) {
        ItemStack food = getNextFoodItem(villager);

        if (!ItemStack.areItemStacksEqual(food, villager.getActiveItemStack())) {
            villager.setHeldItem(Hand.MAIN_HAND, food);
            villager.setActiveHand(Hand.MAIN_HAND);
        }
    }

    static ItemStack getNextFoodItem(VillagerEntity villager) {
        Inventory inventory = villager.getVillagerInventory();
        ItemStack mainHand = villager.getHeldItem(Hand.MAIN_HAND);

        if (mainHand.isFood()) return mainHand;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i).isFood()) {
                ItemStack stack = inventory.getStackInSlot(i).copy();
                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected boolean shouldContinueExecuting(ServerWorld world, VillagerEntity villager, long gameTime) {
        IVillagerCapability capability = villager.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().get();
        return capability.getFoodLevel() < 12 && getFoodValueFromInventory(villager) > 0;
    }

    @Override
    protected void resetTask(ServerWorld world, VillagerEntity villager, long gameTime) {
        Inventory inventory = villager.getVillagerInventory();
        villager.resetActiveHand();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i).isEmpty()) {
                inventory.setInventorySlotContents(i, villager.getHeldItemMainhand());
                villager.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
            }
        }

        Optional<DamageSource> damageSource = villager.getBrain().getMemory(MemoryModuleType.HURT_BY);
        damageSource.ifPresent(source -> {
            if (source == DamageSource.STARVE) {
                villager.getBrain()
                        .switchTo(villager
                                .getBrain()
                                .getSchedule()
                                .getScheduledActivity((int) world.getDayTime()));
            }
        });
    }
}
