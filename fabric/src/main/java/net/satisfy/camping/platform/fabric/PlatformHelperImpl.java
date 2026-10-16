package net.satisfy.camping.platform.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import net.satisfy.camping.core.config.CampingFabricConfig;
import net.satisfy.camping.core.util.BackpackVariant;
import net.satisfy.camping.platform.services.IConfigHelper;

public class PlatformHelperImpl implements IConfigHelper {
    private static CampingFabricConfig config() {
        return AutoConfig.getConfigHolder(CampingFabricConfig.class).getConfig();
    }

    @Override
    public boolean isGrillingEnabled() {
        return config().grilling.enableGrilling;
    }

    @Override
    public boolean isGrilledGlintEnabled() {
        return config().grilling.enableGrilledGlint;
    }

    @Override
    public float getGrilledFoodMultiplier() {
        return config().grilling.grilledFoodMultiplier;
    }

    @Override
    public int getMarshmallowRoastDuration() {
        return config().grilling.marshmallowRoastDuration;
    }

    @Override
    public boolean isSleepingBagRegenerationEnabled() {
        return config().sleeping.enableSleepingBagRegeneration;
    }

    @Override
    public boolean isSleepingBagSpawnEnabled() {
        return config().sleeping.enableSleepingBagSpawn;
    }

    @Override
    public boolean isTentSleepNearMonstersEnabled() {
        return config().sleeping.enableTentSleepNearMonsters;
    }

    @Override
    public int getTentSafeRange() {
        return config().sleeping.tentSafeRange;
    }

    @Override
    public boolean isMonsterTentGriefingEnabled() {
        return config().sleeping.enableMonsterTentGriefing;
    }

    @Override
    public boolean isMosquitoesEnabled() {
        return config().mosquitoes.enableMosquitoes;
    }

    @Override
    public double getMosquitoChance() {
        return config().mosquitoes.mosquitoChance / 100.0;
    }

    @Override
    public int getMosquitoMaxPerPlayer() {
        return config().mosquitoes.mosquitoMaxPerPlayer;
    }

    @Override
    public boolean isItchyEffectEnabled() {
        return config().mosquitoes.enableItchyEffect;
    }

    @Override
    public int getRepellentDuration() {
        return config().mosquitoes.repellentDuration;
    }

    @Override
    public boolean showTentInfo() {
        return config().infoTooltips.showTentInfo;
    }

    @Override
    public boolean showGrillInfo() {
        return config().infoTooltips.showGrillInfo;
    }

    @Override
    public int getBackpackRows(BackpackVariant variant) {
        CampingFabricConfig.BackpacksSettings backpacks = config().backpacks;
        return switch (variant) {
            case SMALL_BACKPACK -> backpacks.smallBackpackMaxRows;
            case WANDERER_BAG -> backpacks.wandererBagMaxRows;
            case SHEEPBAG -> backpacks.sheepbagMaxRows;
            case GOODYBAG -> backpacks.goodybagMaxRows;
            case WANDERER_BACKPACK -> backpacks.wandererBackpackMaxRows;
            case LARGE_BACKPACK -> backpacks.largeBackpackMaxRows;
        };
    }

    @Override
    public int getNutrition(String itemName) {
        CampingFabricConfig.FoodSettings food = config().food;
        return switch (itemName) {
            case "marshmallow" -> food.marshmallowNutrition;
            case "roasted_marshmallow" -> food.roastedMarshmallowNutrition;
            default -> 0;
        };
    }

    @Override
    public float getSaturation(String itemName) {
        CampingFabricConfig.FoodSettings food = config().food;
        return switch (itemName) {
            case "marshmallow" -> food.marshmallowSaturation;
            case "roasted_marshmallow" -> food.roastedMarshmallowSaturation;
            default -> 0.0f;
        };
    }

    @Override
    public int getEffectDuration(String itemName) {
        CampingFabricConfig.FoodSettings food = config().food;
        return switch (itemName) {
            case "marshmallow" -> food.marshmallowEffectDuration;
            case "roasted_marshmallow" -> food.roastedMarshmallowEffectDuration;
            case "melted_marshmallow" -> food.meltedMarshmallowEffectDuration;
            case "charred_marshmallow" -> food.charredMarshmallowEffectDuration;
            case "totally_burnt_marshmallow" -> food.totallyBurntMarshmallowEffectDuration;
            default -> 0;
        };
    }
}
