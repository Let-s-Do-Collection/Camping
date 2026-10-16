package net.satisfy.camping.client.gui.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.camping.core.world.block.GrillBlock;
import net.satisfy.camping.core.world.block.entity.GrillBlockEntity;
import net.satisfy.camping.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import net.satisfy.foundation.tooltip.TooltipBorder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GrillInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (hit == null || !PlatformHelper.showGrillInfo() || !(state.getBlock() instanceof GrillBlock) || !(level.getBlockEntity(pos) instanceof GrillBlockEntity grill)) {
            return List.of();
        }
        List<InfoSection> sections = new ArrayList<>();
        boolean lit = state.getValue(GrillBlock.LIT);
        if (!lit) {
            sections.add(InfoSection.title(Component.translatable("hud.camping.grill.needs_heat").withStyle(ChatFormatting.GOLD)));
        }
        List<ItemStack> food = grill.getItems().stream().filter(stack -> !stack.isEmpty()).toList();
        if (!food.isEmpty()) {
            sections.add(InfoSection.icons(Component.translatable("hud.camping.grill.on_grill"), food, InfoSection.ROW_COLUMNS).withDecorations());
        }
        boolean onTop = hit.getDirection() == Direction.UP;
        boolean slotFilled = onTop && !grill.getItems().get(GrillBlockEntity.slotAt(state, pos, hit.getLocation())).isEmpty();
        if (slotFilled) {
            sections.add(InfoSection.title(Component.translatable("hud.camping.grill.hint_flip").withStyle(ChatFormatting.GRAY)));
        } else if (lit && food.size() < grill.getItems().size()) {
            sections.add(InfoSection.title(Component.translatable("hud.camping.grill.hint_add").withStyle(ChatFormatting.GRAY)));
        }
        return sections;
    }

    @Override
    public void beforeBackground(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof GrillBlock && state.getValue(GrillBlock.LIT)) {
            TooltipBorder.markHot();
        }
    }
}
