package net.infernal_coding.villager_realism.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Providers {
    public static class VillagerCapabilityProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundNBT> {
        @CapabilityInject(IVillagerCapability.class)
        public static Capability<IVillagerCapability> CAPABILITY = null;
        IVillagerCapability instance = new VillagerCapability();

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {

            return cap == CAPABILITY ? LazyOptional.of(() -> (T) this.instance) : LazyOptional.empty();
        }

        @Override
        public CompoundNBT serializeNBT() {
            return (CompoundNBT) CAPABILITY.getStorage().writeNBT(CAPABILITY, instance, null);
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            CAPABILITY.getStorage().readNBT(CAPABILITY, instance, null, nbt);
        }
    }
}
