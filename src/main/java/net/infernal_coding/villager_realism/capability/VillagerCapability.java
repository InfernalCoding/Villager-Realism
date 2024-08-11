package net.infernal_coding.villager_realism.capability;

import net.minecraft.entity.ai.brain.schedule.Activity;


public class VillagerCapability implements IVillagerCapability {

    int foodLevel = 20;
    float foodSaturationLevel = 5;
    float foodExhaustionLevel;
    int foodTimer;
    double drowsiness = 2.5;
    long sleepStartTick = -1;

    long activityStartTick = -1;

    Activity activity;

    @Override
    public void setFoodTimer(int amount) {
        foodTimer = amount;
    }

    @Override
    public int getFoodTimer() {
        return foodTimer;
    }

    @Override
    public void addFood(int level) {
        foodLevel = Math.min(foodLevel + level, 20);
    }
    @Override
    public void setFoodLevel(int level) {
        foodLevel = level;
    }

    @Override
    public int getFoodLevel() {
        return foodLevel;
    }

    @Override
    public void addSaturation(float amount) {
        foodSaturationLevel = Math.min(this.foodSaturationLevel + foodLevel * amount * 2.0F, this.foodLevel);
    }
    @Override
    public void setFoodSaturationLevel(float amount) {
        foodSaturationLevel = amount;
    }

    @Override
    public float getFoodSaturationLevel() {
        return foodSaturationLevel;
    }

    @Override
    public void addExhaustion(float exhaustion) {
        this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + exhaustion, 40.0F);
    }

    @Override
    public void setFoodExhaustionLevel(float amount) {
        foodExhaustionLevel = amount;
    }

    @Override
    public float getFoodExhaustionLevel() {
        return foodExhaustionLevel;
    }

    @Override
    public void setActivityStartTick(long tick) {
        this.activityStartTick = tick;
    }

    @Override
    public void setCurrentActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    public long getActivityStartTick() {
        return activityStartTick;
    }

    @Override
    public Activity getCurrentActivity() {
        return activity;
    }

    @Override
    public void increaseDrowsiness(double amount) {
        if (drowsiness + amount <= 10) {
            drowsiness += amount;
        } else drowsiness = 10;
    }

    @Override
    public void decreaseDrowsiness(double amount) {
        if (drowsiness - amount >= .5) {
            drowsiness -= amount;
        } else drowsiness = .5;
    }

    @Override
    public void setSleepStartTick(long tick) {
        this.sleepStartTick = tick;
    }

    @Override
    public long getSleepStartTick() {
        return sleepStartTick;
    }

    @Override
    public void setDrowsiness(double amount) {
        this.drowsiness = amount;
    }

    @Override
    public double getDrowsiness() {
        return drowsiness;
    }



}
