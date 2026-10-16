package net.satisfy.camping.core.world.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.satisfy.camping.core.registry.CampingItems;
import net.satisfy.camping.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MarshmallowOnAStickItem extends Item {
    private static final String NBT_STAGE = "marshmallowStage";
    private static final String NBT_ROASTING = "marshmallowRoasting";
    private static final String NBT_TICKS = "roastTicks";
    private static final String NBT_TARGET = "roastTarget";
    private static final String NBT_POS = "roastPos";
    private static final String[] STAGES = {"default", "warmed", "melted", "charred", "totally_burnt"};

    public MarshmallowOnAStickItem(Properties properties) {
        super(properties);
    }

    private static CompoundTag getOrCreateData(ItemStack stack) {
        CustomData d = stack.get(DataComponents.CUSTOM_DATA);
        return d == null ? new CompoundTag() : d.copyTag();
    }

    private static void saveData(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    private static void syncHand(ServerPlayer p, InteractionHand hand) {
        ItemStack s = p.getItemInHand(hand);
        p.setItemInHand(hand, s);
        p.getInventory().setChanged();
        p.containerMenu.broadcastChanges();
    }

    private static void setStageOn(ItemStack stack, String stage) {
        CompoundTag t = getOrCreateData(stack);
        t.putString(NBT_STAGE, stage);
        saveData(stack, t);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        CompoundTag tag = getOrCreateData(stack);
        return tag.getBoolean(NBT_ROASTING) ? UseAnim.NONE : UseAnim.EAT;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        CompoundTag tag = getOrCreateData(stack);
        return tag.getBoolean(NBT_ROASTING) ? 72000 : 32;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = getOrCreateData(stack);
        if (!tag.contains(NBT_STAGE)) tag.putString(NBT_STAGE, "default");
        BlockHitResult bhr = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
        boolean roasting = bhr.getType() == HitResult.Type.BLOCK && level.getBlockState(bhr.getBlockPos()).getBlock() instanceof CampfireBlock;
        tag.putBoolean(NBT_ROASTING, roasting);
        if (roasting) {
            tag.putInt(NBT_TICKS, 0);
            tag.putInt(NBT_TARGET, PlatformHelper.getMarshmallowRoastDuration() + level.random.nextInt(60));
            tag.putLong(NBT_POS, bhr.getBlockPos().asLong());
            saveData(stack, tag);
            player.startUsingItem(hand);
            if (!level.isClientSide && player instanceof ServerPlayer sp) syncHand(sp, hand);
            return InteractionResultHolder.consume(stack);
        }
        saveData(stack, tag);
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockState state = level.getBlockState(ctx.getClickedPos());
        if (!(state.getBlock() instanceof CampfireBlock)) return InteractionResult.PASS;
        if (state.hasProperty(CampfireBlock.LIT) && !state.getValue(CampfireBlock.LIT)) return InteractionResult.PASS;
        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.PASS;
        ItemStack stack = ctx.getItemInHand();
        CompoundTag tag = getOrCreateData(stack);
        if (!tag.contains(NBT_STAGE)) tag.putString(NBT_STAGE, "default");
        tag.putBoolean(NBT_ROASTING, true);
        tag.putInt(NBT_TICKS, 0);
        tag.putInt(NBT_TARGET, PlatformHelper.getMarshmallowRoastDuration() + level.random.nextInt(60));
        tag.putLong(NBT_POS, ctx.getClickedPos().asLong());
        saveData(stack, tag);
        player.startUsingItem(ctx.getHand());
        if (!level.isClientSide && player instanceof ServerPlayer sp) syncHand(sp, ctx.getHand());
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity user, int timeLeft) {
        CompoundTag tag = getOrCreateData(stack);
        tag.remove(NBT_TICKS);
        tag.remove(NBT_TARGET);
        tag.remove(NBT_ROASTING);
        tag.remove(NBT_POS);
        saveData(stack, tag);
        if (user instanceof ServerPlayer sp) syncHand(sp, sp.getUsedItemHand());
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level level, LivingEntity entity) {
        CompoundTag tag = getOrCreateData(stack);
        if (tag.getBoolean(NBT_ROASTING)) {
            tag.remove(NBT_ROASTING);
            tag.remove(NBT_POS);
            saveData(stack, tag);
            if (entity instanceof ServerPlayer sp) syncHand(sp, sp.getUsedItemHand());
            return stack;
        }
        FoodProperties props = resolveBaseFood(stack);
        if (entity instanceof Player player) {
            player.getFoodData().eat(props.nutrition(), props.saturation());
            for (FoodProperties.PossibleEffect e : props.effects()) {
                if (level.random.nextFloat() < e.probability()) {
                    entity.addEffect(new MobEffectInstance(e.effect()));
                }
            }
            String s = tag.getString(NBT_STAGE);
            switch (s) {
                case "melted" -> entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, PlatformHelper.getEffectDuration("melted_marshmallow"), 0));
                case "charred" -> entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, PlatformHelper.getEffectDuration("charred_marshmallow"), 0));
                case "totally_burnt" -> entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, PlatformHelper.getEffectDuration("totally_burnt_marshmallow"), 1));
            }
            Abilities ab = player.getAbilities();
            if (!ab.instabuild) stack.shrink(1);
        } else {
            stack.shrink(1);
        }
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotIndex, boolean isSelected) {
        CompoundTag tag = getOrCreateData(stack);
        if (!tag.contains(NBT_STAGE)) tag.putString(NBT_STAGE, "default");

        String stage = tag.getString(NBT_STAGE);
        int modelData = switch (stage) {
            case "warmed" -> 1;
            case "melted" -> 2;
            case "charred" -> 3;
            case "totally_burnt" -> 4;
            default -> 0;
        };

        if (modelData == 0) {
            stack.remove(DataComponents.CUSTOM_MODEL_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(modelData));
        }

        saveData(stack, tag);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!level.isClientSide) {
            if (!(entity instanceof ServerPlayer sp)) return;
            InteractionHand hand = sp.getUsedItemHand();
            ItemStack use = sp.getItemInHand(hand);
            if (use.isEmpty() || !use.is(this)) return;
            CompoundTag tag = getOrCreateData(use);
            if (!tag.getBoolean(NBT_ROASTING) || !tag.contains(NBT_POS)) return;
            BlockPos pos = BlockPos.of(tag.getLong(NBT_POS));
            BlockState bs = level.getBlockState(pos);
            boolean lit = bs.getBlock() instanceof CampfireBlock && (!bs.hasProperty(CampfireBlock.LIT) || bs.getValue(CampfireBlock.LIT));
            if (!lit) return;
            int ticks = tag.getInt(NBT_TICKS) + 1;
            tag.putInt(NBT_TICKS, ticks);
            int target = tag.getInt(NBT_TARGET);
            if (target > 0 && ticks >= target) {
                String s = tag.getString(NBT_STAGE);
                if (s.isEmpty()) s = "default";
                int idx = getStageIndex(s);
                if (idx < STAGES.length - 1) {
                    setStageOn(use, STAGES[idx + 1]);
                }
                CompoundTag t2 = getOrCreateData(use);
                t2.putInt(NBT_TARGET, PlatformHelper.getMarshmallowRoastDuration() + level.random.nextInt(60));
                t2.putInt(NBT_TICKS, 0);
                saveData(use, t2);
                syncHand(sp, hand);
            } else {
                saveData(use, tag);
            }
            return;
        }

        CompoundTag tag = getOrCreateData(stack);
        if (!tag.getBoolean(NBT_ROASTING)) return;
        if (!(entity instanceof Player player)) return;
        if (!tag.contains(NBT_POS)) return;
        BlockPos pos = BlockPos.of(tag.getLong(NBT_POS));
        BlockState bs = level.getBlockState(pos);
        if (!(bs.getBlock() instanceof CampfireBlock)) return;

        Vec3 look = player.getLookAngle();
        Vec3 right = look.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 up = right.cross(look).normalize();

        double baseX = player.getX() + look.x * 0.4 + right.x * 0.15;
        double baseY = player.getY() + 1.3 + up.y * 0.1;
        double baseZ = player.getZ() + look.z * 0.4 + right.z * 0.15;

        if ((remainingUseDuration & 7) == 0 && level.random.nextFloat() < 0.10f) {
            double ix = baseX + right.x * 0.125;
            double iy = baseY + right.y * 0.125;
            double iz = baseZ + right.z * 0.125;
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(CampingItems.MARSHMALLOW)), ix, iy, iz, 0, 0, 0);
        }

        if ((remainingUseDuration & 15) == 0 && level.random.nextFloat() < 0.075f) {
            double dx = baseX + right.x * 0.145 + up.x * -0.05;
            double dy = baseY + right.y * 0.145 + up.y * -0.05;
            double dz = baseZ + right.z * 0.145 + up.z * -0.05;
            double dvx = (level.random.nextDouble() - 0.5) * 0.02;
            double dvy = -0.06 - level.random.nextDouble() * 0.04;
            double dvz = (level.random.nextDouble() - 0.5) * 0.02;
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(CampingItems.MARSHMALLOW)), dx, dy, dz, dvx, dvy, dvz);
        }

        if ((remainingUseDuration & 7) == 0 && level.random.nextFloat() < 0.12f) {
            double sx = baseX + right.x * 0.12 + up.x * 0.22;
            double sy = baseY + right.y * 0.12 + up.y * 0.22;
            double sz = baseZ + right.z * 0.12 + up.z * 0.22;
            level.addParticle(ParticleTypes.SMOKE, sx, sy, sz, 0, 0.02, 0);
        }
    }

    private static int getStageIndex(String stage) {
        for (int i = 0; i < STAGES.length; i++) if (STAGES[i].equals(stage)) return i;
        return 0;
    }

    private FoodProperties resolveBaseFood(ItemStack stack) {
        String s = getOrCreateData(stack).getString(NBT_STAGE);
        if (s.isEmpty()) s = "default";
        if (s.equals("default")) {
            FoodProperties b = new ItemStack(Items.COOKIE).get(DataComponents.FOOD);
            assert b != null;
            return new FoodProperties.Builder().nutrition(b.nutrition()).saturationModifier(b.saturation()).alwaysEdible().build();
        }
        if (s.equals("warmed")) {
            FoodProperties b = new ItemStack(Items.APPLE).get(DataComponents.FOOD);
            assert b != null;
            return new FoodProperties.Builder().nutrition(b.nutrition()).saturationModifier(b.saturation()).alwaysEdible().build();
        }
        if (s.equals("melted")) {
            FoodProperties b = new ItemStack(Items.BREAD).get(DataComponents.FOOD);
            assert b != null;
            return new FoodProperties.Builder().nutrition(b.nutrition()).saturationModifier(b.saturation()).alwaysEdible().build();
        }
        FoodProperties b = new ItemStack(Items.ROTTEN_FLESH).get(DataComponents.FOOD);
        assert b != null;
        return new FoodProperties.Builder().nutrition(b.nutrition()).saturationModifier(b.saturation()).alwaysEdible().build();
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        CompoundTag tag = getOrCreateData(itemStack);
        String s = tag.getString(NBT_STAGE);
        if (s.isEmpty()) s = "default";
        tooltip.add(Component.translatable("tooltip.camping.marshmallow_stage." + s).withStyle(switch (s) {
            case "warmed" -> ChatFormatting.YELLOW;
            case "melted" -> ChatFormatting.GREEN;
            case "charred" -> ChatFormatting.GOLD;
            case "totally_burnt" -> ChatFormatting.RED;
            default -> ChatFormatting.GRAY;
        }));
    }
}
