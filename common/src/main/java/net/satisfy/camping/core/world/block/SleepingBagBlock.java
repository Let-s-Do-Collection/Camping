package net.satisfy.camping.core.world.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.camping.core.util.CampingUtil;
import net.satisfy.camping.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockNotice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@ParametersAreNullableByDefault
public class SleepingBagBlock extends BedBlock {

    public static final double SLEEPING_HEIGHT = 0.375;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    public static final BooleanProperty CAN_DROP = BlockStateProperties.CONDITIONAL;
    protected static final VoxelShape SLEEPING_BAG_SHAPE = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.25, 1.0D);

    public SleepingBagBlock(DyeColor dyeColor) {
        super(dyeColor, BlockBehaviour.Properties.ofFullCopy(Blocks.RED_WOOL).pushReaction(PushReaction.IGNORE).instabreak().mapColor(DyeColor.WHITE).forceSolidOn());
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, BedPart.FOOT).setValue(OCCUPIED, Boolean.FALSE).setValue(CAN_DROP, Boolean.TRUE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OCCUPIED, CAN_DROP);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return state.getValue(PART) == BedPart.HEAD ? CampingUtil.rotateShape(Direction.NORTH, state.getValue(FACING), SLEEPING_BAG_SHAPE) : SLEEPING_BAG_SHAPE;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    private static Direction getNeighbourDirection(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor accessor, BlockPos pos, BlockPos newPos) {
        if (direction == getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
            return newState.is(this) && newState.getValue(PART) != state.getValue(PART) ? state.setValue(OCCUPIED, newState.getValue(OCCUPIED)) : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(state, direction, newState, accessor, pos, newPos);
        }
    }

    private static void bounceUp(Entity entity) {
        Vec3 v = entity.getDeltaMovement();
        if (v.y < 0.0F) {
            double m = entity instanceof LivingEntity ? 1.0F : 0.8;
            entity.setDeltaMovement(v.x, -v.y * 0.66F * m, v.z);
        }
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter getter, Entity entity) {
        if (entity.isSuppressingBounce()) super.updateEntityAfterFallOn(getter, entity);
        else bounceUp(entity);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction dir = context.getHorizontalDirection();
        BlockPos pos = context.getClickedPos();
        BlockPos head = pos.relative(dir);
        return context.getLevel().getBlockState(head).canBeReplaced(context) ? this.defaultBlockState().setValue(FACING, dir) : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity living, ItemStack stack) {
        super.setPlacedBy(level, pos, state, living, stack);
        if (!level.isClientSide) {
            BlockPos headPos = pos.relative(state.getValue(FACING));
            level.setBlock(headPos, state.setValue(PART, BedPart.HEAD), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public long getSeed(BlockState state, BlockPos pos) {
        BlockPos p = pos.relative(state.getValue(FACING), state.getValue(PART) == BedPart.HEAD ? 0 : 1);
        return Mth.getSeed(p.getX(), pos.getY(), p.getZ());
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (!state.getValue(CAN_DROP)) {
            return List.of();
        }
        return super.getDrops(state, builder);
    }

    private boolean kickVillagerOutOfBed(Level level, BlockPos pos) {
        List<Villager> villagers = level.getEntitiesOfClass(Villager.class, new AABB(pos), LivingEntity::isSleeping);
        if (villagers.isEmpty()) return false;
        villagers.get(0).stopSleeping();
        return true;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.CONSUME;
        if (state.getValue(PART) != BedPart.HEAD) {
            pos = pos.relative(state.getValue(FACING));
            state = level.getBlockState(pos);
            if (!state.is(this)) return InteractionResult.CONSUME;
        }
        if (state.getValue(OCCUPIED)) {
            if (!this.kickVillagerOutOfBed(level, pos)) {
                notice(player, pos, Component.translatable("block.minecraft.bed.occupied"));
            }
            return InteractionResult.SUCCESS;
        } else {
            BlockPos headPos = pos;
            player.startSleepInBed(headPos).ifLeft(f -> {
                if (f.getMessage() != null) notice(player, headPos, f.getMessage());
            }).ifRight(s -> {
                if (PlatformHelper.isSleepingBagRegenerationEnabled()) player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
            });
            return InteractionResult.SUCCESS;
        }
    }

    private static void notice(Player player, BlockPos pos, Component message) {
        if (player instanceof ServerPlayer serverPlayer) {
            BlockNotice.send(serverPlayer, pos, message);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.camping.canbeplaced").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(this);
    }
}
