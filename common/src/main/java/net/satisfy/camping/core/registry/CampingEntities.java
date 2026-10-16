package net.satisfy.camping.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.satisfy.camping.Camping;
import net.satisfy.camping.core.world.entity.MosquitoSwarmEntity;

import java.util.function.BiConsumer;

public class CampingEntities {

    public static final EntityType<MosquitoSwarmEntity> MOSQUITO_SWARM = EntityType.Builder.of(MosquitoSwarmEntity::new, MobCategory.AMBIENT)
            .sized(1.2F, 2.0F)
            .clientTrackingRange(8)
            .build("mosquito_swarm");

    public static void register(BiConsumer<EntityType<?>, ResourceLocation> consumer) {
        consumer.accept(MOSQUITO_SWARM, Camping.identifier("mosquito_swarm"));
    }
}
