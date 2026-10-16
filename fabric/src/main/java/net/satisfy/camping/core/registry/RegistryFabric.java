package net.satisfy.camping.core.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.satisfy.camping.Camping;
import net.satisfy.camping.core.world.recipe.BackpackWrapperLookup;

import java.util.function.BiConsumer;

public class RegistryFabric {

    public static void register() {
        CampingBlocks.register(bind(BuiltInRegistries.BLOCK));
        CampingBlockEntities.register(bind(BuiltInRegistries.BLOCK_ENTITY_TYPE));
        CampingEntities.register(bind(BuiltInRegistries.ENTITY_TYPE));
        CampingItems.register(bind(BuiltInRegistries.ITEM));
        CampingParticles.register(bind(BuiltInRegistries.PARTICLE_TYPE));
        CampingEffects.register(bind(BuiltInRegistries.MOB_EFFECT));
        CampingSounds.register(bind(BuiltInRegistries.SOUND_EVENT));
        RegistryFabric.registerTab(bind(BuiltInRegistries.CREATIVE_MODE_TAB));

        CampingScreenHandlers.register(bind(BuiltInRegistries.MENU));

        FabricCampingLootModifiers.register();
        FabricCampingRecipes.register();

        BackpackWrapperLookup.init();
    }

    private static <T> BiConsumer<T, ResourceLocation> bind(Registry<? super T> registry) {
        return (t, id) -> Registry.register(registry, id, t);
    }

    public static final CreativeModeTab CREATIVE_TAB = FabricItemGroup.builder()
            .icon(() -> new ItemStack(CampingItems.LARGE_BACKPACK))
            .title(Component.translatable("itemGroup.camping").setStyle(Style.EMPTY.withColor(0x556B2F)))
            .displayItems((itemDisplayParameters, output) -> CampingItems.CREATIVE_TAB_ITEMS.forEach(output::accept))
            .build();

    public static void registerTab(BiConsumer<CreativeModeTab, ResourceLocation> consumer) {
        consumer.accept(CREATIVE_TAB, Camping.identifier("creative_tab"));
    }
}
