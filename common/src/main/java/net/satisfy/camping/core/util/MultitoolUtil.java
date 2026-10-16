package net.satisfy.camping.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.satisfy.camping.core.registry.CampingParticles;

public class MultitoolUtil {

    public static void rotateBlock(Level level, BlockPos pos, BlockState state, boolean shiftKeyDown) {
        Direction newDirection;

        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            newDirection = shiftKeyDown ? facing.getCounterClockWise() : facing.getClockWise();
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, newDirection);
        } else if (state.hasProperty(BlockStateProperties.FACING)) {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            if (facing.getAxis().isVertical()) return;
            newDirection = shiftKeyDown ? facing.getCounterClockWise() : facing.getClockWise();
            state = state.setValue(BlockStateProperties.FACING, newDirection);
        } else {
            return;
        }

        Direction oldDirection = shiftKeyDown ? newDirection.getClockWise() : newDirection.getCounterClockWise();
        level.setBlock(pos, state, Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.6F, 1.4F + level.random.nextFloat() * 0.3F);
        if (level instanceof ServerLevel serverLevel) {
            spawnRotationArrow(serverLevel, pos, oldDirection, !shiftKeyDown);
            spawnFrontParticles(serverLevel, pos, state, newDirection);
        }
    }

    private static void spawnRotationArrow(ServerLevel level, BlockPos pos, Direction from, boolean clockwise) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;
        double radius = 0.8;
        double start = Math.atan2(from.getStepZ(), from.getStepX());
        double sweep = clockwise ? Math.PI / 2 : -Math.PI / 2;
        level.sendParticles(CampingParticles.ROTATION_ARROW, cx, cy, cz, 0, start, sweep, radius, 1.0);
    }

    private static void spawnFrontParticles(ServerLevel level, BlockPos pos, BlockState state, Direction facing) {
        double x = pos.getX() + 0.5 + 0.55 * facing.getStepX();
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5 + 0.55 * facing.getStepZ();
        double spreadX = facing.getStepX() == 0 ? 0.25 : 0.02;
        double spreadZ = facing.getStepZ() == 0 ? 0.25 : 0.02;
        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), x, y, z, 8, spreadX, 0.25, spreadZ, 0.05);
    }
}
