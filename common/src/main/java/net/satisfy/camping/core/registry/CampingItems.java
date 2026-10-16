package net.satisfy.camping.core.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import net.satisfy.camping.Camping;
import net.satisfy.camping.core.util.BackpackVariant;
import net.satisfy.camping.core.world.item.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CampingItems {

    public static final Item MARSHMALLOW = new MarshmallowItem(false, new Item.Properties().food(CampingFoods.MARSHMALLOW));
    public static final Item ROASTED_MARSHMALLOW = new MarshmallowItem(true, new Item.Properties().food(CampingFoods.ROASTED_MARSHMALLOW));
    public static final Item MARSHMALLOW_ON_A_STICK = new MarshmallowOnAStickItem(new Item.Properties());

    public static final Item WALKING_STICK = new WalkingStickItem(new Item.Properties().durability(200));
    public static final Item MULTITOOL = new MultitoolItem(new Item.Properties().stacksTo(1).durability(175).fireResistant());

    public static final Item MOSQUITO_SWARM_SPAWN_EGG = new SpawnEggItem(CampingEntities.MOSQUITO_SWARM, -1, -1, new Item.Properties());
    public static final Item MOSQUITO_REPELLENT = new MosquitoRepellentItem(new Item.Properties().durability(16));

    public static final Item ENDERPACK = new EnderpackBlockItem(CampingBlocks.ENDERPACK, Camping.identifier("textures/model/enderpack.png"));
    public static final Item ENDERBAG = new EnderpackBlockItem(CampingBlocks.ENDERBAG, Camping.identifier("textures/model/enderbag.png"));

    public static final Item SMALL_BACKPACK = new BackpackBlockItem(CampingBlocks.SMALL_BACKPACK, BackpackVariant.SMALL_BACKPACK, Camping.identifier("textures/model/small_backpack.png"));
    public static final Item LARGE_BACKPACK = new BackpackBlockItem(CampingBlocks.LARGE_BACKPACK, BackpackVariant.LARGE_BACKPACK, Camping.identifier("textures/model/large_backpack.png"));
    public static final Item WANDERER_BACKPACK = new BackpackBlockItem(CampingBlocks.WANDERER_BACKPACK, BackpackVariant.WANDERER_BACKPACK, Camping.identifier("textures/model/wanderer_backpack.png"));
    public static final Item WANDERER_BAG = new BackpackBlockItem(CampingBlocks.WANDERER_BAG, BackpackVariant.WANDERER_BAG, Camping.identifier("textures/model/wanderer_bag.png"));
    public static final Item GOODYBAG = new BackpackBlockItem(CampingBlocks.GOODYBAG, BackpackVariant.GOODYBAG, Camping.identifier("textures/model/goodybag.png"));
    public static final Item SHEEPBAG = new BackpackBlockItem(CampingBlocks.SHEEPBAG, BackpackVariant.SHEEPBAG, Camping.identifier("textures/model/sheepbag.png"));

    public static final Supplier<Item[]> BACKPACKS = () -> new Item[]{SMALL_BACKPACK, LARGE_BACKPACK, WANDERER_BACKPACK, WANDERER_BAG, GOODYBAG, SHEEPBAG};
    public static final List<ItemLike> CREATIVE_TAB_ITEMS = new ArrayList<>();
    public static void register(BiConsumer<Item, ResourceLocation> consumer) {

            consumer.accept(new BlockItem(CampingBlocks.GRILL, new Item.Properties()), BuiltInRegistries.BLOCK.getKey(CampingBlocks.GRILL));

        consumer.accept(MARSHMALLOW, Camping.identifier("marshmallow"));
        consumer.accept(ROASTED_MARSHMALLOW, Camping.identifier("roasted_marshmallow"));
        consumer.accept(MARSHMALLOW_ON_A_STICK, Camping.identifier("marshmallow_on_a_stick"));

        consumer.accept(WALKING_STICK, Camping.identifier("walking_stick"));
        consumer.accept(MULTITOOL, Camping.identifier("multitool"));
        consumer.accept(MOSQUITO_REPELLENT, Camping.identifier("mosquito_repellent"));
        consumer.accept(MOSQUITO_SWARM_SPAWN_EGG, Camping.identifier("mosquito_swarm_spawn_egg"));

        consumer.accept(ENDERPACK, Camping.identifier("enderpack"));
        consumer.accept(ENDERBAG, Camping.identifier("enderbag"));
        consumer.accept(GOODYBAG, Camping.identifier("goodybag"));
        consumer.accept(LARGE_BACKPACK, Camping.identifier("large_backpack"));
        consumer.accept(SHEEPBAG, Camping.identifier("sheepbag"));
        consumer.accept(SMALL_BACKPACK, Camping.identifier("small_backpack"));
        consumer.accept(WANDERER_BACKPACK, Camping.identifier("wanderer_backpack"));
        consumer.accept(WANDERER_BAG, Camping.identifier("wanderer_bag"));

        CREATIVE_TAB_ITEMS.add(CampingBlocks.GRILL);
        CREATIVE_TAB_ITEMS.add(ENDERPACK);
        CREATIVE_TAB_ITEMS.add(ENDERBAG);
        CREATIVE_TAB_ITEMS.add(GOODYBAG);
        CREATIVE_TAB_ITEMS.add(LARGE_BACKPACK);
        CREATIVE_TAB_ITEMS.add(SHEEPBAG);
        CREATIVE_TAB_ITEMS.add(SMALL_BACKPACK);
        CREATIVE_TAB_ITEMS.add(WANDERER_BACKPACK);
        CREATIVE_TAB_ITEMS.add(WANDERER_BAG);
        CREATIVE_TAB_ITEMS.add(WALKING_STICK);
        CREATIVE_TAB_ITEMS.add(MULTITOOL);
        CREATIVE_TAB_ITEMS.add(MOSQUITO_REPELLENT);
        CREATIVE_TAB_ITEMS.add(MARSHMALLOW);
        CREATIVE_TAB_ITEMS.add(ROASTED_MARSHMALLOW);
        CREATIVE_TAB_ITEMS.add(MARSHMALLOW_ON_A_STICK);

        CampingBlocks.SLEEPING_BAGS.forEach((s, block) -> {
            consumer.accept(new BlockItem(block, new Item.Properties()),  BuiltInRegistries.BLOCK.getKey(block));
            CREATIVE_TAB_ITEMS.add(block);
        });

        CampingBlocks.TENTS.forEach((s, block) -> {
            consumer.accept(new BlockItem(block, new Item.Properties()), BuiltInRegistries.BLOCK.getKey(block));
            CREATIVE_TAB_ITEMS.add(block);
        });

        CREATIVE_TAB_ITEMS.add(MOSQUITO_SWARM_SPAWN_EGG);
    }
}
