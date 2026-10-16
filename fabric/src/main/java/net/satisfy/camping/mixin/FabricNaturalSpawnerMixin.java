package net.satisfy.camping.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.satisfy.camping.core.world.block.entity.TentBlockEntity;
import net.satisfy.camping.platform.PlatformHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.class)
public class FabricNaturalSpawnerMixin {
    @Inject(method = "isValidPositionForMob", at = @At("HEAD"), cancellable = true)
    private static void camping$blockSpawnNearTent(ServerLevel level, Mob mob, double distance, CallbackInfoReturnable<Boolean> cir) {
        if (mob.getType().getCategory() == MobCategory.MONSTER && TentBlockEntity.isNearTent(level, mob.blockPosition(), PlatformHelper.getTentSafeRange())) {
            cir.setReturnValue(false);
        }
    }
}
