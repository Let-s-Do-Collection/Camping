package net.satisfy.camping.core.config;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CampingNeoForgeConfig {
    public static final ModConfigSpec COMMON_CONFIG;

    public static final ModConfigSpec.BooleanValue ENABLE_GRILLING;
    public static final ModConfigSpec.BooleanValue ENABLE_GRILLED_GLINT;
    public static final ModConfigSpec.DoubleValue GRILLED_FOOD_MULTIPLIER;
    public static final ModConfigSpec.IntValue MARSHMALLOW_ROAST_DURATION;

    public static final ModConfigSpec.BooleanValue ENABLE_SLEEPING_BAG_REGENERATION;
    public static final ModConfigSpec.BooleanValue ENABLE_SLEEPING_BAG_SPAWN;
    public static final ModConfigSpec.BooleanValue ENABLE_TENT_SLEEP_NEAR_MONSTERS;
    public static final ModConfigSpec.IntValue TENT_SAFE_RANGE;
    public static final ModConfigSpec.BooleanValue ENABLE_MOSQUITOES;
    public static final ModConfigSpec.IntValue MOSQUITO_CHANCE;
    public static final ModConfigSpec.IntValue MOSQUITO_MAX_PER_PLAYER;
    public static final ModConfigSpec.BooleanValue ENABLE_ITCHY_EFFECT;
    public static final ModConfigSpec.IntValue REPELLENT_DURATION;

    public static final ModConfigSpec.BooleanValue SHOW_TENT_INFO;
    public static final ModConfigSpec.BooleanValue SHOW_GRILL_INFO;
    public static final ModConfigSpec.BooleanValue ENABLE_MONSTER_TENT_GRIEFING;

    public static final ModConfigSpec.IntValue SMALL_BACKPACK_MAX_ROWS;
    public static final ModConfigSpec.IntValue WANDERER_BAG_MAX_ROWS;
    public static final ModConfigSpec.IntValue SHEEPBAG_MAX_ROWS;
    public static final ModConfigSpec.IntValue GOODYBAG_MAX_ROWS;
    public static final ModConfigSpec.IntValue WANDERER_BACKPACK_MAX_ROWS;
    public static final ModConfigSpec.IntValue LARGE_BACKPACK_MAX_ROWS;

    public static final ModConfigSpec.IntValue MARSHMALLOW_NUTRITION;
    public static final ModConfigSpec.DoubleValue MARSHMALLOW_SATURATION;
    public static final ModConfigSpec.IntValue MARSHMALLOW_EFFECT_DURATION;
    public static final ModConfigSpec.IntValue ROASTED_MARSHMALLOW_NUTRITION;
    public static final ModConfigSpec.DoubleValue ROASTED_MARSHMALLOW_SATURATION;
    public static final ModConfigSpec.IntValue ROASTED_MARSHMALLOW_EFFECT_DURATION;
    public static final ModConfigSpec.IntValue MELTED_MARSHMALLOW_EFFECT_DURATION;
    public static final ModConfigSpec.IntValue CHARRED_MARSHMALLOW_EFFECT_DURATION;
    public static final ModConfigSpec.IntValue TOTALLY_BURNT_MARSHMALLOW_EFFECT_DURATION;

    public static boolean enableGrilling = true;
    public static boolean enableGrilledGlint = true;
    public static double grilledFoodMultiplier = 1.25;
    public static int marshmallowRoastDuration = 40;

    public static boolean enableSleepingBagRegeneration = true;
    public static boolean enableSleepingBagSpawn = false;
    public static boolean enableTentSleepNearMonsters = true;
    public static int tentSafeRange = 16;
    public static boolean enableMosquitoes = true;
    public static int mosquitoChance = 25;
    public static int mosquitoMaxPerPlayer = 2;
    public static boolean enableItchyEffect = true;
    public static int repellentDuration = 6000;

    public static boolean showTentInfo = true;
    public static boolean showGrillInfo = true;
    public static boolean enableMonsterTentGriefing = false;

    public static int smallBackpackMaxRows = 3;
    public static int wandererBagMaxRows = 3;
    public static int sheepbagMaxRows = 4;
    public static int goodybagMaxRows = 4;
    public static int wandererBackpackMaxRows = 5;
    public static int largeBackpackMaxRows = 6;

    public static int marshmallowNutrition = 2;
    public static double marshmallowSaturation = 0.1;
    public static int marshmallowEffectDuration = 20;
    public static int roastedMarshmallowNutrition = 3;
    public static double roastedMarshmallowSaturation = 0.6;
    public static int roastedMarshmallowEffectDuration = 80;
    public static int meltedMarshmallowEffectDuration = 400;
    public static int charredMarshmallowEffectDuration = 400;
    public static int totallyBurntMarshmallowEffectDuration = 900;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Cooking food on the grill and roasting marshmallows over the campfire.").push("Grilling");
        ENABLE_GRILLING = builder.comment("Food cooked on the grill becomes grilled and fills you up more than normal food.").define("enableGrilling", true);
        ENABLE_GRILLED_GLINT = builder.comment("Grilled food shimmers like an enchanted item.").define("enableGrilledGlint", true);
        GRILLED_FOOD_MULTIPLIER = builder.comment("How much more grilled food fills you up. 1.0 = like normal food, 1.25 = 25% more.").defineInRange("grilledFoodMultiplier", 1.25, 1.0, 3.0);
        MARSHMALLOW_ROAST_DURATION = builder.comment("How long a marshmallow needs over a campfire at least to reach the next roasting stage. 20 ticks = 1 second.").defineInRange("marshmallowRoastDuration", 40, 10, 600);
        builder.pop();

        builder.comment("Sleeping bags and tents.").push("Sleeping");
        ENABLE_SLEEPING_BAG_REGENERATION = builder.comment("You get Regeneration when you lie down in a sleeping bag.").define("enableSleepingBagRegeneration", true);
        ENABLE_SLEEPING_BAG_SPAWN = builder.comment("Sleeping in a sleeping bag sets your spawn point like a bed does.").define("enableSleepingBagSpawn", false);
        ENABLE_TENT_SLEEP_NEAR_MONSTERS = builder.comment("You can sleep in a tent even when monsters are nearby.").define("enableTentSleepNearMonsters", true);
        TENT_SAFE_RANGE = builder.comment("Radius in blocks around a tent where no monsters spawn naturally. 0 = off.").defineInRange("tentSafeRange", 16, 0, 64);
        ENABLE_MONSTER_TENT_GRIEFING = builder.comment("Monsters that walk into a tent tear it down, if mob griefing is on.").define("enableMonsterTentGriefing", false);
        builder.pop();

        builder.comment("How much fits into each backpack.").push("Backpacks");
        SMALL_BACKPACK_MAX_ROWS = builder.comment("How many rows of 8 slots the small backpack has. Items in rows that no longer exist are moved into free slots or handed back to you.").defineInRange("smallBackpackMaxRows", 3, 1, 6);
        WANDERER_BAG_MAX_ROWS = builder.comment("How many rows of 8 slots the wanderer bag has. Items in rows that no longer exist are moved into free slots or handed back to you.").defineInRange("wandererBagMaxRows", 3, 1, 6);
        SHEEPBAG_MAX_ROWS = builder.comment("How many rows of 8 slots the sheepbag has. Items in rows that no longer exist are moved into free slots or handed back to you.").defineInRange("sheepbagMaxRows", 4, 1, 6);
        GOODYBAG_MAX_ROWS = builder.comment("How many rows of 8 slots the goodybag has. Items in rows that no longer exist are moved into free slots or handed back to you.").defineInRange("goodybagMaxRows", 4, 1, 6);
        WANDERER_BACKPACK_MAX_ROWS = builder.comment("How many rows of 8 slots the wanderer backpack has. Items in rows that no longer exist are moved into free slots or handed back to you.").defineInRange("wandererBackpackMaxRows", 5, 1, 6);
        LARGE_BACKPACK_MAX_ROWS = builder.comment("How many rows of 8 slots the large backpack has. Items in rows that no longer exist are moved into free slots or handed back to you.").defineInRange("largeBackpackMaxRows", 6, 1, 6);
        builder.pop();

        builder.comment("Mosquito swarms that hover over water at night.").push("Mosquitoes");
        ENABLE_MOSQUITOES = builder.comment("Mosquito swarms hover over water at night in swamps, jungles and along rivers and chase you when you stand near a light source. A lit campfire nearby keeps them away.").define("enableMosquitoes", true);
        MOSQUITO_CHANCE = builder.comment("Chance in percent that a mosquito swarm shows up near you, checked every 10 seconds.").defineInRange("mosquitoChance", 25, 0, 100);
        MOSQUITO_MAX_PER_PLAYER = builder.comment("How many mosquito swarms can be around you at once.").defineInRange("mosquitoMaxPerPlayer", 2, 1, 6);
        ENABLE_ITCHY_EFFECT = builder.comment("Mosquitoes can bite you and make you itchy. It does no damage, you just scratch yourself now and then.").define("enableItchyEffect", true);
        REPELLENT_DURATION = builder.comment("How long one spray of Mosquito Repellent keeps mosquitoes away. 20 ticks = 1 second.").defineInRange("repellentDuration", 6000, 200, 24000);
        builder.pop();

        builder.comment("Info panels that show up when you look at blocks. Only affects your own screen.").push("Info Tooltips");
        SHOW_TENT_INFO = builder.comment("Shows sleeping bags and controls when you look at a tent.").define("showTentInfo", true);
        SHOW_GRILL_INFO = builder.comment("Shows the food on the grill and controls when you look at a grill.").define("showGrillInfo", true);
        builder.pop();

        builder.comment("Hunger and saturation of the food items. Changes need a game restart.").push("Food");
        MARSHMALLOW_NUTRITION = builder.comment("Hunger points restored by Marshmallow. 2 points = 1 drumstick.").defineInRange("marshmallowNutrition", 2, 0, 20);
        MARSHMALLOW_SATURATION = builder.comment("Saturation given by Marshmallow, multiplied with its hunger points.").defineInRange("marshmallowSaturation", 0.1, 0.0, 2.0);
        MARSHMALLOW_EFFECT_DURATION = builder.comment("How long the Speed effect of Marshmallow lasts. 20 ticks = 1 second.").defineInRange("marshmallowEffectDuration", 20, 0, 72000);
        ROASTED_MARSHMALLOW_NUTRITION = builder.comment("Hunger points restored by Roasted Marshmallow. 2 points = 1 drumstick.").defineInRange("roastedMarshmallowNutrition", 3, 0, 20);
        ROASTED_MARSHMALLOW_SATURATION = builder.comment("Saturation given by Roasted Marshmallow, multiplied with its hunger points.").defineInRange("roastedMarshmallowSaturation", 0.6, 0.0, 2.0);
        ROASTED_MARSHMALLOW_EFFECT_DURATION = builder.comment("How long the Speed effect of Roasted Marshmallow lasts. 20 ticks = 1 second.").defineInRange("roastedMarshmallowEffectDuration", 80, 0, 72000);
        MELTED_MARSHMALLOW_EFFECT_DURATION = builder.comment("How long the Speed effect of a melted marshmallow on a stick lasts. 20 ticks = 1 second.").defineInRange("meltedMarshmallowEffectDuration", 400, 0, 72000);
        CHARRED_MARSHMALLOW_EFFECT_DURATION = builder.comment("How long the Hunger effect of a charred marshmallow on a stick lasts. 20 ticks = 1 second.").defineInRange("charredMarshmallowEffectDuration", 400, 0, 72000);
        TOTALLY_BURNT_MARSHMALLOW_EFFECT_DURATION = builder.comment("How long the Hunger effect of a totally burnt marshmallow on a stick lasts. 20 ticks = 1 second.").defineInRange("totallyBurntMarshmallowEffectDuration", 900, 0, 72000);
        builder.pop();

        COMMON_CONFIG = builder.build();
    }

    public static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == COMMON_CONFIG) sync();
    }

    public static void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == COMMON_CONFIG) sync();
    }

    private static void sync() {
        enableGrilling = ENABLE_GRILLING.get();
        enableGrilledGlint = ENABLE_GRILLED_GLINT.get();
        grilledFoodMultiplier = GRILLED_FOOD_MULTIPLIER.get();
        marshmallowRoastDuration = MARSHMALLOW_ROAST_DURATION.get();
        enableSleepingBagRegeneration = ENABLE_SLEEPING_BAG_REGENERATION.get();
        enableSleepingBagSpawn = ENABLE_SLEEPING_BAG_SPAWN.get();
        enableTentSleepNearMonsters = ENABLE_TENT_SLEEP_NEAR_MONSTERS.get();
        tentSafeRange = TENT_SAFE_RANGE.get();
        enableMosquitoes = ENABLE_MOSQUITOES.get();
        mosquitoChance = MOSQUITO_CHANCE.get();
        mosquitoMaxPerPlayer = MOSQUITO_MAX_PER_PLAYER.get();
        enableItchyEffect = ENABLE_ITCHY_EFFECT.get();
        repellentDuration = REPELLENT_DURATION.get();
        showTentInfo = SHOW_TENT_INFO.get();
        showGrillInfo = SHOW_GRILL_INFO.get();
        enableMonsterTentGriefing = ENABLE_MONSTER_TENT_GRIEFING.get();
        smallBackpackMaxRows = SMALL_BACKPACK_MAX_ROWS.get();
        wandererBagMaxRows = WANDERER_BAG_MAX_ROWS.get();
        sheepbagMaxRows = SHEEPBAG_MAX_ROWS.get();
        goodybagMaxRows = GOODYBAG_MAX_ROWS.get();
        wandererBackpackMaxRows = WANDERER_BACKPACK_MAX_ROWS.get();
        largeBackpackMaxRows = LARGE_BACKPACK_MAX_ROWS.get();
        marshmallowNutrition = MARSHMALLOW_NUTRITION.get();
        marshmallowSaturation = MARSHMALLOW_SATURATION.get();
        marshmallowEffectDuration = MARSHMALLOW_EFFECT_DURATION.get();
        roastedMarshmallowNutrition = ROASTED_MARSHMALLOW_NUTRITION.get();
        roastedMarshmallowSaturation = ROASTED_MARSHMALLOW_SATURATION.get();
        roastedMarshmallowEffectDuration = ROASTED_MARSHMALLOW_EFFECT_DURATION.get();
        meltedMarshmallowEffectDuration = MELTED_MARSHMALLOW_EFFECT_DURATION.get();
        charredMarshmallowEffectDuration = CHARRED_MARSHMALLOW_EFFECT_DURATION.get();
        totallyBurntMarshmallowEffectDuration = TOTALLY_BURNT_MARSHMALLOW_EFFECT_DURATION.get();
    }
}
