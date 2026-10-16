package net.satisfy.camping.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.satisfy.camping.core.registry.CampingItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTrader.class)
public class FabricWanderingTraderMixin {

    @Inject(method = "updateTrades", at = @At("RETURN"))
    private void addCustomTrade(CallbackInfo ci) {
        WanderingTrader wanderingTrader = (WanderingTrader) (Object) this;
        MerchantOffers offers = wanderingTrader.getOffers();
        RandomSource random = RandomSource.create();

        if (random.nextInt(5) == 0) {
            int emeraldCost = 27 + random.nextInt(27);
            MerchantOffer customOffer = new MerchantOffer(new ItemCost(Items.EMERALD, emeraldCost), new ItemStack(CampingItems.WANDERER_BACKPACK), 6, 2, 0.05F);
            offers.add(customOffer);
        }

        if (random.nextInt(5) == 0) {
            int emeraldCost = 16 + random.nextInt(16);
            offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, emeraldCost), new ItemStack(CampingItems.WANDERER_BAG), 6, 2, 0.05F));
        }
    }
}
