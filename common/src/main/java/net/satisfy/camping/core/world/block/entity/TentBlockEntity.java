package net.satisfy.camping.core.world.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.camping.core.registry.CampingBlockEntities;
import net.satisfy.camping.core.world.block.SleepingBagBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TentBlockEntity extends BlockEntity {
    private static final Map<ResourceKey<Level>, Set<BlockPos>> TENTS = new ConcurrentHashMap<>();

    private ItemStack bag = ItemStack.EMPTY;

    public TentBlockEntity(BlockPos pos, BlockState state) {
        super(CampingBlockEntities.TENT, pos, state);
    }

    public static boolean isSleepingBag(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SleepingBagBlock;
    }

    public ItemStack getBag() {
        return this.bag;
    }

    public boolean hasBag() {
        return !this.bag.isEmpty();
    }

    public void setBag(ItemStack stack) {
        this.bag = stack;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    public ItemStack takeBag() {
        ItemStack stack = this.bag;
        if (!stack.isEmpty()) {
            setBag(ItemStack.EMPTY);
        }
        return stack;
    }

    public @Nullable SleepingBagBlock getBagBlock() {
        if (this.bag.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SleepingBagBlock sleepingBag) {
            return sleepingBag;
        }
        return null;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.bag = ItemStack.parseOptional(registries, tag.getCompound("Bag"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.bag.isEmpty()) {
            tag.put("Bag", this.bag.save(registries));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (this.level instanceof ServerLevel serverLevel) {
            TENTS.computeIfAbsent(serverLevel.dimension(), key -> ConcurrentHashMap.newKeySet()).add(this.worldPosition.immutable());
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (this.level instanceof ServerLevel serverLevel) {
            Set<BlockPos> tents = TENTS.get(serverLevel.dimension());
            if (tents != null) {
                tents.remove(this.worldPosition);
            }
        }
    }

    public static boolean isNearTent(ServerLevel level, BlockPos pos, int range) {
        if (range <= 0) {
            return false;
        }
        Set<BlockPos> tents = TENTS.get(level.dimension());
        if (tents == null || tents.isEmpty()) {
            return false;
        }
        long rangeSqr = (long) range * range;
        for (BlockPos tent : tents) {
            if (tent.distSqr(pos) <= rangeSqr) {
                return true;
            }
        }
        return false;
    }
}
