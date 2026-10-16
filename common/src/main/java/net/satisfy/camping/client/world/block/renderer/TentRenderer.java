package net.satisfy.camping.client.world.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.satisfy.camping.core.world.block.SleepingBagBlock;
import net.satisfy.camping.core.world.block.TentBlock;
import net.satisfy.camping.core.world.block.entity.TentBlockEntity;

public class TentRenderer implements BlockEntityRenderer<TentBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public TentRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(TentBlockEntity tent, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        BlockState tentState = tent.getBlockState();
        SleepingBagBlock bag = tent.getBagBlock();
        if (bag == null || !(tentState.getBlock() instanceof TentBlock)) {
            return;
        }
        Direction facing = tentState.getValue(TentBlock.FACING);
        BlockState bagState = bag.defaultBlockState().setValue(BedBlock.FACING, facing.getOpposite());
        Vec3 offset = TentBlock.getBagOffset(facing);
        poseStack.pushPose();
        poseStack.translate(offset.x, offset.y, offset.z);
        renderPart(tent, bagState.setValue(BedBlock.PART, BedPart.FOOT), BlockPos.ZERO, poseStack, buffer, overlay);
        renderPart(tent, bagState.setValue(BedBlock.PART, BedPart.HEAD), TentBlock.TentPart.MAIN_HEAD.fromMain(BlockPos.ZERO, facing), poseStack, buffer, overlay);
        poseStack.popPose();
    }

    private void renderPart(TentBlockEntity tent, BlockState state, BlockPos offset, PoseStack poseStack, MultiBufferSource buffer, int overlay) {
        Level level = tent.getLevel();
        int light = level != null ? LevelRenderer.getLightColor(level, tent.getBlockPos().offset(offset)) : 0xF000F0;
        poseStack.pushPose();
        poseStack.translate(offset.getX(), offset.getY(), offset.getZ());
        this.blockRenderer.renderSingleBlock(state, poseStack, buffer, light, overlay);
        poseStack.popPose();
    }

    public AABB getRenderBoundingBox(TentBlockEntity tent) {
        return new AABB(tent.getBlockPos()).inflate(2.0);
    }
}
