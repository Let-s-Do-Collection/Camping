package net.satisfy.camping.core.world.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.camping.core.util.CampingUtil;
import net.satisfy.camping.core.world.block.entity.TentBlockEntity;
import net.satisfy.camping.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockNotice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class TentBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<TentBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            propertiesCodec(),
            DyeColor.CODEC.fieldOf("color").forGetter(TentBlock::getColor)
    ).apply(instance, TentBlock::new));

    public static final EnumProperty<TentPart> PART = EnumProperty.create("part", TentPart.class);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    private static final Map<TentPart, Map<DoubleBlockHalf, Map<Direction, VoxelShape>>> SHAPES = Util.make(new EnumMap<>(TentPart.class), map -> {
        map.put(TentPart.MAIN, shapes(
                new double[][]{{0.8125, 0, 0, 1, 1, 0.1875}, {0.8125, 0, 0.1875, 0.875, 1, 1}, {0.875, 0, 0.9375, 0.9375, 1, 1}},
                new double[][]{{0.8125, 0, 0, 1, 0.8125, 0.1875}, {0.8125, 0, 0.1875, 0.875, 0.8125, 1}, {0, 0.8125, 0, 1, 1, 0.1875}, {0.8125, 0.8125, 0.1875, 1, 1, 1}, {0.125, 0.8125, 0.1875, 0.8125, 0.875, 1}, {0, 0.8125, 0.1875, 0.125, 0.9375, 1}, {0.875, 0, 0.9375, 0.9375, 0.8125, 1}}));
        map.put(TentPart.MAIN_HEAD, shapes(
                new double[][]{{0.8125, 0, 0.8125, 1, 1, 1}, {0.8125, 0, 0, 0.875, 1, 0.8125}, {0, 0, 0.8125, 0.8125, 1, 0.875}, {0.875, 0, 0, 0.9375, 1, 0.0625}},
                new double[][]{{0.8125, 0, 0.8125, 1, 0.8125, 1}, {0.8125, 0, 0, 0.875, 0.8125, 0.8125}, {0, 0, 0.8125, 0.8125, 0.8125, 0.875}, {0, 0.8125, 0.8125, 1, 1, 1}, {0.8125, 0.8125, 0, 1, 1, 0.8125}, {0.875, 0, 0, 0.9375, 0.8125, 0.0625}, {0, 0.8125, 0, 0.125, 0.9375, 0.8125}, {0.125, 0.8125, 0, 0.8125, 0.875, 0.8125}}));
        map.put(TentPart.RIGHT, shapes(
                new double[][]{{0, 0, 0, 0.1875, 1, 0.1875}, {0.125, 0, 0.1875, 0.1875, 1, 1}, {0.0625, 0, 0.9375, 0.125, 1, 1}},
                new double[][]{{0, 0, 0, 0.1875, 0.8125, 0.1875}, {0.125, 0, 0.1875, 0.1875, 0.8125, 1}, {0, 0.8125, 0, 1, 1, 0.1875}, {0, 0.8125, 0.1875, 0.1875, 1, 1}, {0.1875, 0.8125, 0.1875, 0.875, 0.875, 1}, {0.875, 0.8125, 0.1875, 1, 0.9375, 1}, {0.0625, 0, 0.9375, 0.125, 0.8125, 1}}));
        map.put(TentPart.RIGHT_HEAD, shapes(
                new double[][]{{0, 0, 0.8125, 0.1875, 1, 1}, {0.125, 0, 0, 0.1875, 1, 0.8125}, {0.1875, 0, 0.8125, 1, 1, 0.875}, {0.0625, 0, 0, 0.125, 1, 0.0625}},
                new double[][]{{0, 0, 0.8125, 0.1875, 0.8125, 1}, {0.125, 0, 0, 0.1875, 0.8125, 0.8125}, {0.1875, 0, 0.8125, 1, 0.8125, 0.875}, {0, 0.8125, 0.8125, 1, 1, 1}, {0, 0.8125, 0, 0.1875, 1, 0.8125}, {0.0625, 0, 0, 0.125, 0.8125, 0.0625}, {0.875, 0.8125, 0, 1, 0.9375, 0.8125}, {0.1875, 0.8125, 0, 0.875, 0.875, 0.8125}}));
    });

    private static final double SLEEPING_BAG_HEIGHT = 0.25;
    private static final Map<Direction, Map<TentPart, VoxelShape>> BAG_SHAPES = Util.make(new EnumMap<>(Direction.class), map -> {
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            Vec3 offset = getBagOffset(facing);
            BlockPos head = TentPart.MAIN_HEAD.fromMain(BlockPos.ZERO, facing);
            VoxelShape bag = Shapes.or(
                    Shapes.box(offset.x, 0, offset.z, 1 + offset.x, SLEEPING_BAG_HEIGHT, 1 + offset.z),
                    Shapes.box(head.getX() + offset.x, 0, head.getZ() + offset.z, head.getX() + 1 + offset.x, SLEEPING_BAG_HEIGHT, head.getZ() + 1 + offset.z));
            Map<TentPart, VoxelShape> parts = new EnumMap<>(TentPart.class);
            for (TentPart part : TentPart.values()) {
                BlockPos partOffset = part.fromMain(BlockPos.ZERO, facing);
                parts.put(part, Shapes.join(bag.move(-partOffset.getX(), 0, -partOffset.getZ()), Shapes.block(), BooleanOp.AND));
            }
            map.put(facing, parts);
        }
    });
    private final DyeColor color;

    public TentBlock(Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, TentPart.MAIN)
                .setValue(HALF, DoubleBlockHalf.LOWER));
    }

    private static Map<DoubleBlockHalf, Map<Direction, VoxelShape>> shapes(double[][] lower, double[][] upper) {
        Map<DoubleBlockHalf, Map<Direction, VoxelShape>> map = new EnumMap<>(DoubleBlockHalf.class);
        map.put(DoubleBlockHalf.LOWER, rotated(lower));
        map.put(DoubleBlockHalf.UPPER, rotated(upper));
        return map;
    }

    private static Map<Direction, VoxelShape> rotated(double[][] boxes) {
        VoxelShape shape = Shapes.empty();
        for (double[] box : boxes) {
            shape = Shapes.or(shape, Shapes.box(box[0], box[1], box[2], box[3], box[4], box[5]));
        }
        Map<Direction, VoxelShape> map = new HashMap<>();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, CampingUtil.rotateShape(Direction.NORTH, direction, shape));
        }
        return map;
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public DyeColor getColor() {
        return this.color;
    }

    public static BlockPos getMainPos(BlockState state, BlockPos pos) {
        BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        return state.getValue(PART).toMain(lowerPos, state.getValue(FACING));
    }

    private static BlockPos partPos(BlockPos mainPos, Direction facing, TentPart part, DoubleBlockHalf half) {
        BlockPos pos = part.fromMain(mainPos, facing);
        return half == DoubleBlockHalf.UPPER ? pos.above() : pos;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos mainPos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection().getOpposite();
        for (TentPart part : TentPart.values()) {
            BlockPos lowerPos = part.fromMain(mainPos, facing);
            BlockPos upperPos = lowerPos.above();
            if (upperPos.getY() >= level.getMaxBuildHeight()) {
                return null;
            }
            if (!level.getBlockState(lowerPos).canBeReplaced(context) || !level.getBlockState(upperPos).canBeReplaced(context)) {
                return null;
            }
            if (!level.getBlockState(lowerPos.below()).isFaceSturdy(level, lowerPos.below(), Direction.UP)) {
                return null;
            }
        }
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide) {
            return;
        }
        Direction facing = state.getValue(FACING);
        for (TentPart part : TentPart.values()) {
            for (DoubleBlockHalf half : DoubleBlockHalf.values()) {
                if (part == TentPart.MAIN && half == DoubleBlockHalf.LOWER) {
                    continue;
                }
                level.setBlock(partPos(pos, facing, part, half), state.setValue(PART, part).setValue(HALF, half), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return true;
        }
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        BlockPos mainPos = getMainPos(state, pos);
        Direction facing = state.getValue(FACING);
        for (TentPart part : TentPart.values()) {
            for (DoubleBlockHalf half : DoubleBlockHalf.values()) {
                if (!partPos(mainPos, facing, part, half).equals(neighborPos)) {
                    continue;
                }
                boolean intact = neighborState.is(this)
                        && neighborState.getValue(FACING) == facing
                        && neighborState.getValue(PART) == part
                        && neighborState.getValue(HALF) == half;
                return intact ? state : Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            BlockPos mainPos = getMainPos(state, pos);
            Direction facing = state.getValue(FACING);
            boolean isMain = state.getValue(PART) == TentPart.MAIN && state.getValue(HALF) == DoubleBlockHalf.LOWER;
            if (!isMain && !player.isCreative() && player.hasCorrectToolForDrops(state)) {
                popResource(level, mainPos, new ItemStack(this));
            }
            for (TentPart part : TentPart.values()) {
                for (DoubleBlockHalf half : DoubleBlockHalf.values()) {
                    BlockPos partPos = partPos(mainPos, facing, part, half);
                    if (partPos.equals(pos)) {
                        continue;
                    }
                    BlockState partState = level.getBlockState(partPos);
                    if (partState.is(this)) {
                        level.setBlock(partPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                        level.levelEvent(player, 2001, partPos, Block.getId(partState));
                    }
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!TentBlockEntity.isSleepingBag(stack) || player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!(level.getBlockEntity(getMainPos(state, pos)) instanceof TentBlockEntity tent)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (tent.hasBag()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            tent.setBag(stack.copyWithCount(1));
            notice(player, pos, Component.translatable("hud.camping.tent.bag_added", stack.getHoverName()));
            stack.consume(1, player);
            level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown()) {
            packUp(state, level, pos, player);
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(getMainPos(state, pos)) instanceof TentBlockEntity tent) || !tent.hasBag()) {
            notice(player, pos, Component.translatable("hud.camping.tent.needs_sleeping_bag").withStyle(ChatFormatting.GOLD));
            return InteractionResult.SUCCESS;
        }
        BlockPos sleepPos = getSleepPos(state, pos);
        if (isOccupied(level, sleepPos)) {
            notice(player, pos, Component.translatable("block.minecraft.bed.occupied"));
            return InteractionResult.SUCCESS;
        }
        player.startSleepInBed(sleepPos).ifLeft(problem -> {
            if (problem.getMessage() != null) {
                notice(player, pos, problem.getMessage());
            }
        });
        return InteractionResult.SUCCESS;
    }

    private void packUp(BlockState state, Level level, BlockPos pos, Player player) {
        BlockPos mainPos = getMainPos(state, pos);
        Direction facing = state.getValue(FACING);
        if (level.getBlockEntity(mainPos) instanceof TentBlockEntity tent) {
            giveOrDrop(player, tent.takeBag());
        }
        for (TentPart part : TentPart.values()) {
            for (DoubleBlockHalf half : DoubleBlockHalf.values()) {
                BlockPos partPos = partPos(mainPos, facing, part, half);
                if (level.getBlockState(partPos).is(this)) {
                    level.setBlock(partPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                }
            }
        }
        level.playSound(null, mainPos, this.soundType.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
        notice(player, mainPos, Component.translatable("hud.camping.tent.packed_up"));
        if (!player.isCreative()) {
            giveOrDrop(player, new ItemStack(this));
        }
    }

    private static void notice(Player player, BlockPos pos, Component message) {
        if (player instanceof ServerPlayer serverPlayer) {
            BlockNotice.send(serverPlayer, pos, message);
        }
    }

    private static void giveOrDrop(Player player, ItemStack stack) {
        if (!stack.isEmpty() && !player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof TentBlockEntity tent) {
            popResource(level, pos, tent.getBag());
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public static final double BAG_FRONT_OFFSET = 3.0 / 16.0;

    public static BlockPos getSleepPos(BlockState state, BlockPos pos) {
        return TentPart.MAIN_HEAD.fromMain(getMainPos(state, pos), state.getValue(FACING));
    }

    public static Vec3 getBagOffset(Direction facing) {
        Direction side = facing.getCounterClockWise();
        return new Vec3(side.getStepX() * 0.5 + facing.getStepX() * BAG_FRONT_OFFSET, 0.0, side.getStepZ() * 0.5 + facing.getStepZ() * BAG_FRONT_OFFSET);
    }

    public static Direction getSleepDirection(BlockState state) {
        return state.getValue(FACING).getOpposite();
    }

    private static boolean isOccupied(Level level, BlockPos sleepPos) {
        return !level.getEntitiesOfClass(LivingEntity.class, new AABB(sleepPos), entity -> entity.getSleepingPos().map(sleepPos::equals).orElse(false)).isEmpty();
    }

    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, @Nullable LivingEntity sleeper) {
        return true;
    }

    public void setBedOccupied(BlockState state, Level level, BlockPos pos, @Nullable LivingEntity sleeper, boolean occupied) {
    }

    public Direction getBedDirection(BlockState state, LevelReader level, BlockPos pos) {
        return getSleepDirection(state);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && entity instanceof Monster && PlatformHelper.isMonsterTentGriefingEnabled() && level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            BlockPos mainPos = getMainPos(state, pos);
            if (level.getBlockState(mainPos).is(this)) {
                level.destroyBlock(mainPos, true, entity);
            }
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(PART) == TentPart.MAIN && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new TentBlockEntity(pos, state);
        }
        return null;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(this);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = SHAPES.get(state.getValue(PART)).get(state.getValue(HALF)).get(state.getValue(FACING));
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER && level.getBlockEntity(getMainPos(state, pos)) instanceof TentBlockEntity tent && tent.hasBag()) {
            return Shapes.or(shape, BAG_SHAPES.get(state.getValue(FACING)).get(state.getValue(PART)));
        }
        return shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, HALF);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.camping.canbeplaced").withStyle(ChatFormatting.GRAY));
    }

    public enum TentPart implements StringRepresentable {
        MAIN("main"),
        MAIN_HEAD("main_head"),
        RIGHT("right"),
        RIGHT_HEAD("right_head");

        private final String name;

        TentPart(String name) {
            this.name = name;
        }

        public BlockPos fromMain(BlockPos mainPos, Direction facing) {
            return switch (this) {
                case MAIN -> mainPos;
                case MAIN_HEAD -> mainPos.relative(facing.getOpposite());
                case RIGHT -> mainPos.relative(facing.getCounterClockWise());
                case RIGHT_HEAD -> mainPos.relative(facing.getCounterClockWise()).relative(facing.getOpposite());
            };
        }

        public BlockPos toMain(BlockPos partPos, Direction facing) {
            return switch (this) {
                case MAIN -> partPos;
                case MAIN_HEAD -> partPos.relative(facing);
                case RIGHT -> partPos.relative(facing.getClockWise());
                case RIGHT_HEAD -> partPos.relative(facing).relative(facing.getClockWise());
            };
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
