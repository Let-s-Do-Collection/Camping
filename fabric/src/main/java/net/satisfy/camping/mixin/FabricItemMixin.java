package net.satisfy.camping.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.satisfy.camping.core.util.GrillingUtil;
import net.satisfy.camping.platform.PlatformHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class FabricItemMixin {
    @Inject(method = "isFoil(Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "RETURN"), cancellable = true)
    private void camping$isFoilInject(ItemStack pStack, CallbackInfoReturnable<Boolean> cir) {
        if (PlatformHelper.isGrilledGlintEnabled() && GrillingUtil.isGrilled(pStack)) cir.setReturnValue(true);
    }
}
