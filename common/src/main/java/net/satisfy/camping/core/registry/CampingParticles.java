package net.satisfy.camping.core.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.camping.Camping;

import java.util.function.BiConsumer;

public class CampingParticles {

    public static final SimpleParticleType ROTATION_ARROW = new SimpleParticleType(false) {};

    public static void register(BiConsumer<ParticleType<?>, ResourceLocation> consumer) {
        consumer.accept(ROTATION_ARROW, Camping.identifier("rotation_arrow"));
    }
}
