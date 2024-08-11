package net.infernal_coding.villager_realism.mixins.tasks;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.infernal_coding.villager_realism.util.MixinCalls;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerTasks;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(VillagerTasks.class)
public class VillagerTasksMixin {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> core(VillagerProfession profession, float p_220641_1_) {
        return MixinCalls.core(profession, p_220641_1_);
    }

}
