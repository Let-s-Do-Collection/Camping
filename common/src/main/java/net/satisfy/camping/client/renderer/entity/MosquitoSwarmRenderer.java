package net.satisfy.camping.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.satisfy.camping.core.world.entity.MosquitoSwarmEntity;
import org.jetbrains.annotations.NotNull;

public class MosquitoSwarmRenderer extends EntityRenderer<MosquitoSwarmEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("foundation", "textures/particle/fly.png");
    private static final float SIZE = 0.09F;

    public MosquitoSwarmRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(@NotNull MosquitoSwarmEntity entity, float yaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffers, int light) {
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityTranslucent(TEXTURE));
        float time = entity.tickCount + partialTicks;
        int count = entity.getMosquitoes();
        boolean chasing = entity.isChasing();
        for (int i = 0; i < count; i++) {
            float seed = i * 1.618F + entity.getId() * 0.37F;
            float level = (i + 0.5F) / count;
            float direction = i % 2 == 0 ? 1.0F : -1.0F;
            float height;
            float radius;
            float speed;
            if (chasing) {
                height = 0.9F + Mth.sin(seed * 3.0F) * 0.5F;
                radius = 0.35F + Mth.sin(seed * 5.0F) * 0.15F;
                speed = 0.35F + (i % 3) * 0.06F;
            } else {
                height = 0.2F + level * 1.7F;
                radius = 0.12F + level * 0.45F;
                speed = 0.22F + (i % 3) * 0.04F;
            }
            float angle = time * speed * direction + seed * 6.0F;
            float x = Mth.cos(angle) * radius + Mth.sin(time * 2.3F + seed) * 0.03F;
            float y = height + Mth.sin(time * 0.35F + seed * 2.0F) * 0.1F;
            float z = Mth.sin(angle) * radius + Mth.cos(time * 2.1F + seed) * 0.03F;

            poseStack.pushPose();
            poseStack.translate(x, y, z);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.scale(SIZE, SIZE, SIZE);
            PoseStack.Pose pose = poseStack.last();
            vertex(consumer, pose, light, -0.5F, -0.5F, 0.0F, 1.0F);
            vertex(consumer, pose, light, 0.5F, -0.5F, 1.0F, 1.0F);
            vertex(consumer, pose, light, 0.5F, 0.5F, 1.0F, 0.0F);
            vertex(consumer, pose, light, -0.5F, 0.5F, 0.0F, 0.0F);
            poseStack.popPose();
        }
        super.render(entity, yaw, partialTicks, poseStack, buffers, light);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int light, float x, float y, float u, float v) {
        consumer.addVertex(pose, x, y, 0.0F).setColor(255, 255, 255, 255).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MosquitoSwarmEntity entity) {
        return TEXTURE;
    }
}
