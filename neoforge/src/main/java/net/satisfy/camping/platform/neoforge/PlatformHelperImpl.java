package net.satisfy.camping.platform.neoforge;

import net.satisfy.camping.core.config.CampingNeoForgeConfig;
import net.satisfy.camping.core.util.BackpackVariant;
import net.satisfy.camping.platform.services.IConfigHelper;

public class PlatformHelperImpl implements IConfigHelper {
    @Override
    public boolean isGrillingEnabled() {
        return CampingNeoForgeConfig.enableGrilling;
    }

    @Override
    public boolean isGrilledGlintEnabled() {
        return CampingNeoForgeConfig.enableGrilledGlint;
    }

    @Override
    public float getGrilledFoodMultiplier() {
        return (float) CampingNeoForgeConfig.grilledFoodMultiplier;
    }

    @Override
    public int getMarshmallowRoastDuration() {
        return CampingNeoForgeConfig.marshmallowRoastDuration;
    }

    @Override
    public boolean isSleepingBagRegenerationEnabled() {
        return CampingNeoForgeConfig.enableSleepingBagRegeneration;
    }

    @Override
    public boolean isSleepingBagSpawnEnabled() {
        return CampingNeoForgeConfig.enableSleepingBagSpawn;
    }

    @Override
    public boolean isTentSleepNearMonstersEnabled() {
        return CampingNeoForgeConfig.enableTentSleepNearMonsters;
    }

    @Override
    public int getTentSafeRange() {
        return CampingNeoForgeConfig.tentSafeRange;
    }

    @Override
    public boolean isMonsterTentGriefingEnabled() {
        return CampingNeoForgeConfig.enableMonsterTentGriefing;
    }

    @Override
    public boolean isMosquitoesEnabled() {
        return CampingNeoForgeConfig.enableMosquitoes;
    }

    @Override
    public double getMosquitoChance() {
        return CampingNeoForgeConfig.mosquitoChance / 100.0;
    }

    @Override
    public int getMosquitoMaxPerPlayer() {
        return CampingNeoForgeConfig.mosquitoMaxPerPlayer;
    }

    @Override
    public boolean isItchyEffectEnabled() {
        return CampingNeoForgeConfig.enableItchyEffect;
    }

    @Override
    public int getRepellentDuration() {
        return CampingNeoForgeConfig.repellentDuration;
    }

    @Override
    public boolean showTentInfo() {
        return CampingNeoForgeConfig.showTentInfo;
    }

    @Override
    public boolean showGrillInfo() {
        return CampingNeoForgeConfig.showGrillInfo;
    }

    @Override
    public int getBackpackRows(BackpackVariant variant) {
        return switch (variant) {
            case SMALL_BACKPACK -> CampingNeoForgeConfig.smallBackpackMaxRows;
            case WANDERER_BAG -> CampingNeoForgeConfig.wandererBagMaxRows;
            case SHEEPBAG -> CampingNeoForgeConfig.sheepbagMaxRows;
            case GOODYBAG -> CampingNeoForgeConfig.goodybagMaxRows;
            case WANDERER_BACKPACK -> CampingNeoForgeConfig.wandererBackpackMaxRows;
            case LARGE_BACKPACK -> CampingNeoForgeConfig.largeBackpackMaxRows;
        };
    }

    @Override
    public int getNutrition(String itemName) {
        try {
            return switch (itemName) {
                case "marshmallow" -> CampingNeoForgeConfig.MARSHMALLOW_NUTRITION.get();
                case "roasted_marshmallow" -> CampingNeoForgeConfig.ROASTED_MARSHMALLOW_NUTRITION.get();
                default -> 0;
            };
        } catch (IllegalStateException notLoadedYet) {
            return switch (itemName) {
                case "marshmallow" -> CampingNeoForgeConfig.marshmallowNutrition;
                case "roasted_marshmallow" -> CampingNeoForgeConfig.roastedMarshmallowNutrition;
                default -> 0;
            };
        }
    }

    @Override
    public float getSaturation(String itemName) {
        try {
            return switch (itemName) {
                case "marshmallow" -> CampingNeoForgeConfig.MARSHMALLOW_SATURATION.get().floatValue();
                case "roasted_marshmallow" -> CampingNeoForgeConfig.ROASTED_MARSHMALLOW_SATURATION.get().floatValue();
                default -> 0.0f;
            };
        } catch (IllegalStateException notLoadedYet) {
            return switch (itemName) {
                case "marshmallow" -> (float) CampingNeoForgeConfig.marshmallowSaturation;
                case "roasted_marshmallow" -> (float) CampingNeoForgeConfig.roastedMarshmallowSaturation;
                default -> 0.0f;
            };
        }
    }

    @Override
    public int getEffectDuration(String itemName) {
        try {
            return switch (itemName) {
                case "marshmallow" -> CampingNeoForgeConfig.MARSHMALLOW_EFFECT_DURATION.get();
                case "roasted_marshmallow" -> CampingNeoForgeConfig.ROASTED_MARSHMALLOW_EFFECT_DURATION.get();
                case "melted_marshmallow" -> CampingNeoForgeConfig.MELTED_MARSHMALLOW_EFFECT_DURATION.get();
                case "charred_marshmallow" -> CampingNeoForgeConfig.CHARRED_MARSHMALLOW_EFFECT_DURATION.get();
                case "totally_burnt_marshmallow" -> CampingNeoForgeConfig.TOTALLY_BURNT_MARSHMALLOW_EFFECT_DURATION.get();
                default -> 0;
            };
        } catch (IllegalStateException notLoadedYet) {
            return switch (itemName) {
                case "marshmallow" -> CampingNeoForgeConfig.marshmallowEffectDuration;
                case "roasted_marshmallow" -> CampingNeoForgeConfig.roastedMarshmallowEffectDuration;
                case "melted_marshmallow" -> CampingNeoForgeConfig.meltedMarshmallowEffectDuration;
                case "charred_marshmallow" -> CampingNeoForgeConfig.charredMarshmallowEffectDuration;
                case "totally_burnt_marshmallow" -> CampingNeoForgeConfig.totallyBurntMarshmallowEffectDuration;
                default -> 0;
            };
        }
    }
}
