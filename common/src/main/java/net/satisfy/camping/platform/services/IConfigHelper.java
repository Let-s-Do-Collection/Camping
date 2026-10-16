package net.satisfy.camping.platform.services;

import net.satisfy.camping.core.util.BackpackVariant;

public interface IConfigHelper {
    boolean isGrillingEnabled();

    boolean isGrilledGlintEnabled();

    float getGrilledFoodMultiplier();

    int getMarshmallowRoastDuration();

    boolean isSleepingBagRegenerationEnabled();

    boolean isSleepingBagSpawnEnabled();

    boolean isTentSleepNearMonstersEnabled();

    int getTentSafeRange();

    boolean isMonsterTentGriefingEnabled();

    boolean isMosquitoesEnabled();

    double getMosquitoChance();

    int getMosquitoMaxPerPlayer();

    boolean isItchyEffectEnabled();

    int getRepellentDuration();

    boolean showTentInfo();

    boolean showGrillInfo();

    int getBackpackRows(BackpackVariant variant);

    int getNutrition(String itemName);

    float getSaturation(String itemName);

    int getEffectDuration(String itemName);
}
