package net.satisfy.camping.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.satisfy.camping.Camping;
import net.satisfy.camping.core.world.block.entity.BackpackBlockEntity;
import net.satisfy.camping.core.world.block.entity.EnderpackBlockEntity;
import net.satisfy.camping.core.world.block.entity.GrillBlockEntity;
import net.satisfy.camping.core.world.block.entity.TentBlockEntity;
import net.minecraft.world.level.block.Block;
import net.satisfy.camping.platform.Services;

import java.util.function.BiConsumer;

public class CampingBlockEntities {

    public static final BlockEntityType<GrillBlockEntity> GRILL = Services.REGISTER.blockEntity(GrillBlockEntity::new, CampingBlocks.GRILL);
    public static final BlockEntityType<EnderpackBlockEntity> ENDERPACK = Services.REGISTER.blockEntity(EnderpackBlockEntity::new, CampingBlocks.ENDERPACK, CampingBlocks.ENDERBAG);
    public static final BlockEntityType<BackpackBlockEntity> BACKPACK = Services.REGISTER.blockEntity(BackpackBlockEntity::new, CampingBlocks.GOODYBAG, CampingBlocks.LARGE_BACKPACK, CampingBlocks.SHEEPBAG, CampingBlocks.SMALL_BACKPACK, CampingBlocks.WANDERER_BACKPACK, CampingBlocks.WANDERER_BAG);
    public static final BlockEntityType<TentBlockEntity> TENT = Services.REGISTER.blockEntity(TentBlockEntity::new, CampingBlocks.TENTS.values().toArray(new Block[0]));

    public static void register(BiConsumer<BlockEntityType<?>, ResourceLocation> consumer) {
        consumer.accept(GRILL, Camping.identifier("grill"));
        consumer.accept(ENDERPACK, Camping.identifier("enderpack"));
        consumer.accept(BACKPACK, Camping.identifier("backpack"));
        consumer.accept(TENT, Camping.identifier("tent"));
    }
}
