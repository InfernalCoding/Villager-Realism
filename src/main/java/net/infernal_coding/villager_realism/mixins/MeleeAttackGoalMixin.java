package net.infernal_coding.villager_realism.mixins;

import net.infernal_coding.villager_realism.Config;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MeleeAttackGoal.class)
public class MeleeAttackGoalMixin {

    @Shadow @Final protected CreatureEntity attacker;

    @Inject(method = "getAttackReachSqr", at = @At("HEAD"), cancellable = true)

    protected void getAttackReachSqr(LivingEntity target, CallbackInfoReturnable<Double> cir) {
        if (attacker instanceof IronGolemEntity && Config.INCREASE_GOLEM_RANGE.get()) {
            cir.cancel();
            cir.setReturnValue(25.0);
        }
    }
}
