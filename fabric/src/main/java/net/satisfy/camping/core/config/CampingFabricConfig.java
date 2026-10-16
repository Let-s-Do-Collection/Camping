package net.satisfy.camping.core.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "camping")
@Config.Gui.Background("camping:textures/block/grill_bottom.png")
public class CampingFabricConfig implements ConfigData {
    @ConfigEntry.Gui.CollapsibleObject
    public GrillingSettings grilling = new GrillingSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public SleepingSettings sleeping = new SleepingSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public BackpacksSettings backpacks = new BackpacksSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public MosquitoesSettings mosquitoes = new MosquitoesSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public InfoTooltipsSettings infoTooltips = new InfoTooltipsSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public FoodSettings food = new FoodSettings();

    public static class GrillingSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableGrilling = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableGrilledGlint = true;

        @ConfigEntry.Gui.Tooltip
        public float grilledFoodMultiplier = 1.25f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 10, max = 600)
        public int marshmallowRoastDuration = 40;
    }

    public static class SleepingSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableSleepingBagRegeneration = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableSleepingBagSpawn = false;

        @ConfigEntry.Gui.Tooltip
        public boolean enableTentSleepNearMonsters = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 64)
        public int tentSafeRange = 16;

        @ConfigEntry.Gui.Tooltip
        public boolean enableMonsterTentGriefing = false;
    }

    public static class BackpacksSettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 6)
        public int smallBackpackMaxRows = 3;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 6)
        public int wandererBagMaxRows = 3;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 6)
        public int sheepbagMaxRows = 4;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 6)
        public int goodybagMaxRows = 4;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 6)
        public int wandererBackpackMaxRows = 5;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 6)
        public int largeBackpackMaxRows = 6;
    }

    public static class MosquitoesSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableMosquitoes = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int mosquitoChance = 25;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 6)
        public int mosquitoMaxPerPlayer = 2;

        @ConfigEntry.Gui.Tooltip
        public boolean enableItchyEffect = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 200, max = 24000)
        public int repellentDuration = 6000;
    }

    public static class InfoTooltipsSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean showTentInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showGrillInfo = true;
    }

    public static class FoodSettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int marshmallowNutrition = 2;

        @ConfigEntry.Gui.Tooltip
        public float marshmallowSaturation = 0.1f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 72000)
        public int marshmallowEffectDuration = 20;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 20)
        public int roastedMarshmallowNutrition = 3;

        @ConfigEntry.Gui.Tooltip
        public float roastedMarshmallowSaturation = 0.6f;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 72000)
        public int roastedMarshmallowEffectDuration = 80;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 72000)
        public int meltedMarshmallowEffectDuration = 400;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 72000)
        public int charredMarshmallowEffectDuration = 400;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 72000)
        public int totallyBurntMarshmallowEffectDuration = 900;
    }

    @Override
    public void validatePostLoad() {
        grilling.grilledFoodMultiplier = Math.max(1.0f, Math.min(3.0f, grilling.grilledFoodMultiplier));
        food.marshmallowSaturation = Math.max(0.0f, Math.min(2.0f, food.marshmallowSaturation));
        food.roastedMarshmallowSaturation = Math.max(0.0f, Math.min(2.0f, food.roastedMarshmallowSaturation));
    }
}
