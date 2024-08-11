package net.infernal_coding.villager_realism.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.infernal_coding.villager_realism.VillagerRealism;
import net.infernal_coding.villager_realism.capability.IVillagerCapability;
import net.infernal_coding.villager_realism.capability.Providers;
import net.infernal_coding.villager_realism.tasks.VillagerEatTask;
import net.infernal_coding.villager_realism.tasks.VillagerWakeTask;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffers;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.Difficulty;

import java.util.Optional;

import static net.infernal_coding.villager_realism.util.Util.*;
import static net.minecraft.entity.Entity.horizontalMag;

public class MixinCalls {

    public static void jump(VillagerEntity villager) {
        IVillagerCapability capability = villager.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().get();
        if (villager.getAIMoveSpeed() > .6) {
            capability.addExhaustion(.2F);
        } else capability.addExhaustion(.05F);
    }
    public static void performEffect(LivingEntity entity, Effect effect, int amplifier) {
        IVillagerCapability capability = entity.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().orElse(null);
        if (capability == null) return;

        if (effect == Effects.SATURATION) {
            if (!entity.world.isRemote) {
                capability.addFood(amplifier + 1);
                capability.addSaturation(1.0F);
            }
        } else if (effect == Effects.HUNGER) {
            capability.addExhaustion(0.005F * (float)(amplifier + 1));
        }
    }
    public static void onFoodEaten(VillagerEntity villager, ItemStack itemStack) {
        IVillagerCapability capability = villager.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().orElse(null);
        if (capability == null) return;
        Food food = itemStack.getItem().getFood();
        capability.addFood(food.getHealing());
        capability.addSaturation(food.getSaturation());
    }
    public static void tickFood(VillagerEntity villager) {
        IVillagerCapability capability = villager.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().orElse(null);
        if (capability == null) return;

        Difficulty difficulty = villager.world.getDifficulty();
        float foodExhaustionLevel = capability.getFoodExhaustionLevel();
        float foodSaturationLevel = capability.getFoodSaturationLevel();
        int foodLevel = capability.getFoodLevel();
        int foodTimer = capability.getFoodTimer();

        if (foodExhaustionLevel > 4.0F) {
            capability.setFoodExhaustionLevel(foodExhaustionLevel - 4);
            if (foodSaturationLevel > 0.0F) {
               capability.setFoodSaturationLevel(Math.max(foodSaturationLevel - 1.0F, 0.0F));
            } else if (difficulty != Difficulty.PEACEFUL) {
                capability.setFoodLevel(Math.max(foodLevel - 1, 0));
            }
        }

        if (foodLevel <= 0) {

            capability.setFoodTimer(foodTimer + 1);
            if (foodTimer >= 80) {
                if (villager.getHealth() > 10.0F || difficulty == Difficulty.HARD || villager.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    villager.attackEntityFrom(DamageSource.STARVE, 1.0F);
                }
                capability.setFoodTimer(0);
            }
        } else {
            capability.setFoodTimer(0);
        }

        if (capability.getFoodLevel() <= 6) {
            villager.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.5);
        } else if (villager.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() == .3) {
            villager.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(.5);
        }
    }
    
    public static void tick(VillagerEntity villager) {
        IVillagerCapability capability = villager.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().orElse(null);
        if (capability == null) return;
        long gameTime = villager.world.getGameTime();
        int dayTime = (int) villager.world.getDayTime();

        if (capability.getCurrentActivity() == null) {
            capability.setActivityStartTick(gameTime);
            capability.setCurrentActivity(villager.getBrain().getSchedule().getScheduledActivity(dayTime));
        }

        Activity previousActivity = capability.getCurrentActivity();
        Activity currentActivity = villager.getBrain().getSchedule().getScheduledActivity(dayTime);

        if (previousActivity != currentActivity) {
            long ticksActive = gameTime - capability.getActivityStartTick();

            if (Util.tickToDrowsinessMap.containsKey(previousActivity)) {
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
                    capability.setActivityStartTick(gameTime);

                   // StringTextComponent display = new StringTextComponent("Drowsiness: " + capability.getDrowsiness() + " Food:" + capability.getFoodLevel() + " Satur" + capability.getFoodSaturationLevel() + " Exh " + capability.getFoodExhaustionLevel());
                   // if (!display.equals(villager.getCustomName())) villager.setCustomName(display);
                    return;
                } catch (Exception e) {
                    VillagerRealism.LOGGER.error(e.getMessage());
                }
            }
            capability.setCurrentActivity(currentActivity);
            capability.setActivityStartTick(gameTime);
        }

        if (capability.getCurrentActivity() == Activity.REST && villager.getPose() != Pose.SLEEPING) {
            double mcTick = 1 / 5000.0;
            double drowsinessAdd = villager.isChild() ? mcTick/2 : mcTick;
            capability.increaseDrowsiness(drowsinessAdd);
        }

        //StringTextComponent display = new StringTextComponent("Drowsiness: " + capability.getDrowsiness() + " Food:" + capability.getFoodLevel() + " Satur" + capability.getFoodSaturationLevel() + " Exh " + capability.getFoodExhaustionLevel());

        //if (!display.equals(villager.getCustomName())) villager.setCustomName(display);
    }

    public static void travel(LivingEntity entity, Vector3d travelVector) {
        double x = entity.getPosX(), y = entity.getPosY(), z = entity.getPosZ();

        if (entity.isServerWorld() || entity.canPassengerSteer()) {
            double d0 = 0.08D;
            ModifiableAttributeInstance gravity = entity.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
            boolean flag = entity.getMotion().y <= 0.0D;
            if (flag && entity.isPotionActive(Effects.SLOW_FALLING)) {
                if (!gravity.hasModifier(SLOW_FALLING)) gravity.applyNonPersistentModifier(SLOW_FALLING);
                entity.fallDistance = 0.0F;
            } else if (gravity.hasModifier(SLOW_FALLING)) {
                gravity.removeModifier(SLOW_FALLING);
            }
            d0 = gravity.getValue();

            FluidState fluidstate = entity.world.getFluidState(entity.getPosition());
            if (entity.isInWater() && entity.func_241208_cS_() && !entity.func_230285_a_(fluidstate.getFluid())) {
                double d8 = entity.getPosY();
                float waterSlowDown = 0.0f;
                try {
                    waterSlowDown = (float) getWaterSlowDown.invoke(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                float f5 = entity.isSprinting() ? 0.9F : waterSlowDown;
                float f6 = 0.02F;
                float f7 = (float) EnchantmentHelper.getDepthStriderModifier(entity);
                if (f7 > 3.0F) {
                    f7 = 3.0F;
                }

                if (!entity.isOnGround()) {
                    f7 *= 0.5F;
                }

                if (f7 > 0.0F) {
                    f5 += (0.54600006F - f5) * f7 / 3.0F;
                    f6 += (entity.getAIMoveSpeed() - f6) * f7 / 3.0F;
                }

                if (entity.isPotionActive(Effects.DOLPHINS_GRACE)) {
                    f5 = 0.96F;
                }

                f6 *= (float)entity.getAttribute(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get()).getValue();
                entity.moveRelative(f6, travelVector);
                entity.move(MoverType.SELF, entity.getMotion());
                Vector3d vector3d6 = entity.getMotion();
                if (entity.collidedHorizontally && entity.isOnLadder()) {
                    vector3d6 = new Vector3d(vector3d6.x, 0.2D, vector3d6.z);
                }

                entity.setMotion(vector3d6.mul((double)f5, (double)0.8F, (double)f5));
                Vector3d vector3d2 = entity.func_233626_a_(d0, flag, entity.getMotion());
                entity.setMotion(vector3d2);
                if (entity.collidedHorizontally && entity.isOffsetPositionInLiquid(vector3d2.x, vector3d2.y + (double)0.6F - entity.getPosY() + d8, vector3d2.z)) {
                    entity.setMotion(vector3d2.x, (double)0.3F, vector3d2.z);
                }
            } else if (entity.isInLava() && entity.func_241208_cS_() && !entity.func_230285_a_(fluidstate.getFluid())) {
                double d7 = entity.getPosY();
                entity.moveRelative(0.02F, travelVector);
                entity.move(MoverType.SELF, entity.getMotion());
                if (entity.func_233571_b_(FluidTags.LAVA) <= entity.getFluidJumpHeight()) {
                    entity.setMotion(entity.getMotion().mul(0.5D, (double)0.8F, 0.5D));
                    Vector3d vector3d3 = entity.func_233626_a_(d0, flag, entity.getMotion());
                    entity.setMotion(vector3d3);
                } else {
                    entity.setMotion(entity.getMotion().scale(0.5D));
                }

                if (!entity.hasNoGravity()) {
                    entity.setMotion(entity.getMotion().add(0.0D, -d0 / 4.0D, 0.0D));
                }

                Vector3d vector3d4 = entity.getMotion();
                if (entity.collidedHorizontally && entity.isOffsetPositionInLiquid(vector3d4.x, vector3d4.y + (double)0.6F - entity.getPosY() + d7, vector3d4.z)) {
                    entity.setMotion(vector3d4.x, (double)0.3F, vector3d4.z);
                }
            } else if (entity.isElytraFlying()) {
                Vector3d vector3d = entity.getMotion();
                if (vector3d.y > -0.5D) {
                    entity.fallDistance = 1.0F;
                }

                Vector3d vector3d1 = entity.getLookVec();
                float f = entity.rotationPitch * ((float)Math.PI / 180F);
                double d1 = Math.sqrt(vector3d1.x * vector3d1.x + vector3d1.z * vector3d1.z);
                double d3 = Math.sqrt(horizontalMag(vector3d));
                double d4 = vector3d1.length();
                float f1 = MathHelper.cos(f);
                f1 = (float)((double)f1 * (double)f1 * Math.min(1.0D, d4 / 0.4D));
                vector3d = entity.getMotion().add(0.0D, d0 * (-1.0D + (double)f1 * 0.75D), 0.0D);
                if (vector3d.y < 0.0D && d1 > 0.0D) {
                    double d5 = vector3d.y * -0.1D * (double)f1;
                    vector3d = vector3d.add(vector3d1.x * d5 / d1, d5, vector3d1.z * d5 / d1);
                }

                if (f < 0.0F && d1 > 0.0D) {
                    double d9 = d3 * (double)(-MathHelper.sin(f)) * 0.04D;
                    vector3d = vector3d.add(-vector3d1.x * d9 / d1, d9 * 3.2D, -vector3d1.z * d9 / d1);
                }

                if (d1 > 0.0D) {
                    vector3d = vector3d.add((vector3d1.x / d1 * d3 - vector3d.x) * 0.1D, 0.0D, (vector3d1.z / d1 * d3 - vector3d.z) * 0.1D);
                }

                entity.setMotion(vector3d.mul((double)0.99F, (double)0.98F, (double)0.99F));
                entity.move(MoverType.SELF, entity.getMotion());
                if (entity.collidedHorizontally && !entity.world.isRemote) {
                    double d10 = Math.sqrt(horizontalMag(entity.getMotion()));
                    double d6 = d3 - d10;
                    float f2 = (float)(d6 * 10.0D - 3.0D);
                    if (f2 > 0.0F) {
                        entity.playSound(getFallSound(entity, (int)f2), 1.0F, 1.0F);
                        entity.attackEntityFrom(DamageSource.FLY_INTO_WALL, f2);
                    }
                }

                if (entity.isOnGround() && !entity.world.isRemote) {
                    entity.setFlag(7, false);
                }
            } else {
                BlockPos blockpos = getPositionUnderneath(entity);
                float f3 = entity.world.getBlockState(getPositionUnderneath(entity)).getSlipperiness(entity.world, getPositionUnderneath(entity), entity);
                float f4 = entity.isOnGround() ? f3 * 0.91F : 0.91F;
                Vector3d vector3d5 = entity.func_233633_a_(travelVector, f3);
                double d2 = vector3d5.y;
                if (entity.isPotionActive(Effects.LEVITATION)) {
                    d2 += (0.05D * (double)(entity.getActivePotionEffect(Effects.LEVITATION).getAmplifier() + 1) - vector3d5.y) * 0.2D;
                    entity.fallDistance = 0.0F;
                } else if (entity.world.isRemote && !entity.world.isBlockLoaded(blockpos)) {
                    if (entity.getPosY() > 0.0D) {
                        d2 = -0.1D;
                    } else {
                        d2 = 0.0D;
                    }
                } else if (!entity.hasNoGravity()) {
                    d2 -= d0;
                }

                entity.setMotion(vector3d5.x * (double)f4, d2 * (double)0.98F, vector3d5.z * (double)f4);
            }
        }

        entity.func_233629_a_(entity, entity instanceof IFlyingAnimal);

        if (entity instanceof VillagerEntity) {
            addMovementStat(entity,entity.getPosX() - x,
                    entity.getPosY() - y, entity.getPosZ() - z);
        }
    }

    public static void addMovementStat(LivingEntity entity, double dx, double dy, double dz) {
        IVillagerCapability capability = entity.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().orElse(null);

        if (capability == null) return;

        if (!entity.isPassenger()) {
            int l;
            double d = dx * dx + dy * dy + dz * dz;
            if (entity.isSwimming()) {
                l = Math.round(MathHelper.sqrt(d) * 100.0F);
                if (l > 0) {
                    capability.addExhaustion(0.01F * (float)l * 0.01F);
                }
            } else if (entity.areEyesInFluid(FluidTags.WATER)) {
                l = Math.round(MathHelper.sqrt(d) * 100.0F);
                if (l > 0) {
                    capability.addExhaustion(0.01F * (float)l * 0.01F);
                }
            } else if (entity.isInWater()) {
                l = Math.round(MathHelper.sqrt(dx * dx + dz * dz) * 100.0F);
                if (l > 0) {
                    capability.addExhaustion(0.01F * (float)l * 0.01F);
                }
            } else if (entity.isOnGround()) {
                l = Math.round(MathHelper.sqrt(dx * dx + dz * dz) * 100.0F);
                if (l > 0) {
                    if (entity.getAIMoveSpeed() > .6) {
                        capability.addExhaustion(0.1F * (float)l * 0.01F);
                    } else if (entity.isCrouching()) {
                        capability.addExhaustion(0.0F * (float)l * 0.01F);
                    } else {
                        capability.addExhaustion(0.05F * (float)l * 0.01F);
                    }
                }
            }
        }

    }

    public static boolean canVillagerTrade(AbstractVillagerEntity villager, MerchantOffers offers) {
        IVillagerCapability capability = villager.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().get();
        Activity activity = capability.getCurrentActivity();
        double drowsiness = capability.getDrowsiness();
        boolean isValidActivity = activity == Activity.WORK || activity == Activity.IDLE || activity == Activity.CELEBRATE || activity == Activity.MEET;
        return offers.isEmpty() || drowsiness >= 5 || !isValidActivity;
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> core(VillagerProfession profession, float speed) {
        return ImmutableList.of(
                Pair.of(0, new SwimTask(0.8F)), 
                Pair.of(0, new InteractWithDoorTask()), 
                Pair.of(0, new LookTask(45, 90)), 
                Pair.of(0, new PanicTask()), 
                Pair.of(0, new VillagerWakeTask()),
                Pair.of(0, new HideFromRaidOnBellRingTask()),
                Pair.of(0, new BeginRaidTask()), 
                Pair.of(0, new ExpirePOITask(profession.getPointOfInterest(), MemoryModuleType.JOB_SITE)), 
                Pair.of(0, new ExpirePOITask(profession.getPointOfInterest(), MemoryModuleType.POTENTIAL_JOB_SITE)),
                Pair.of(0, new VillagerEatTask()),
                Pair.of(1, new WalkToTargetTask()), 
                Pair.of(2, new SwitchVillagerJobTask(profession)), 
                Pair.of(3, new TradeTask(speed)), 
                Pair.of(5, new PickupWantedItemTask(speed, false, 4)), 
                Pair.of(6, new GatherPOITask(profession.getPointOfInterest(), MemoryModuleType.JOB_SITE, MemoryModuleType.POTENTIAL_JOB_SITE, true, Optional.empty())), 
                Pair.of(7, new FindPotentialJobTask(speed)), 
                Pair.of(8, new FindJobTask(speed)), 
                Pair.of(10, new GatherPOITask(PointOfInterestType.HOME, MemoryModuleType.HOME, false, Optional.of((byte)14))), 
                Pair.of(10, new GatherPOITask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT, true, Optional.of((byte)14))), 
                Pair.of(10, new AssignProfessionTask()), 
                Pair.of(10, new ChangeJobTask()));
    }
    private static Pair<Integer, Task<LivingEntity>> lookAtMany() {
        return Pair.of(5, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.CAT, 8.0F), 8), Pair.of(new LookAtEntityTask(EntityType.VILLAGER, 8.0F), 2), Pair.of(new LookAtEntityTask(EntityType.PLAYER, 8.0F), 2), Pair.of(new LookAtEntityTask(EntityClassification.CREATURE, 8.0F), 1), Pair.of(new LookAtEntityTask(EntityClassification.WATER_CREATURE, 8.0F), 1), Pair.of(new LookAtEntityTask(EntityClassification.WATER_AMBIENT, 8.0F), 1), Pair.of(new LookAtEntityTask(EntityClassification.MONSTER, 8.0F), 1), Pair.of(new DummyTask(30, 60), 2))));
    }
}
