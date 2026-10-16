package net.satisfy.camping.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.satisfy.camping.Camping;
import net.satisfy.camping.core.world.block.entity.BackpackBlockEntity;
import net.satisfy.camping.core.world.inventory.BackpackScreenHandler;
import net.satisfy.camping.platform.Services;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public class CampingScreenHandlers {
    public static final List<MenuType<BackpackScreenHandler>> BACKPACKS = IntStream.rangeClosed(1, BackpackBlockEntity.MAX_ROWS)
            .mapToObj(rows -> Services.PLATFORM.createMenuType((id, inv) -> new BackpackScreenHandler(id, inv, rows), FeatureFlags.VANILLA_SET))
            .toList();

    public static MenuType<BackpackScreenHandler> backpack(int rows) {
        return BACKPACKS.get(Math.max(1, Math.min(BackpackBlockEntity.MAX_ROWS, rows)) - 1);
    }

    public static void register(BiConsumer<MenuType<?>, ResourceLocation> consumer) {
        for (int rows = 1; rows <= BACKPACKS.size(); rows++) {
            consumer.accept(backpack(rows), Camping.identifier("backpack_" + rows));
        }
    }
}
