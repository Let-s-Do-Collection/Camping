package net.satisfy.camping.core.world.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.satisfy.camping.core.registry.CampingEffects;
import net.satisfy.camping.core.world.entity.MosquitoSwarmEntity;
import net.satisfy.camping.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MosquitoRepellentItem extends Item {
    private static final int SPRAY_PARTICLES = 8;
    private static final double SPRAY_RANGE = 3.0;
    private static final double SPRAY_CONE = 0.6;
    private static final ResourceLocation COLORED_STEAM = ResourceLocation.fromNamespaceAndPath("foundation", "colored_steam");
    private static final int SPRAY_COLOR = 0xFFB8D99A;

    public MosquitoRepellentItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.3F, 1.8F + level.random.nextFloat() * 0.2F);
        if (level instanceof ServerLevel serverLevel) {
            player.addEffect(new MobEffectInstance(CampingEffects.repellent(), PlatformHelper.getRepellentDuration(), 0, false, true, true));
            player.removeEffect(CampingEffects.itchy());
            spray(serverLevel, player, hand);
            player.getCooldowns().addCooldown(this, 20);
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            if (stack.isEmpty()) {
                return InteractionResultHolder.consume(new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        MobEffectInstance effect = new MobEffectInstance(CampingEffects.repellent(), PlatformHelper.getRepellentDuration());
        tooltip.add(Component.translatable("tooltip.camping.mosquito_repellent.when_sprayed").withStyle(ChatFormatting.GRAY));
        tooltip.add(CommonComponents.space().append(Component.translatable("potion.withDuration", Component.translatable(effect.getDescriptionId()), MobEffectUtil.formatDuration(effect, 1.0F, context.tickRate()))).withStyle(ChatFormatting.BLUE));
    }

    private static void spray(ServerLevel level, Player player, InteractionHand hand) {
        HumanoidArm arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();
        Vec3 look = player.getLookAngle();
        Vec3 right = look.cross(new Vec3(0.0, 1.0, 0.0)).normalize().scale(arm == HumanoidArm.RIGHT ? 0.35 : -0.35);
        Vec3 nozzle = player.getEyePosition().add(look.scale(0.5)).add(right).add(0.0, -0.3, 0.0);
        RandomSource random = player.getRandom();
        @SuppressWarnings("unchecked")
        ParticleType<ColorParticleOption> type = (ParticleType<ColorParticleOption>) BuiltInRegistries.PARTICLE_TYPE.get(COLORED_STEAM);
        if (type != null) {
            ColorParticleOption particle = ColorParticleOption.create(type, SPRAY_COLOR);
            for (int i = 0; i < SPRAY_PARTICLES; i++) {
                Vec3 velocity = look.scale(0.3 + random.nextDouble() * 0.15).add(random.nextGaussian() * 0.04, random.nextGaussian() * 0.03, random.nextGaussian() * 0.04);
                level.sendParticles(particle, nozzle.x, nozzle.y, nozzle.z, 0, velocity.x, velocity.y, velocity.z, 1.0);
            }
        }
        for (MosquitoSwarmEntity swarm : level.getEntitiesOfClass(MosquitoSwarmEntity.class, new AABB(nozzle, nozzle).inflate(SPRAY_RANGE))) {
            Vec3 toSwarm = swarm.getBoundingBox().getCenter().subtract(nozzle);
            if (toSwarm.length() <= SPRAY_RANGE && toSwarm.normalize().dot(look) >= SPRAY_CONE) {
                swarm.sprayed();
            }
        }
    }
}
