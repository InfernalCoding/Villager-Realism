package net.infernal_coding.villager_realism.mixins;

import net.infernal_coding.villager_realism.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(AbstractArrowEntity.class)
public class AbstractArrowEntityMixin {

    @Unique
    private static final Random random = new Random();

    @Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
    protected void onEntityHit(EntityRayTraceResult result, CallbackInfo info) {
        Entity entity = result.getEntity();
        if (entity instanceof IronGolemEntity && Config.GOLEM_ARROWS.get()) {
            AbstractArrowEntity arrow = (AbstractArrowEntity) (Object) this;
            arrow.setMotion(arrow.getMotion().mul(-1.0, 1.0, -1.0));
            arrow.getEntityWorld().playSound(entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.MASTER, (float) (1.0 + random.nextFloat()), random.nextFloat() * .7F + .3F, false);
            info.cancel();
        }
    }
}
