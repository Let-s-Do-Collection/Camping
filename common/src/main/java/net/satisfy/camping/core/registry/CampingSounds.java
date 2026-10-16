package net.satisfy.camping.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.satisfy.camping.Camping;

import java.util.function.BiConsumer;

public class CampingSounds {

    public static final SoundEvent MOSQUITO_BUZZ = SoundEvent.createVariableRangeEvent(Camping.identifier("mosquito_buzz"));

    public static void register(BiConsumer<SoundEvent, ResourceLocation> consumer) {
        consumer.accept(MOSQUITO_BUZZ, MOSQUITO_BUZZ.getLocation());
    }
}
