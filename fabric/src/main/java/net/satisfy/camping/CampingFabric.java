package net.satisfy.camping;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.satisfy.camping.core.registry.CampingEntities;
import net.satisfy.camping.core.world.entity.MosquitoSwarmEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.camping.core.config.CampingFabricConfig;
import net.satisfy.camping.core.network.FabricCampingNetwork;
import net.satisfy.camping.core.registry.RegistryFabric;
import net.satisfy.camping.core.util.MosquitoUtil;
import net.satisfy.camping.core.world.block.SleepingBagBlock;
import net.satisfy.camping.core.world.block.SleepingBagBackpackBlock;
import net.satisfy.camping.core.world.block.TentBlock;
import net.satisfy.camping.optional.trinkets.TrinketsHelper;
import net.satisfy.camping.platform.PlatformHelper;
import net.satisfy.camping.platform.Services;

public class CampingFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AutoConfig.register(CampingFabricConfig.class, GsonConfigSerializer::new);
        Camping.init();
        RegistryFabric.register();
        Camping.registerRarities();
        FabricCampingNetwork.registerCommon();
        FabricCampingNetwork.registerServer();

        if (Services.PLATFORM.isModLoaded("trinkets")) {
            TrinketsHelper.registerItemsAsTrinkets();
        }

        FabricDefaultAttributeRegistry.register(CampingEntities.MOSQUITO_SWARM, MosquitoSwarmEntity.createAttributes());
        ServerTickEvents.END_WORLD_TICK.register(level -> level.players().forEach(MosquitoUtil::tickPlayer));

        EntitySleepEvents.ALLOW_SETTING_SPAWN.register(CampingFabric::onPlayerSetSpawn);
        EntitySleepEvents.ALLOW_BED.register((entity, sleepingPos, state, vanillaResult) -> state.getBlock() instanceof TentBlock ? InteractionResult.SUCCESS : InteractionResult.PASS);
        EntitySleepEvents.SET_BED_OCCUPATION_STATE.register((entity, sleepingPos, state, occupied) -> state.getBlock() instanceof TentBlock);
        EntitySleepEvents.MODIFY_SLEEPING_DIRECTION.register((entity, sleepingPos, direction) -> {
            BlockState state = entity.level().getBlockState(sleepingPos);
            return state.getBlock() instanceof TentBlock ? TentBlock.getSleepDirection(state) : direction;
        });
        EntitySleepEvents.MODIFY_WAKE_UP_POSITION.register((entity, sleepingPos, state, wakeUpPos) -> {
            if (wakeUpPos == null && state.getBlock() instanceof TentBlock) {
                return Vec3.atBottomCenterOf(sleepingPos).add(0, 0.1, 0);
            }
            return wakeUpPos;
        });
        EntitySleepEvents.ALLOW_NEARBY_MONSTERS.register((player, sleepingPos, vanillaResult) -> {
            if (PlatformHelper.isTentSleepNearMonstersEnabled() && player.level().getBlockState(sleepingPos).getBlock() instanceof TentBlock) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });
    }

    public static boolean onPlayerSetSpawn(Player player, BlockPos pos) {
        BlockState state = player.level().getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof TentBlock || SleepingBagBackpackBlock.isBackpackBag(state)) {
            return false;
        }
        return PlatformHelper.isSleepingBagSpawnEnabled() || !(block instanceof SleepingBagBlock);
    }
}
