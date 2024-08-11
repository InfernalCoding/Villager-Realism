package net.infernal_coding.villager_realism.mixins;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.infernal_coding.villager_realism.Registry;
import net.infernal_coding.villager_realism.tasks.VillagerEatTask;
import net.infernal_coding.villager_realism.util.MixinCalls;
import net.infernal_coding.villager_realism.util.Util;
import net.infernal_coding.villager_realism.VillagerRealism;
import net.infernal_coding.villager_realism.capability.IVillagerCapability;
import net.infernal_coding.villager_realism.capability.Providers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.merchant.villager.*;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.item.*;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.Set;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends AbstractVillagerEntity {
    @Shadow private long lastRestock;
    @Shadow private int restocksToday;

    @Shadow public abstract Brain<VillagerEntity> getBrain();

    @Unique
    AbstractVillagerEntity entity = this;


    @Redirect(method = "func_230293_i_", at = @At(value = "INVOKE", target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z"))
    public boolean $makePickUpHoe(Set<Item> instance, Object o) {
        VillagerEntity villager = (VillagerEntity) entity;

        Item item = (Item) o;
        return instance.contains(item) || (item instanceof HoeItem && villager.getVillagerData().getProfession() == VillagerProfession.FARMER);
    }

    public VillagerEntityMixin(EntityType<? extends VillagerEntity> type, World worldIn, VillagerType villagerType) {
        super(type, worldIn);
        ((GroundPathNavigator)this.getNavigator()).setBreakDoors(true);
        this.getNavigator().setCanSwim(true);
        this.setCanPickUpLoot(true);
        this.setVillagerData(this.getVillagerData().withType(villagerType).withProfession(VillagerProfession.NONE));
    }

    @Inject(method = "initBrain", at = @At("HEAD"))
    private void initBrain(Brain<VillagerEntity> brain, CallbackInfo ci) {
        brain.registerActivity(Registry.STARVE.get(), ImmutableList.of(Pair.of(0, new VillagerEatTask())));
    }

    /**
     * @author Infernal_Coding
     * @reason Meant to modify the existing vanilla villager trades
     */
    @Overwrite
    protected void populateTradeData() {
        VillagerData villagerdata = this.getVillagerData();
        Int2ObjectMap<VillagerTrades.ITrade[]> int2objectmap = VillagerTrades.VILLAGER_DEFAULT_TRADES.get(villagerdata.getProfession());
        if (int2objectmap != null && !int2objectmap.isEmpty()) {
            VillagerTrades.ITrade[] avillagertrades$itrade = int2objectmap.get(villagerdata.getLevel());
            if (avillagertrades$itrade != null) {
                MerchantOffers merchantoffers = this.getOffers();
                this.addTrades(merchantoffers, avillagertrades$itrade, 2);
            }
        }

    }

    @Redirect(method = "getEntityInteractionResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/MerchantOffers;isEmpty()Z", ordinal = 1))

    public boolean canVillagerTrade(MerchantOffers offers) {
        return MixinCalls.canVillagerTrade(this, offers);
    }

    /**
     * @author Infernal_Coding
     * @reason Meant to prevent overly fatigued villagers from restocking for the day
     */
    @Overwrite
    private boolean canRestock() {
        IVillagerCapability capability = this.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().get();
        return this.restocksToday < 2 && this.world.getGameTime() > this.lastRestock + 2400L && capability.getDrowsiness() < 5;
    }

    /**
     * @author Infernal_Coding
     * @reason Meant to make villagers more tired upon restocking trades
     */
    @Overwrite
    public void restock() {
        IVillagerCapability capability = this.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().get();
        this.calculateDemandOfOffers();

        for(MerchantOffer merchantoffer : this.getOffers()) {
            if (capability.getDrowsiness() > 4.5) break;
            merchantoffer.resetUses();
            capability.increaseDrowsiness(new Random().nextGaussian());
        }

        this.lastRestock = this.world.getGameTime();
        this.restocksToday++;
    }

    @Inject(at = @At("TAIL"), method = "startSleeping")
    public void startSleeping(CallbackInfo info) {
        IVillagerCapability capability = this.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().get();
        capability.setSleepStartTick(world.getDayTime());
    }

    @Inject(at = @At("TAIL"), method = "wakeUp")
    public void wakeUp(CallbackInfo info) {
        VillagerEntity villager = (VillagerEntity) entity;
        IVillagerCapability capability = villager.getCapability(Providers.VillagerCapabilityProvider.CAPABILITY).resolve().get();
        long sleptTicks = capability.getSleepStartTick() - world.getDayTime();
        double sleptMcHours = Util.ticksToMcHours(sleptTicks);
        double drowsiness = villager.isChild() ? Util.getChildDrowsinessDecrease(sleptMcHours) :
                Util.getAdultDrowsinessDecrease(sleptMcHours);
        capability.decreaseDrowsiness(drowsiness);
    }

    @Inject(at = @At("TAIL"), method = "tick")
    public void tick(CallbackInfo info) {
        VillagerEntity villager = (VillagerEntity) entity;
        MixinCalls.tick(villager);
        MixinCalls.tickFood(villager);
    }
    @Shadow public abstract VillagerData getVillagerData();
    @Shadow public abstract void setVillagerData(VillagerData data);

    @Shadow protected abstract void calculateDemandOfOffers();
}
