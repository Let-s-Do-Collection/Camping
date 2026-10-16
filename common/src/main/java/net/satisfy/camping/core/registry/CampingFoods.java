package net.satisfy.camping.core.registry;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.satisfy.camping.platform.PlatformHelper;

public class CampingFoods {
    public static final FoodProperties MARSHMALLOW = new FoodProperties.Builder()
            .nutrition(PlatformHelper.getNutrition("marshmallow"))
            .saturationModifier(PlatformHelper.getSaturation("marshmallow"))
            .effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, PlatformHelper.getEffectDuration("marshmallow"), 0), 0.5f)
            .build();

    public static final FoodProperties ROASTED_MARSHMALLOW = new FoodProperties.Builder()
            .nutrition(PlatformHelper.getNutrition("roasted_marshmallow"))
            .saturationModifier(PlatformHelper.getSaturation("roasted_marshmallow"))
            .effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, PlatformHelper.getEffectDuration("roasted_marshmallow"), 1), 1.0f)
            .build();
}
