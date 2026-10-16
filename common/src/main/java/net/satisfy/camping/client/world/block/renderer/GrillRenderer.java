package net.satisfy.camping.client.world.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.satisfy.camping.core.world.block.GrillBlock;
import net.satisfy.camping.core.world.block.entity.GrillBlockEntity;

public class GrillRenderer implements BlockEntityRenderer<GrillBlockEntity> {

    private static final float ITEM_SIZE = 0.3F;
    private static final float ITEM_DISPLAY_HEIGHT = 0.95f;
    private static final float ITEM_DISPLAY_OFFSET = -0.15625F;
    private static final float FLIP_TICKS = 10.0F;

    private final ItemRenderer itemRenderer;

    public GrillRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(GrillBlockEntity grillBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        NonNullList<ItemStack> items = grillBlockEntity.getItems();
        int posLong = (int) grillBlockEntity.getBlockPos().asLong();
        Direction direction = grillBlockEntity.getBlockState().getValue(GrillBlock.FACING);
        float time = grillBlockEntity.getLevel() == null ? 0.0F : grillBlockEntity.getLevel().getGameTime() + partialTicks;

        for (int i = 0; i < items.size(); ++i) {
            ItemStack itemStack = items.get(i);
            if (!itemStack.isEmpty()) {
                long flipStart = grillBlockEntity.getFlipStart(i);
                float flip = flipStart == 0L ? 1.0F : Mth.clamp((time - flipStart) / FLIP_TICKS, 0.0F, 1.0F);
                float lift = Mth.sin(flip * Mth.PI) * 0.3F;
                poseStack.pushPose();
                poseStack.translate(0.5F, ITEM_DISPLAY_HEIGHT + lift, 0.5F);
                Direction itemDirection = Direction.from2DDataValue((i + direction.get2DDataValue()) % 4);
                float rotation = -itemDirection.toYRot();
                poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
                poseStack.translate(ITEM_DISPLAY_OFFSET, 0.0F, ITEM_DISPLAY_OFFSET);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F + flip * 360.0F));
                poseStack.scale(ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);
                this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, grillBlockEntity.getLevel(), posLong + i);
                poseStack.popPose();
            }
        }
    }
}
