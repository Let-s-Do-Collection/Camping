package net.satisfy.camping.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.satisfy.camping.core.util.GrillingUtil;
import net.satisfy.camping.platform.PlatformHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class NeoForgePlayerEatMixin {

    @Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
    private void camping$eat(Level level, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player player)) return;
        if (!PlatformHelper.isGrillingEnabled() || !GrillingUtil.isGrilled(stack)) return;

        FoodProperties props = stack.getFoodProperties(player);
        if (props == null) return;

        GrillingUtil.FoodValue extra = GrillingUtil.getAdditionalFoodValue(stack);

        float multiplier = PlatformHelper.getGrilledFoodMultiplier();
        int targetNutrition = Math.round(props.nutrition() * multiplier) + extra.nutrition();
        float targetSaturation = props.saturation() * multiplier + extra.saturationModifier();

        int addNutrition = targetNutrition - props.nutrition();
        float addSaturation = targetSaturation - props.saturation();

        if (addNutrition <= 0 && addSaturation <= 0f) return;
        if (addNutrition < 0) addNutrition = 0;
        if (addSaturation < 0f) addSaturation = 0f;

        player.getFoodData().eat(addNutrition, addSaturation);
    }
}
