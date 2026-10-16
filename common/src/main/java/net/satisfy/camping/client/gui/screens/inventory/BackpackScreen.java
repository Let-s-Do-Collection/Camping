package net.satisfy.camping.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.satisfy.camping.Camping;
import net.satisfy.camping.core.world.inventory.BackpackScreenHandler;
import org.jetbrains.annotations.NotNull;

public class BackpackScreen extends AbstractContainerScreen<BackpackScreenHandler> {
    private static final ResourceLocation CONTAINER_LOCATION = Camping.identifier("textures/gui/backpack.png");

    public BackpackScreen(BackpackScreenHandler screenHandler, Inventory inventory, Component component) {
        super(screenHandler, inventory, component);
        this.imageHeight = 166 + (screenHandler.getRows() - 3) * 18;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.titleLabelY = -8;
        this.inventoryLabelY = this.imageHeight - 90;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int i, int j, float f) {
        this.renderBackground(guiGraphics, i, j, f);
        super.render(guiGraphics, i, j, f);
        this.renderTooltip(guiGraphics, i, j);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float f, int i, int j) {
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2 - 20;
        int rows = this.menu.getRows();
        guiGraphics.blit(CONTAINER_LOCATION, k, l + 5, 0, 0, this.imageWidth, 26);
        for (int row = 0; row < rows; row++) {
            guiGraphics.blit(CONTAINER_LOCATION, k, l + 31 + row * 18, 0, 26, this.imageWidth, 18);
        }
        guiGraphics.blit(CONTAINER_LOCATION, k, l + 31 + rows * 18, 0, 80, this.imageWidth, 103);
    }
}
