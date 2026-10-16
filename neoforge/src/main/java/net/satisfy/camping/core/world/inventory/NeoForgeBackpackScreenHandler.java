package net.satisfy.camping.core.world.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.satisfy.camping.core.registry.CampingScreenHandlers;
import net.satisfy.camping.core.world.BackpackContainer;
import net.satisfy.camping.platform.Services;
import org.jetbrains.annotations.NotNull;

public class NeoForgeBackpackScreenHandler extends BackpackScreenHandler {

    public NeoForgeBackpackScreenHandler(int id, Inventory inv, BackpackContainer container, BlockPos pos, int rows) {
        super(CampingScreenHandlers.backpack(rows), id, inv, container, pos, rows);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return Services.PLATFORM.getEquippedBackpack(player) != null && !Services.PLATFORM.getEquippedBackpack(player).isEmpty();
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.container.stopOpen(player);
        var equipped = Services.PLATFORM.getEquippedBackpack(player);
        if (equipped != null && !equipped.isEmpty()) {
            BackpackData.write(equipped, this.container.getItems());
        }
        if (!player.level().isClientSide) this.container.setChanged();
    }
}
