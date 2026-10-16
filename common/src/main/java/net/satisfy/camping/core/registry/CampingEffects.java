package net.satisfy.camping.core.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.satisfy.camping.Camping;
import net.satisfy.camping.core.world.effect.ItchyEffect;

import java.util.function.BiConsumer;

public class CampingEffects {

    public static final MobEffect ITCHY = new ItchyEffect(MobEffectCategory.HARMFUL, 0xC4553B);
    public static final MobEffect REPELLENT = new MobEffect(MobEffectCategory.BENEFICIAL, 0x8DB360) {};

    public static Holder<MobEffect> itchy() {
        return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(ITCHY);
    }

    public static Holder<MobEffect> repellent() {
        return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(REPELLENT);
    }

    public static void register(BiConsumer<MobEffect, ResourceLocation> consumer) {
        consumer.accept(ITCHY, Camping.identifier("itchy"));
        consumer.accept(REPELLENT, Camping.identifier("repellent"));
    }
}
