package net.satisfy.camping.core.world.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.camping.core.registry.CampingScreenHandlers;
import net.satisfy.camping.core.registry.CampingTags;
import net.satisfy.camping.core.world.BackpackContainer;
import net.satisfy.camping.core.world.block.BackpackBlock;
import net.satisfy.camping.core.world.block.entity.BackpackBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BackpackScreenHandler extends AbstractContainerMenu {
    final BackpackContainer container;
    private final Player player;
    private final BlockPos blockPos;
    private final boolean portable;
    private final int rows;

    public static BackpackScreenHandler create(int syncId, Inventory playerInventory, BackpackContainer container, BlockPos blockPos, int rows) {
        return new BackpackScreenHandler(CampingScreenHandlers.backpack(rows), syncId, playerInventory, container, blockPos, rows);
    }

    public BackpackScreenHandler(MenuType<?> type, int syncId, Inventory playerInventory, BackpackContainer container, BlockPos blockPos, int rows) {
        super(type, syncId);
        checkContainerSize(container, BackpackBlockEntity.CONTAINER_SIZE);
        this.container = container;
        this.player = playerInventory.player;
        this.blockPos = blockPos;
        this.portable = blockPos.equals(BlockPos.ZERO);
        this.rows = rows;
        container.startOpen(playerInventory.player);
        if (!this.player.level().isClientSide) this.moveOverflow();
        for (int j = 0; j < rows; ++j) {
            for (int k = 0; k < BackpackBlockEntity.COLUMNS; ++k) {
                this.addSlot(new Slot(container, k + j * BackpackBlockEntity.COLUMNS, 17 + k * 18, 12 + j * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return !stack.is(CampingTags.BACKPACK_BLACKLIST);
                    }
                });
            }
        }
        int oy = 1 + (rows - 3) * 18;
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 85 + j * 18 + oy));
            }
        }
        for (int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 143 + oy));
        }
    }

    public BackpackScreenHandler(int syncId, Inventory playerInventory, int rows) {
        this(CampingScreenHandlers.backpack(rows), syncId, playerInventory, new BackpackContainer(NonNullList.withSize(BackpackBlockEntity.CONTAINER_SIZE, ItemStack.EMPTY)), BlockPos.ZERO, rows);
    }

    public int getRows() {
        return this.rows;
    }

    private void moveOverflow() {
        int size = this.rows * BackpackBlockEntity.COLUMNS;
        for (int i = size; i < this.container.getContainerSize(); i++) {
            ItemStack stack = this.container.getItem(i);
            if (stack.isEmpty()) continue;
            this.container.setItem(i, ItemStack.EMPTY);
            for (int j = 0; j < size && !stack.isEmpty(); j++) {
                if (this.container.getItem(j).isEmpty()) {
                    this.container.setItem(j, stack);
                    stack = ItemStack.EMPTY;
                }
            }
            if (!stack.isEmpty()) this.player.getInventory().placeItemBackInInventory(stack);
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        if (i < 0 || i >= this.slots.size()) return ItemStack.EMPTY;
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack in = slot.getItem();
        ret = in.copy();
        int size = this.rows * BackpackBlockEntity.COLUMNS;
        if (i < size) {
            if (!this.moveItemStackTo(in, size, this.slots.size(), true)) return ItemStack.EMPTY;
        } else if (!this.moveItemStackTo(in, 0, size, false)) {
            return ItemStack.EMPTY;
        }
        if (in.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        if (in.getCount() == ret.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, in);
        return ret;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (!this.player.isAlive()) {
            this.player.closeContainer();
            return;
        }
        if (!this.player.level().isClientSide) {
            this.container.setChanged();
            if (!this.portable) {
                if (this.player.distanceToSqr(this.blockPos.getX() + 0.5, this.blockPos.getY() + 0.5, this.blockPos.getZ() + 0.5) > 64.0) {
                    this.player.closeContainer();
                }
            }
        }
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.container.stopOpen(player);
        if (!player.level().isClientSide) this.container.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.portable) return true;
        BlockState s = player.level().getBlockState(this.blockPos);
        Block b = s.getBlock();
        boolean ok = b instanceof BackpackBlock && (player.level().getBlockEntity(this.blockPos) != null && !Objects.requireNonNull(player.level().getBlockEntity(this.blockPos)).isRemoved());
        return this.container.stillValid(player) && (ok || (b == Blocks.AIR && player.distanceToSqr(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < 2.0D));
    }
}
