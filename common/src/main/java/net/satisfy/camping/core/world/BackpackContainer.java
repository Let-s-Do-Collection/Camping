package net.satisfy.camping.core.world;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.satisfy.camping.core.world.block.entity.BackpackBlockEntity;
import net.satisfy.camping.core.world.item.BackpackBlockItem;
import net.satisfy.camping.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class BackpackContainer implements Container, StackedContentsCompatible {
    private final NonNullList<ItemStack> stacks;
    private final Player player;
    private final EquipmentSlot slot;

    public static BackpackContainer forEquipment(Player p, EquipmentSlot s) {

        ItemStack stack = p.getItemBySlot(s);
        NonNullList<ItemStack> items = readFromItem(stack);
        return new BackpackContainer(items, p, s);
    }

    public BackpackContainer(NonNullList<ItemStack> stacks) {
        this(stacks, null, null);
    }

    public BackpackContainer(NonNullList<ItemStack> stacks, Player player, EquipmentSlot slot) {
        this.stacks = stacks;
        this.player = player;
        this.slot = slot;
    }

    @Override
    public int getContainerSize() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : stacks) if (!s.isEmpty()) return false;
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return stacks.get(i);
    }

    @Override
    public @NotNull ItemStack removeItem(int i, int j) {
        ItemStack out = ContainerHelper.removeItem(stacks, i, j);
        if (!out.isEmpty()) setChanged();
        return out;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(stacks, i);
    }

    @Override
    public void setItem(int i, @NotNull ItemStack itemStack) {
        stacks.set(i, itemStack);
        setChanged();
    }

    @Override
    public boolean stillValid(Player p) {
        return !p.isDeadOrDying();
    }

    @Override
    public void clearContent() {
        Collections.fill(stacks, ItemStack.EMPTY);
        setChanged();
    }

    @Override
    public void fillStackedContents(@NotNull StackedContents sc) {
        for (ItemStack s : stacks) sc.accountStack(s);
        setChanged();
    }

    @Override
    public void setChanged() {
        if (player == null || player.level().isClientSide || slot == null) return;
        ItemStack eq = player.getItemBySlot(slot);
        if (eq.isEmpty() || !(eq.getItem() instanceof BackpackBlockItem)) return;

        eq.remove(DataComponents.BLOCK_ENTITY_DATA);

        if (allEmpty(stacks)) {
            eq.remove(DataComponents.CONTAINER);
        } else {
            eq.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(stacks));
        }
        player.setItemSlot(slot, eq);
        player.getInventory().setChanged();
    }

    public List<ItemStack> getStacks() {
        return stacks;
    }

    public static void writeToItem(ItemStack stack, NonNullList<ItemStack> items) {
        stack.remove(DataComponents.BLOCK_ENTITY_DATA);

        if (allEmpty(items)) {
            stack.remove(DataComponents.CONTAINER);
        } else {
            stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        }
    }


    public static NonNullList<ItemStack> readFromItem(ItemStack stack) {
        NonNullList<ItemStack> list = NonNullList.withSize(BackpackBlockEntity.CONTAINER_SIZE, ItemStack.EMPTY);
        stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(list);
        return list;
    }

    public static int getRows(ItemStack stack) {
        return stack.getItem() instanceof BackpackBlockItem item ? PlatformHelper.getBackpackRows(item.variant) : BackpackBlockEntity.MAX_ROWS;
    }

    private static boolean allEmpty(NonNullList<ItemStack> items) {
        for (ItemStack s : items) if (!s.isEmpty()) return false;
        return true;
    }

    public NonNullList<ItemStack> getItems() {
        return this.stacks;
    }
}
