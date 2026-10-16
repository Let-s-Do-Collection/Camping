package net.satisfy.camping;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.satisfy.camping.client.gui.screens.inventory.BackpackScreen;
import net.satisfy.camping.client.model.EnderbagModel;
import net.satisfy.camping.client.model.EnderpackModel;
import net.satisfy.camping.client.model.GoodybagModel;
import net.satisfy.camping.client.model.LargeBackpackModel;
import net.satisfy.camping.client.model.SheepbagModel;
import net.satisfy.camping.client.model.SmallBackpackModel;
import net.satisfy.camping.client.model.WandererBackpackModel;
import net.satisfy.camping.client.model.WandererBagModel;
import net.satisfy.camping.client.renderer.entity.MosquitoSwarmRenderer;
import net.satisfy.camping.core.registry.CampingEntities;
import net.satisfy.camping.client.particle.RotationArrowParticle;
import net.satisfy.camping.client.renderer.entity.layers.BackpackRenderLayer;
import net.satisfy.camping.client.renderer.entity.layers.EnderpackRenderLayer;
import net.satisfy.camping.client.world.block.renderer.GrillRenderer;
import net.satisfy.camping.client.world.block.renderer.TentRenderer;
import net.satisfy.camping.core.registry.CampingBlockEntities;
import net.satisfy.camping.core.registry.CampingBlocks;
import net.satisfy.camping.core.registry.CampingParticles;
import net.satisfy.camping.core.registry.CampingScreenHandlers;
import net.satisfy.camping.optional.NeoForgeCuriosHelper;
import net.satisfy.camping.platform.Services;

import java.util.function.Consumer;

public class CampingClientNeoForge {
    public CampingClientNeoForge(IEventBus modEventBus) {
        modEventBus.addListener((Consumer<FMLClientSetupEvent>) event -> event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(CampingBlocks.GRILL, RenderType.cutout());
            for (Block block : CampingBlocks.TENTS.values()) {
                ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
            }
            if (Services.PLATFORM.isModLoaded("curios")) NeoForgeCuriosHelper.registerRenderersForCurios();
            CampingClient.registerInfoProviders();
        }));

        modEventBus.addListener((Consumer<EntityRenderersEvent.RegisterRenderers>) event -> {
            event.registerBlockEntityRenderer(CampingBlockEntities.GRILL, GrillRenderer::new);
            event.registerBlockEntityRenderer(CampingBlockEntities.TENT, TentRenderer::new);
            event.registerEntityRenderer(CampingEntities.MOSQUITO_SWARM, MosquitoSwarmRenderer::new);
        });

        modEventBus.addListener((Consumer<RegisterParticleProvidersEvent>) event -> {
            event.registerSpriteSet(CampingParticles.ROTATION_ARROW, RotationArrowParticle.Provider::new);
        });

        modEventBus.addListener((Consumer<RegisterMenuScreensEvent>) event -> {
            CampingScreenHandlers.BACKPACKS.forEach(type -> event.register(type, BackpackScreen::new));
        });

        modEventBus.addListener((Consumer<EntityRenderersEvent.RegisterLayerDefinitions>) event -> {
            event.registerLayerDefinition(EnderpackModel.LAYER_LOCATION, EnderpackModel::createBodyLayer);
            event.registerLayerDefinition(EnderbagModel.LAYER_LOCATION, EnderbagModel::createBodyLayer);
            event.registerLayerDefinition(GoodybagModel.LAYER_LOCATION, GoodybagModel::createBodyLayer);
            event.registerLayerDefinition(LargeBackpackModel.LAYER_LOCATION, LargeBackpackModel::createBodyLayer);
            event.registerLayerDefinition(SheepbagModel.LAYER_LOCATION, SheepbagModel::createBodyLayer);
            event.registerLayerDefinition(SmallBackpackModel.LAYER_LOCATION, SmallBackpackModel::createBodyLayer);
            event.registerLayerDefinition(WandererBackpackModel.LAYER_LOCATION, WandererBackpackModel::createBodyLayer);
            event.registerLayerDefinition(WandererBagModel.LAYER_LOCATION, WandererBagModel::createBodyLayer);
        });

        modEventBus.addListener((Consumer<EntityRenderersEvent.AddLayers>) event -> {
            PlayerRenderer wide = event.getSkin(PlayerSkin.Model.WIDE);
            if (wide != null) {
                wide.addLayer(new EnderpackRenderLayer(wide));
                wide.addLayer(new BackpackRenderLayer(wide));
            }
            PlayerRenderer slim = event.getSkin(PlayerSkin.Model.SLIM);
            if (slim != null) {
                slim.addLayer(new EnderpackRenderLayer(slim));
                slim.addLayer(new BackpackRenderLayer(slim));
            }
        });
    }
}
