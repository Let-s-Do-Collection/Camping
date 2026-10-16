package net.satisfy.camping;

import net.satisfy.camping.client.gui.overlay.GrillInfoProvider;
import net.satisfy.camping.client.gui.overlay.TentInfoProvider;
import net.satisfy.camping.client.world.block.renderer.GrillRenderer;
import net.satisfy.camping.client.world.block.renderer.TentRenderer;
import net.satisfy.camping.core.registry.CampingBlockEntities;
import net.satisfy.camping.platform.Services;
import net.satisfy.foundation.overlay.BlockInfoOverlay;

public class CampingClient {
    public static void init() {
        Services.REGISTER.blockEntityRenderer(CampingBlockEntities.GRILL, GrillRenderer::new);
        Services.REGISTER.blockEntityRenderer(CampingBlockEntities.TENT, TentRenderer::new);
        registerInfoProviders();
    }

    public static void registerInfoProviders() {
        BlockInfoOverlay.registerProvider(new TentInfoProvider());
        BlockInfoOverlay.registerProvider(new GrillInfoProvider());
    }
}
