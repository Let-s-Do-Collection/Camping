package net.satisfy.camping.client.gui.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.camping.core.world.block.TentBlock;
import net.satisfy.camping.core.world.block.entity.TentBlockEntity;
import net.satisfy.camping.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TentInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (hit == null || !PlatformHelper.showTentInfo() || !(state.getBlock() instanceof TentBlock)) {
            return List.of();
        }
        if (!(level.getBlockEntity(TentBlock.getMainPos(state, pos)) instanceof TentBlockEntity tent)) {
            return List.of();
        }
        List<InfoSection> sections = new ArrayList<>();
        if (tent.hasBag()) {
            sections.add(InfoSection.icons(Component.translatable("hud.camping.tent.in_tent"), List.of(tent.getBag()), InfoSection.ROW_COLUMNS).withDecorations());
            if (!level.isDay()) {
                sections.add(InfoSection.title(Component.translatable("hud.camping.tent.hint_sleep").withStyle(ChatFormatting.GRAY)));
            }
        } else {
            sections.add(InfoSection.title(Component.translatable("hud.camping.tent.needs_sleeping_bag").withStyle(ChatFormatting.GOLD)));
            sections.add(InfoSection.title(Component.translatable("hud.camping.tent.hint_add").withStyle(ChatFormatting.GRAY)));
        }
        sections.add(InfoSection.title(Component.translatable("hud.camping.tent.hint_pack_up").withStyle(ChatFormatting.GRAY)));
        return sections;
    }
}
