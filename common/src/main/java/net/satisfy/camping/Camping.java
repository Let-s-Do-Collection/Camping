package net.satisfy.camping;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.camping.core.registry.CampingItems;
import net.satisfy.foundation.rarity.FoundationRarities;
import net.satisfy.foundation.rarity.FoundationRarity;

public class Camping {

    public static void init() {
    }

    public static void registerRarities() {
        FoundationRarities.register(CampingItems.SMALL_BACKPACK, FoundationRarity.UNCOMMON);
        FoundationRarities.register(CampingItems.LARGE_BACKPACK, FoundationRarity.UNCOMMON);
        FoundationRarities.register(CampingItems.SHEEPBAG, FoundationRarity.UNCOMMON);
        FoundationRarities.register(CampingItems.MULTITOOL, FoundationRarity.UNCOMMON);
        FoundationRarities.register(CampingItems.WANDERER_BACKPACK, FoundationRarity.RARE);
        FoundationRarities.register(CampingItems.WANDERER_BAG, FoundationRarity.RARE);
        FoundationRarities.register(CampingItems.ENDERBAG, FoundationRarity.EPIC);
        FoundationRarities.register(CampingItems.ENDERPACK, FoundationRarity.MYTHIC);
        FoundationRarities.register(CampingItems.GOODYBAG, FoundationRarity.MYTHIC);
    }

    public static ResourceLocation identifier(String name) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, name);
    }
}
