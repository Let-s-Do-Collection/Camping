package net.satisfy.camping.mixin;

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

@Mixin(Player.class)
public abstract class FabricPlayerEatMixin {
    @Inject(method="eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at=@At("TAIL"))
    private void camping$eat(Level level, ItemStack stack, FoodProperties props, CallbackInfoReturnable<ItemStack> cir) {
        if (!PlatformHelper.isGrillingEnabled() || !GrillingUtil.isGrilled(stack)) return;
        Player self=(Player)(Object)this;
        GrillingUtil.FoodValue extra = GrillingUtil.getAdditionalFoodValue(stack);
        self.getFoodData().eat(extra.nutrition(), extra.saturationModifier());
    }
}
