package net.satisfy.camping.core.world.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.camping.core.registry.CampingBlockEntities;
import net.satisfy.camping.core.world.BackpackContainer;
import net.satisfy.camping.core.world.block.BackpackBlock;
import net.satisfy.camping.core.world.inventory.BackpackScreenHandler;
import net.satisfy.camping.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;

public class BackpackBlockEntity extends BaseContainerBlockEntity {
    public static final int COLUMNS = 8;
    public static final int MAX_ROWS = 6;
    public static final int CONTAINER_SIZE = COLUMNS * MAX_ROWS;
    public static final String ITEMS_TAG = "Items";
    private NonNullList<ItemStack> itemStacks;

    public BackpackBlockEntity(BlockPos pos, BlockState state) {
        super(CampingBlockEntities.BACKPACK, pos, state);
        this.itemStacks = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    }

    public int getContainerSize() {
        return this.getRows() * COLUMNS;
    }

    public int getRows() {
        return this.getBlockState().getBlock() instanceof BackpackBlock backpack ? PlatformHelper.getBackpackRows(backpack.getVariant()) : MAX_ROWS;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : this.itemStacks) if (!s.isEmpty()) return false;
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return this.itemStacks.get(index);
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        ItemStack out = ContainerHelper.removeItem(this.itemStacks, index, count);
        if (!out.isEmpty()) this.setChanged();
        return out;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.itemStacks, index);
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        this.itemStacks.set(index, stack);
        if (stack.getCount() > this.getMaxStackSize()) stack.setCount(this.getMaxStackSize());
        this.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) return false;
        double dx = this.worldPosition.getX() + 0.5;
        double dy = this.worldPosition.getY() + 0.5;
        double dz = this.worldPosition.getZ() + 0.5;
        return player.distanceToSqr(dx, dy, dz) <= 64.0;
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.camping.backpack");
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        this.itemStacks = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
        if (tag.contains(ITEMS_TAG, 9)) {
            ContainerHelper.loadAllItems(tag, this.itemStacks, provider);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        ContainerHelper.saveAllItems(tag, this.itemStacks, provider);
        super.saveAdditional(tag, provider);
    }


    @NotNull
    public NonNullList<ItemStack> getItems() {
        return this.itemStacks;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> nonNullList) {
        this.itemStacks = nonNullList;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inv) {
        return BackpackScreenHandler.create(id, inv, new BackpackContainer(this.itemStacks), this.worldPosition, this.getRows());
    }

    @Override
    public void clearContent() {
        this.itemStacks.clear();
    }
}
