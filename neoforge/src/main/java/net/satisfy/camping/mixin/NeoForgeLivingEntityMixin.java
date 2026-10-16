package net.satisfy.camping.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.camping.core.world.block.SleepingBagBlock;
import net.satisfy.camping.core.world.block.TentBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class NeoForgeLivingEntityMixin {
    @Inject(method = "setPosToBed", at = @At("TAIL"))
    private void camping$lowerOntoSleepingBag(BlockPos pos, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        BlockState state = self.level().getBlockState(pos);
        if (state.getBlock() instanceof TentBlock) {
            Vec3 offset = TentBlock.getBagOffset(state.getValue(TentBlock.FACING));
            self.setPos(pos.getX() + 0.5 + offset.x, pos.getY() + SleepingBagBlock.SLEEPING_HEIGHT, pos.getZ() + 0.5 + offset.z);
        } else if (state.getBlock() instanceof SleepingBagBlock) {
            self.setPos(self.getX(), pos.getY() + SleepingBagBlock.SLEEPING_HEIGHT, self.getZ());
        }
    }
}
