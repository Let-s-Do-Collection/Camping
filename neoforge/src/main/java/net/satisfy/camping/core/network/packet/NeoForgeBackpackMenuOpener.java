package net.satisfy.camping.core.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.CustomData;
import net.satisfy.camping.core.registry.CampingItems;
import net.satisfy.camping.core.world.BackpackContainer;
import net.satisfy.camping.core.world.block.entity.BackpackBlockEntity;
import net.satisfy.camping.core.world.inventory.NeoForgeBackpackScreenHandler;
import net.satisfy.camping.platform.Services;

public final class NeoForgeBackpackMenuOpener {
    public static void open(ServerPlayer player) {
        ItemStack equipped = Services.PLATFORM.getEquippedBackpack(player);
        if (equipped == null || equipped.isEmpty()) return;

        if (equipped.is(CampingItems.ENDERPACK) || equipped.is(CampingItems.ENDERBAG)) {
            player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> ChestMenu.threeRows(id, inv, player.getEnderChestInventory()),
                    Component.translatable("container.camping.enderpack")
            ));
            return;
        }

        boolean isBackpack = false;
        for (Item backpack : CampingItems.BACKPACKS.get()) { if (equipped.is(backpack)) { isBackpack = true; break; } }
        if (!isBackpack) return;

        NonNullList<ItemStack> items = NonNullList.withSize(BackpackBlockEntity.CONTAINER_SIZE, ItemStack.EMPTY);

        CustomData beData = equipped.get(DataComponents.BLOCK_ENTITY_DATA);
        if (beData != null) {
            CompoundTag tag = beData.copyTag();
            if (tag.contains("Items", 9)) {
                ContainerHelper.loadAllItems(tag, items, player.registryAccess());
                equipped.remove(DataComponents.BLOCK_ENTITY_DATA);
                if (isAllEmpty(items)) equipped.remove(DataComponents.CONTAINER);
                else equipped.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
            } else {
                equipped.remove(DataComponents.BLOCK_ENTITY_DATA);
            }
        }

        NonNullList<ItemStack> tmp = NonNullList.withSize(BackpackBlockEntity.CONTAINER_SIZE, ItemStack.EMPTY);
        equipped.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(tmp);
        final NonNullList<ItemStack> itemsFinal = tmp;

        BlockPos pos = player.blockPosition();
        int rows = BackpackContainer.getRows(equipped);
        player.openMenu(new SimpleMenuProvider(
                (id, inv, p) -> new NeoForgeBackpackScreenHandler(id, inv, new BackpackContainer(itemsFinal, p, EquipmentSlot.CHEST), pos, rows),
                Component.translatable("container.camping.backpack")
        ));
    }

    private static boolean isAllEmpty(NonNullList<ItemStack> list) {
        for (ItemStack s : list) if (!s.isEmpty()) return false;
        return true;
    }

    private NeoForgeBackpackMenuOpener() {}
}
