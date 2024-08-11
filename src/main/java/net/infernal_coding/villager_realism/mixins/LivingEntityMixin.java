package net.infernal_coding.villager_realism.mixins;

import net.infernal_coding.villager_realism.capability.IVillagerCapability;
import net.infernal_coding.villager_realism.capability.Providers;
import net.infernal_coding.villager_realism.tasks.VillagerEatTask;
import net.infernal_coding.villager_realism.util.MixinCalls;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    Entity entity = this;
    public LivingEntityMixin(EntityType<?> p_i48580_1_, World p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    @Inject(method = "onFoodEaten", at = @At("HEAD"))
    public void onFoodEaten(World world, ItemStack itemStack, CallbackInfoReturnable<ItemStack> info) {
        if (entity instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) entity;
            MixinCalls.onFoodEaten(villager, itemStack);
        }
    }

    @Inject(method = "jump", at = @At("HEAD"))
    public void jump(CallbackInfo info) {
        if (entity instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) entity;
            MixinCalls.jump(villager);
        }
    }


    /**
     * @author Infernal_Coding
     * @reason To ensure that villagers lose hunger when traveling
     */
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travel(Vector3d travelVector, CallbackInfo ci) {
        ci.cancel();
        MixinCalls.travel((LivingEntity) entity, travelVector);
    }

}
