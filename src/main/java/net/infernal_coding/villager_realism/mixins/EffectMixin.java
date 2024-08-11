package net.infernal_coding.villager_realism.mixins;

import net.infernal_coding.villager_realism.util.MixinCalls;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.potion.Effect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Effect.class)
public class EffectMixin {

    @Unique
    Object object = this;

    @Inject(method = "performEffect", at = @At("HEAD"))
    public void performEffect(LivingEntity entity, int amplifier, CallbackInfo info) {
        Effect effect = (Effect) object;

        if (entity instanceof VillagerEntity) {
            MixinCalls.performEffect(entity, effect, amplifier);
        }
    }
}
