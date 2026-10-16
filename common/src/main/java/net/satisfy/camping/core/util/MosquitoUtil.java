package net.satisfy.camping.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.satisfy.camping.core.registry.CampingEffects;
import net.satisfy.camping.core.registry.CampingEntities;
import net.satisfy.camping.core.registry.CampingTags;
import net.satisfy.camping.core.world.entity.MosquitoSwarmEntity;
import net.satisfy.camping.platform.PlatformHelper;

public class MosquitoUtil {
    private static final int SPAWN_INTERVAL = 200;
    private static final int SPAWN_ATTEMPTS = 8;
    private static final int MIN_SPAWN_DISTANCE = 8;
    private static final int MAX_SPAWN_DISTANCE = 24;
    private static final double SWARM_CHECK_RANGE = 32.0;
    private static final int MIN_BLOCK_LIGHT = 7;
    private static final int CAMPFIRE_RANGE = 6;

    public static void tickPlayer(ServerPlayer player) {
        if (player.tickCount % SPAWN_INTERVAL != 0 || !PlatformHelper.isMosquitoesEnabled() || player.isSpectator()) return;
        ServerLevel level = player.serverLevel();
        if (!level.dimensionType().hasSkyLight() || !level.isNight()) return;
        RandomSource random = player.getRandom();
        if (random.nextDouble() >= PlatformHelper.getMosquitoChance()) return;
        AABB area = player.getBoundingBox().inflate(SWARM_CHECK_RANGE);
        if (level.getEntitiesOfClass(MosquitoSwarmEntity.class, area).size() >= PlatformHelper.getMosquitoMaxPerPlayer()) return;

        for (int i = 0; i < SPAWN_ATTEMPTS; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            int distance = MIN_SPAWN_DISTANCE + random.nextInt(MAX_SPAWN_DISTANCE - MIN_SPAWN_DISTANCE + 1);
            int x = player.getBlockX() + (int) Math.round(Math.cos(angle) * distance);
            int z = player.getBlockZ() + (int) Math.round(Math.sin(angle) * distance);
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(x, 0, z));
            if (isSwarmSpot(level, surface)) {
                spawnSwarm(level, surface, random);
                return;
            }
        }
    }

    private static boolean isSwarmSpot(ServerLevel level, BlockPos pos) {
        return level.getFluidState(pos.below()).is(FluidTags.WATER)
                && level.getBlockState(pos).isAir()
                && level.getBlockState(pos.above()).isAir()
                && level.getBiome(pos).is(CampingTags.MOSQUITO_HABITAT)
                && !isNearLitCampfire(level, pos);
    }

    private static void spawnSwarm(ServerLevel level, BlockPos pos, RandomSource random) {
        MosquitoSwarmEntity swarm = CampingEntities.MOSQUITO_SWARM.create(level);
        if (swarm == null) return;
        swarm.moveTo(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, random.nextFloat() * 360.0F, 0.0F);
        swarm.setHome(pos);
        swarm.setMosquitoes(MosquitoSwarmEntity.MIN_MOSQUITOES + random.nextInt(MosquitoSwarmEntity.MAX_MOSQUITOES - MosquitoSwarmEntity.MIN_MOSQUITOES + 1));
        swarm.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null);
        level.addFreshEntity(swarm);
    }

    public static boolean canBother(ServerLevel level, Player player) {
        if (player.isSpectator() || player.isCreative() || player.isSleeping()) return false;
        if (player.hasEffect(CampingEffects.repellent())) return false;
        BlockPos pos = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        if (level.getBrightness(LightLayer.BLOCK, pos) < MIN_BLOCK_LIGHT) return false;
        return !isNearLitCampfire(level, player.blockPosition());
    }

    private static boolean isNearLitCampfire(ServerLevel level, BlockPos center) {
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-CAMPFIRE_RANGE, -2, -CAMPFIRE_RANGE), center.offset(CAMPFIRE_RANGE, 2, CAMPFIRE_RANGE))) {
            BlockState state = level.getBlockState(pos);
            if (state.is(BlockTags.CAMPFIRES) && CampfireBlock.isLitCampfire(state)) return true;
        }
        return false;
    }
}
