package net.infernal_coding.villager_realism.capability;

import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface IVillagerCapability {


    void setFoodTimer(int amount);
    int getFoodTimer();

    void addFood(int amount);
    void setFoodLevel(int level);
    int getFoodLevel();

    void addSaturation(float amount);
    void setFoodSaturationLevel(float amount);
    float getFoodSaturationLevel();

    void addExhaustion(float exhaustion);
    void setFoodExhaustionLevel(float amount);
    float getFoodExhaustionLevel();

    void setActivityStartTick(long tick);

    long getActivityStartTick();

    void setCurrentActivity(Activity activity);

    Activity getCurrentActivity();
    void increaseDrowsiness(double amount);
    void decreaseDrowsiness(double amount);
    void setDrowsiness(double amount);

    double getDrowsiness();

    void setSleepStartTick(long tick);

    long getSleepStartTick();

}
