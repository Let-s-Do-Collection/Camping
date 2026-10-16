package net.satisfy.camping;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.satisfy.camping.client.gui.screens.inventory.BackpackScreen;
import net.satisfy.camping.client.keys.FabricOpenBackpackKey;
import net.satisfy.camping.client.model.*;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.satisfy.camping.client.renderer.entity.MosquitoSwarmRenderer;
import net.satisfy.camping.core.registry.CampingEntities;
import net.satisfy.camping.client.particle.RotationArrowParticle;
import net.satisfy.camping.core.network.FabricCampingNetwork;
import net.satisfy.camping.core.registry.CampingBlocks;
import net.satisfy.camping.core.registry.CampingParticles;
import net.satisfy.camping.core.registry.CampingScreenHandlers;
import net.satisfy.camping.core.util.GrillingUtil;
import net.satisfy.camping.optional.trinkets.TrinketsHelper;
import net.satisfy.camping.platform.Services;


public class CampingClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CampingClient.init();

        FabricCampingNetwork.registerClient();
        FabricOpenBackpackKey.register();

        ParticleFactoryRegistry.getInstance().register(CampingParticles.ROTATION_ARROW, RotationArrowParticle.Provider::new);
        EntityRendererRegistry.register(CampingEntities.MOSQUITO_SWARM, MosquitoSwarmRenderer::new);

        ItemTooltipCallback.EVENT.register((stack, ctx, type, lines) -> GrillingUtil.addGrilledTooltip(stack, lines));

        BlockRenderLayerMap.INSTANCE.putBlock(CampingBlocks.GRILL, RenderType.cutout());
        for (Block block : CampingBlocks.TENTS.values()) {
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout());
        }

        EntityModelLayerRegistry.registerModelLayer(EnderpackModel.LAYER_LOCATION, EnderpackModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(EnderbagModel.LAYER_LOCATION, EnderbagModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(GoodybagModel.LAYER_LOCATION, GoodybagModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(LargeBackpackModel.LAYER_LOCATION, LargeBackpackModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(SheepbagModel.LAYER_LOCATION, SheepbagModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(SmallBackpackModel.LAYER_LOCATION, SmallBackpackModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(WandererBackpackModel.LAYER_LOCATION, WandererBackpackModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(WandererBagModel.LAYER_LOCATION, WandererBagModel::createBodyLayer);

        if (Services.PLATFORM.isModLoaded("trinkets")) {
            TrinketsHelper.registerRenderersForTrinkets();
        }

        CampingScreenHandlers.BACKPACKS.forEach(type -> MenuScreens.register(type, BackpackScreen::new));
    }
}
