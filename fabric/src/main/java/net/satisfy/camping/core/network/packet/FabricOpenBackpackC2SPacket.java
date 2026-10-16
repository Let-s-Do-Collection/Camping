package net.satisfy.camping.core.network.packet;

import dev.emi.trinkets.api.SlotReference;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Tuple;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleMenuProvider;
import net.satisfy.camping.Camping;
import net.satisfy.camping.core.world.BackpackContainer;
import net.satisfy.camping.core.world.inventory.BackpackScreenHandler;
import net.satisfy.camping.core.world.item.BackpackBlockItem;
import net.satisfy.camping.core.registry.CampingItems;
import net.satisfy.camping.optional.trinkets.TrinketBackpackContainer;
import net.satisfy.camping.optional.trinkets.TrinketsHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FabricOpenBackpackC2SPacket implements CustomPacketPayload {
    public static final Type<FabricOpenBackpackC2SPacket> TYPE = new Type<>(Camping.identifier("open_backpack"));
    public static final FabricOpenBackpackC2SPacket INSTANCE = new FabricOpenBackpackC2SPacket();
    public static final StreamCodec<FriendlyByteBuf, FabricOpenBackpackC2SPacket> CODEC = StreamCodec.unit(INSTANCE);

    public static void receive(ServerPlayer player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!chest.isEmpty()) {
            if (chest.is(CampingItems.ENDERPACK) || chest.is(CampingItems.ENDERBAG)) {
                Component t = chest.getHoverName();
                player.openMenu(new SimpleMenuProvider((id, inv, p) -> ChestMenu.threeRows(id, inv, player.getEnderChestInventory()), t));
                player.awardStat(Stats.OPEN_ENDERCHEST);
                return;
            }
            if (chest.getItem() instanceof BackpackBlockItem) {
                openEquipment(player, chest.getHoverName());
                return;
            }
        }

        Optional<Tuple<SlotReference, ItemStack>> opt = TrinketsHelper.getEquippedBackpack(player);
        if (opt.isPresent()) {
            SlotReference ref = opt.get().getA();
            ItemStack stack = opt.get().getB();
            if (stack.is(CampingItems.ENDERPACK) || stack.is(CampingItems.ENDERBAG)) {
                Component t = stack.getHoverName();
                player.openMenu(new SimpleMenuProvider((id, inv, p) -> ChestMenu.threeRows(id, inv, player.getEnderChestInventory()), t));
                player.awardStat(Stats.OPEN_ENDERCHEST);
                return;
            }
            if (stack.getItem() instanceof BackpackBlockItem) {
                openTrinket(player, ref, stack.getHoverName(), stack);
            }
        }
    }

    private static void openEquipment(ServerPlayer player, Component title) {
        BackpackContainer c = BackpackContainer.forEquipment(player, EquipmentSlot.CHEST);
        int rows = BackpackContainer.getRows(player.getItemBySlot(EquipmentSlot.CHEST));
        MenuConstructor ctor = (sync, inv, p) -> BackpackScreenHandler.create(sync, inv, c, BlockPos.ZERO, rows);
        MenuProvider prov = new MenuProvider() {
            @Override public @NotNull Component getDisplayName() { return title; }
            @Override public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player p) { return ctor.createMenu(id, inv, p); }
        };
        player.openMenu(prov);
    }

    private static void openTrinket(ServerPlayer player, dev.emi.trinkets.api.SlotReference ref, Component title, ItemStack stack) {
        TrinketBackpackContainer c = TrinketBackpackContainer.of(player, ref, stack);
        int rows = BackpackContainer.getRows(stack);
        MenuConstructor ctor = (sync, inv, p) -> BackpackScreenHandler.create(sync, inv, c, BlockPos.ZERO, rows);
        MenuProvider prov = new MenuProvider() {
            @Override public @NotNull Component getDisplayName() { return title; }
            @Override public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player p) { return ctor.createMenu(id, inv, p); }
        };
        player.openMenu(prov);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }
}
