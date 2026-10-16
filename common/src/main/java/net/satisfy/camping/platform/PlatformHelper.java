package net.satisfy.camping.platform;

import net.satisfy.camping.core.util.BackpackVariant;

public abstract class PlatformHelper {
    public static boolean isGrillingEnabled() {
        return Services.CONFIG.isGrillingEnabled();
    }

    public static boolean isGrilledGlintEnabled() {
        return Services.CONFIG.isGrilledGlintEnabled();
    }

    public static float getGrilledFoodMultiplier() {
        return Services.CONFIG.getGrilledFoodMultiplier();
    }

    public static int getMarshmallowRoastDuration() {
        return Services.CONFIG.getMarshmallowRoastDuration();
    }

    public static boolean isSleepingBagRegenerationEnabled() {
        return Services.CONFIG.isSleepingBagRegenerationEnabled();
    }

    public static boolean isSleepingBagSpawnEnabled() {
        return Services.CONFIG.isSleepingBagSpawnEnabled();
    }

    public static boolean isTentSleepNearMonstersEnabled() {
        return Services.CONFIG.isTentSleepNearMonstersEnabled();
    }

    public static int getTentSafeRange() {
        return Services.CONFIG.getTentSafeRange();
    }

    public static boolean isMonsterTentGriefingEnabled() {
        return Services.CONFIG.isMonsterTentGriefingEnabled();
    }

    public static boolean isMosquitoesEnabled() {
        return Services.CONFIG.isMosquitoesEnabled();
    }

    public static double getMosquitoChance() {
        return Services.CONFIG.getMosquitoChance();
    }

    public static int getMosquitoMaxPerPlayer() {
        return Services.CONFIG.getMosquitoMaxPerPlayer();
    }

    public static boolean isItchyEffectEnabled() {
        return Services.CONFIG.isItchyEffectEnabled();
    }

    public static int getRepellentDuration() {
        return Services.CONFIG.getRepellentDuration();
    }

    public static boolean showTentInfo() {
        return Services.CONFIG.showTentInfo();
    }

    public static boolean showGrillInfo() {
        return Services.CONFIG.showGrillInfo();
    }

    public static int getBackpackRows(BackpackVariant variant) {
        return Services.CONFIG.getBackpackRows(variant);
    }

    public static int getNutrition(String itemName) {
        return Services.CONFIG.getNutrition(itemName);
    }

    public static float getSaturation(String itemName) {
        return Services.CONFIG.getSaturation(itemName);
    }

    public static int getEffectDuration(String itemName) {
        return Services.CONFIG.getEffectDuration(itemName);
    }
}
