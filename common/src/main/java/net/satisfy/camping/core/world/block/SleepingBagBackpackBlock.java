package net.satisfy.camping.core.world.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.camping.core.registry.CampingBlocks;
import net.satisfy.camping.core.util.BackpackVariant;
import net.satisfy.camping.core.util.CampingUtil;
import net.satisfy.foundation.overlay.BlockNotice;

import java.util.HashMap;
import java.util.Map;

public class SleepingBagBackpackBlock extends BackpackBlock {
    public static final BooleanProperty UNROLLED = BooleanProperty.create("unrolled");
    public static final Map<BackpackVariant, Map<Direction, VoxelShape>> UNROLLED_SHAPES = new HashMap<>();
    private static final double ROLL_HIT_MARGIN = 0.5 / 16.0;

    private final double rollHeight;
    private final MapCodec<SleepingBagBackpackBlock> codec;

    public SleepingBagBackpackBlock(Properties properties, BackpackVariant variant, double rollHeight) {
        super(properties, variant);
        this.rollHeight = rollHeight;
        this.codec = simpleCodec(p -> new SleepingBagBackpackBlock(p, variant, rollHeight));
        this.registerDefaultState(this.defaultBlockState().setValue(UNROLLED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(UNROLLED)) {
            return UNROLLED_SHAPES.get(this.getVariant()).get(state.getValue(FACING));
        }
        return super.getShape(state, world, pos, context);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return this.codec;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UNROLLED);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown() || player.isSpectator() || hit.getLocation().y - pos.getY() < this.rollHeight + ROLL_HIT_MARGIN) {
            return super.useWithoutItem(state, level, pos, player, hit);
        }
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (state.getValue(UNROLLED)) {
            rollUp(state, level, pos);
            level.setBlock(pos, state.setValue(UNROLLED, false), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            notice(player, pos, Component.translatable("hud.camping.backpack.rolled_up"));
            return InteractionResult.SUCCESS;
        }
        Direction facing = state.getValue(FACING);
        BlockPos headPos = pos.relative(facing);
        BlockPos footPos = headPos.relative(facing);
        if (!canUnrollAt(level, headPos) || !canUnrollAt(level, footPos)) {
            notice(player, pos, Component.translatable("hud.camping.backpack.not_enough_space").withStyle(ChatFormatting.GOLD));
            return InteractionResult.SUCCESS;
        }
        BlockState bag = CampingBlocks.SLEEPING_BAGS.get("red").defaultBlockState().setValue(BedBlock.FACING, facing.getOpposite()).setValue(SleepingBagBlock.CAN_DROP, false);
        level.setBlock(footPos, bag, Block.UPDATE_ALL);
        level.setBlock(headPos, bag.setValue(BedBlock.PART, BedPart.HEAD), Block.UPDATE_ALL);
        level.setBlock(pos, state.setValue(UNROLLED, true), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        notice(player, pos, Component.translatable("hud.camping.backpack.unrolled"));
        return InteractionResult.SUCCESS;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        if (direction == state.getValue(FACING) && state.getValue(UNROLLED) && !isOwnBag(neighborState, state.getValue(FACING))) {
            return updated.setValue(UNROLLED, false);
        }
        return updated;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && state.getValue(UNROLLED)) {
            rollUp(state, level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    public static boolean isBackpackBag(BlockState state) {
        return state.getBlock() instanceof SleepingBagBlock && !state.getValue(SleepingBagBlock.CAN_DROP);
    }

    private static boolean isOwnBag(BlockState state, Direction backpackFacing) {
        return isBackpackBag(state) && state.getValue(BedBlock.PART) == BedPart.HEAD && state.getValue(BedBlock.FACING) == backpackFacing.getOpposite();
    }

    private static void rollUp(BlockState state, Level level, BlockPos pos) {
        BlockPos headPos = pos.relative(state.getValue(FACING));
        if (isOwnBag(level.getBlockState(headPos), state.getValue(FACING))) {
            level.setBlock(headPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
        }
    }

    private static boolean canUnrollAt(Level level, BlockPos pos) {
        return level.getBlockState(pos).canBeReplaced() && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    private static void notice(Player player, BlockPos pos, Component message) {
        if (player instanceof ServerPlayer serverPlayer) {
            BlockNotice.send(serverPlayer, pos, message);
        }
    }

    private static Map<Direction, VoxelShape> rotated(VoxelShape shape) {
        Map<Direction, VoxelShape> map = new HashMap<>();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            map.put(dir, CampingUtil.rotateShape(Direction.NORTH, dir, shape));
        }
        return map;
    }

    static {
        UNROLLED_SHAPES.put(BackpackVariant.LARGE_BACKPACK, rotated(BackpackBlockShapes.LARGE_BACKPACK_UNROLLED.get()));
        UNROLLED_SHAPES.put(BackpackVariant.WANDERER_BACKPACK, rotated(BackpackBlockShapes.WANDERER_BACKPACK_UNROLLED.get()));
    }
}
