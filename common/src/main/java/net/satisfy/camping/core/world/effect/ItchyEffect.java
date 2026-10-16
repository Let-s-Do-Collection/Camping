package net.satisfy.camping.core.world.effect;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ItchyEffect extends MobEffect {

    public ItchyEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 60 == 0;
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide || entity.getRandom().nextInt(3) == 0) return true;
        entity.swing(entity.getRandom().nextBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, true);
        if (entity instanceof Player player) {
            player.causeFoodExhaustion(0.05F);
        }
        return true;
    }
}
