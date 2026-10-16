package net.satisfy.camping.core.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.satisfy.camping.Camping;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NeoForgeRegistry {

    public static void register(IEventBus modEventBus) {
        bind(modEventBus, Registries.BLOCK, CampingBlocks::register);
        bind(modEventBus, Registries.BLOCK_ENTITY_TYPE, CampingBlockEntities::register);
        bind(modEventBus, Registries.ENTITY_TYPE, CampingEntities::register);
        bind(modEventBus, Registries.ITEM, CampingItems::register);
        bind(modEventBus, Registries.PARTICLE_TYPE, CampingParticles::register);
        bind(modEventBus, Registries.MOB_EFFECT, CampingEffects::register);
        bind(modEventBus, Registries.SOUND_EVENT, CampingSounds::register);
        bind(modEventBus, Registries.CREATIVE_MODE_TAB, NeoForgeRegistry::registerTab);
        bind(modEventBus, Registries.MENU, CampingScreenHandlers::register);
        bind(modEventBus, NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, NeoForgeCampingLootModifiers::register);
        bind(modEventBus, Registries.RECIPE_SERIALIZER, NeoForgeCampingRecipes::register);
    }

    private static <T> void bind(IEventBus bus, ResourceKey<Registry<T>> registry, Consumer<BiConsumer<T, ResourceLocation>> source) {
        bus.addListener((RegisterEvent event) -> {
            if (registry.equals(event.getRegistryKey())) {
                source.accept((t, rl) -> event.register(registry, rl, () -> t));
            }
        });
    }

    public static final CreativeModeTab CREATIVE_TAB = CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> new ItemStack(CampingItems.LARGE_BACKPACK))
            .title(Component.translatable("itemGroup.camping"))
            .displayItems((p, out) -> CampingItems.CREATIVE_TAB_ITEMS.forEach(out::accept))
            .build();

    public static void registerTab(BiConsumer<CreativeModeTab, ResourceLocation> consumer) {
        consumer.accept(CREATIVE_TAB, Camping.identifier("creative_tab"));
    }
}
