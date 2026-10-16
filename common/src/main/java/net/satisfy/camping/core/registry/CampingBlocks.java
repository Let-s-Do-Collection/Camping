package net.satisfy.camping.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.satisfy.camping.Camping;
import net.satisfy.camping.core.util.BackpackVariant;
import net.satisfy.camping.core.util.EnderpackVariant;
import net.satisfy.camping.core.world.block.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class CampingBlocks {

    public static final BlockBehaviour.Properties BACKPACK_BEHAVIOUR = BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BIT).strength(1.5F).sound(SoundType.CANDLE).ignitedByLava().noOcclusion().noTerrainParticles().instabreak();

    public static final Block GRILL = new GrillBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON).lightLevel((state) -> state.getValue(GrillBlock.LIT) ? 10 : 0));

    public static final Block ENDERPACK = new EnderpackBlock(BACKPACK_BEHAVIOUR, EnderpackVariant.ENDERPACK);
    public static final Block ENDERBAG = new EnderpackBlock(BACKPACK_BEHAVIOUR, EnderpackVariant.ENDERBAG);

    public static final Block GOODYBAG = new BackpackBlock(BACKPACK_BEHAVIOUR, BackpackVariant.GOODYBAG);
    public static final Block LARGE_BACKPACK = new SleepingBagBackpackBlock(BACKPACK_BEHAVIOUR, BackpackVariant.LARGE_BACKPACK, 12.0 / 16.0);
    public static final Block SHEEPBAG = new BackpackBlock(BACKPACK_BEHAVIOUR, BackpackVariant.SHEEPBAG);
    public static final Block SMALL_BACKPACK = new BackpackBlock(BACKPACK_BEHAVIOUR, BackpackVariant.SMALL_BACKPACK);
    public static final Block WANDERER_BACKPACK = new SleepingBagBackpackBlock(BACKPACK_BEHAVIOUR, BackpackVariant.WANDERER_BACKPACK, 8.0 / 16.0);
    public static final Block WANDERER_BAG = new BackpackBlock(BACKPACK_BEHAVIOUR, BackpackVariant.WANDERER_BAG);

    public static final Map<String, Block> SLEEPING_BAGS = new HashMap<>();
    public static final Map<String, Block> TENTS = new HashMap<>();

    static {
        for (DyeColor dyeColor : DyeColor.values()) {
            TENTS.put(dyeColor.getName(), new TentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_WOOL).pushReaction(PushReaction.IGNORE).instabreak().dynamicShape(), dyeColor));
        }
    }

    public static void register(BiConsumer<Block, ResourceLocation> consumer) {
        consumer.accept(GRILL, Camping.identifier("grill"));

        consumer.accept(ENDERPACK, Camping.identifier("enderpack"));
        consumer.accept(ENDERBAG, Camping.identifier("enderbag"));

        consumer.accept(GOODYBAG, Camping.identifier("goodybag"));
        consumer.accept(LARGE_BACKPACK, Camping.identifier("large_backpack"));
        consumer.accept(SHEEPBAG, Camping.identifier("sheepbag"));
        consumer.accept(SMALL_BACKPACK, Camping.identifier("small_backpack"));
        consumer.accept(WANDERER_BACKPACK, Camping.identifier("wanderer_backpack"));
        consumer.accept(WANDERER_BAG, Camping.identifier("wanderer_bag"));

        for (DyeColor dyeColor : DyeColor.values()) {

            String colorName = dyeColor.getName();

            Block dyedSleepingBag = new SleepingBagBlock(dyeColor);
            SLEEPING_BAGS.put(colorName, dyedSleepingBag);
            consumer.accept(dyedSleepingBag, Camping.identifier("sleeping_bag_" + colorName));

            consumer.accept(TENTS.get(colorName), Camping.identifier("tent_" + colorName));
        }
    }
}
