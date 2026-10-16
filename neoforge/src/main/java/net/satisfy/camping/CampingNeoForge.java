package net.satisfy.camping;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.camping.core.util.MosquitoUtil;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.satisfy.camping.core.registry.CampingEntities;
import net.satisfy.camping.core.world.entity.MosquitoSwarmEntity;
import net.satisfy.camping.core.world.block.SleepingBagBackpackBlock;
import net.satisfy.camping.core.world.block.TentBlock;
import net.satisfy.camping.core.world.block.entity.TentBlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.satisfy.camping.client.keys.NeoForgeOpenBackpackKey;
import net.satisfy.camping.core.config.CampingNeoForgeConfig;
import net.satisfy.camping.core.network.NeoForgeCampingNetwork;
import net.satisfy.camping.core.registry.CampingItems;
import net.satisfy.camping.core.registry.NeoForgeRegistry;
import net.satisfy.camping.core.util.GrillingUtil;
import net.satisfy.camping.core.world.block.SleepingBagBlock;
import net.satisfy.camping.platform.PlatformHelper;

import java.util.Objects;

@Mod(Constants.MOD_ID)
public class CampingNeoForge {

    public CampingNeoForge(ModContainer modContainer) {
        IEventBus bus = Objects.requireNonNull(modContainer.getEventBus());
        modContainer.registerConfig(ModConfig.Type.COMMON, CampingNeoForgeConfig.COMMON_CONFIG);
        bus.addListener(CampingNeoForgeConfig::onLoad);
        bus.addListener(CampingNeoForgeConfig::onReload);
        bus.addListener(CampingNeoForge::reapplyFoodConfig);
        Camping.init();
        NeoForgeRegistry.register(bus);
        NeoForgeCampingNetwork.register(bus);
        bus.addListener(CampingNeoForge::onCommonSetup);
        bus.addListener(CampingNeoForge::onEntityAttributes);
        NeoForge.EVENT_BUS.addListener(CampingNeoForge::onItemTooltip);
        NeoForge.EVENT_BUS.addListener(CampingNeoForge::onPlayerSetSpawn);
        NeoForge.EVENT_BUS.addListener(CampingNeoForge::onCanPlayerSleep);
        NeoForge.EVENT_BUS.addListener(CampingNeoForge::onSpawnPositionCheck);
        NeoForge.EVENT_BUS.addListener(CampingNeoForge::onPlayerTick);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            new CampingClientNeoForge(bus);
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
            bus.addListener(CampingNeoForge::onClientSetup);
            bus.addListener(NeoForgeOpenBackpackKey::onRegisterKeyMappings);
        }
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(Camping::registerRarities);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(NeoForgeOpenBackpackKey::registerInputHandler);
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        GrillingUtil.addGrilledTooltip(stack, event.getToolTip());
    }

    private static void onPlayerSetSpawn(PlayerSetSpawnEvent event) {
        if (event.getNewSpawn() == null) return;
        BlockState state = event.getEntity().level().getBlockState(event.getNewSpawn());
        Block block = state.getBlock();
        if (block instanceof TentBlock || SleepingBagBackpackBlock.isBackpackBag(state) || (block instanceof SleepingBagBlock && !PlatformHelper.isSleepingBagSpawnEnabled())) {
            event.setCanceled(true);
        }
    }

    private static void onCanPlayerSleep(CanPlayerSleepEvent event) {
        if (event.getProblem() == Player.BedSleepingProblem.NOT_SAFE && PlatformHelper.isTentSleepNearMonstersEnabled() && event.getState().getBlock() instanceof TentBlock) {
            event.setProblem(null);
        }
    }

    private static void onEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(CampingEntities.MOSQUITO_SWARM, MosquitoSwarmEntity.createAttributes().build());
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) MosquitoUtil.tickPlayer(player);
    }

    private static void onSpawnPositionCheck(MobSpawnEvent.PositionCheck event) {
        if (event.getSpawnType() != MobSpawnType.NATURAL || event.getEntity().getType().getCategory() != MobCategory.MONSTER) return;
        if (event.getLevel() instanceof ServerLevel level && TentBlockEntity.isNearTent(level, event.getEntity().blockPosition(), PlatformHelper.getTentSafeRange())) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        }
    }

    private static void reapplyFoodConfig(ModifyDefaultComponentsEvent event) {
        patchFood(event, CampingItems.MARSHMALLOW, "marshmallow");
        patchFood(event, CampingItems.ROASTED_MARSHMALLOW, "roasted_marshmallow");
    }

    private static void patchFood(ModifyDefaultComponentsEvent event, Item item, String key) {
        FoodProperties current = item.components().get(DataComponents.FOOD);
        if (current == null) return;

        int duration = PlatformHelper.getEffectDuration(key);
        FoodProperties updated = new FoodProperties(
                PlatformHelper.getNutrition(key),
                PlatformHelper.getNutrition(key) * PlatformHelper.getSaturation(key) * 2.0F,
                current.canAlwaysEat(),
                current.eatSeconds(),
                current.usingConvertsTo(),
                current.effects().stream()
                        .map(e -> new FoodProperties.PossibleEffect(() -> new MobEffectInstance(e.effect().getEffect(), duration, e.effect().getAmplifier()), e.probability()))
                        .toList()
        );
        event.modify(item, builder -> builder.set(DataComponents.FOOD, updated));
    }
}
