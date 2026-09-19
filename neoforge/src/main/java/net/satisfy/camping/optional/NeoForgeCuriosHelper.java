package net.satisfy.camping.optional;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.satisfy.camping.client.renderer.entity.layers.BackpackRenderLayer;
import net.satisfy.camping.core.registry.CampingItems;
import net.satisfy.camping.core.util.BackpackRegistry;
import net.satisfy.camping.core.world.item.BackpackBlockItem;
import net.satisfy.camping.core.world.item.EnderpackBlockItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.concurrent.atomic.AtomicReference;

public class NeoForgeCuriosHelper {
    public static ItemStack getBackpackFromCurios(Player player) {
        AtomicReference<ItemStack> returned = new AtomicReference<>(ItemStack.EMPTY);

        CuriosApi.getCuriosInventory(player).ifPresent(curioInventory -> {
            curioInventory.findFirstCurio(CampingItems.SMALL_BACKPACK).ifPresent(slotResult -> returned.set(slotResult.stack()));
            curioInventory.findFirstCurio(CampingItems.LARGE_BACKPACK).ifPresent(slotResult -> returned.set(slotResult.stack()));
            curioInventory.findFirstCurio(CampingItems.WANDERER_BAG).ifPresent(slotResult -> returned.set(slotResult.stack()));
            curioInventory.findFirstCurio(CampingItems.WANDERER_BACKPACK).ifPresent(slotResult -> returned.set(slotResult.stack()));
            curioInventory.findFirstCurio(CampingItems.GOODYBAG).ifPresent(slotResult -> returned.set(slotResult.stack()));
            curioInventory.findFirstCurio(CampingItems.SHEEPBAG).ifPresent(slotResult -> returned.set(slotResult.stack()));
            curioInventory.findFirstCurio(CampingItems.ENDERBAG).ifPresent(slotResult -> returned.set(slotResult.stack()));
            curioInventory.findFirstCurio(CampingItems.ENDERPACK).ifPresent(slotResult -> returned.set(slotResult.stack()));
        });

        return returned.get();
    }

    public static void registerRenderersForCurios() {
        CuriosRendererRegistry.register(CampingItems.SMALL_BACKPACK, BackpackCuriosRenderer::new);
        CuriosRendererRegistry.register(CampingItems.LARGE_BACKPACK, BackpackCuriosRenderer::new);
        CuriosRendererRegistry.register(CampingItems.WANDERER_BACKPACK, BackpackCuriosRenderer::new);
        CuriosRendererRegistry.register(CampingItems.WANDERER_BAG, BackpackCuriosRenderer::new);
        CuriosRendererRegistry.register(CampingItems.SHEEPBAG, BackpackCuriosRenderer::new);
        CuriosRendererRegistry.register(CampingItems.GOODYBAG, BackpackCuriosRenderer::new);
        CuriosRendererRegistry.register(CampingItems.ENDERPACK, EnderpackCuriosRenderer::new);
        CuriosRendererRegistry.register(CampingItems.ENDERBAG, EnderpackCuriosRenderer::new);
    }

    public static class BackpackCuriosRenderer implements ICurioRenderer {

        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack itemStack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource multiBufferSource, int i, float v, float v1, float v2, float v3, float v4, float v5) {
            BackpackBlockItem backpack = (BackpackBlockItem) itemStack.getItem();
            Model model = BackpackRegistry.getBodyModel(backpack, ((HumanoidModel<?>) renderLayerParent.getModel()).body);
            poseStack.pushPose();
            BackpackRenderLayer.performTranslations(poseStack, backpack.variant, slotContext.entity().isCrouching());
            model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(backpack.getTexture())), i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }

    public static class EnderpackCuriosRenderer implements ICurioRenderer {

        @Override
        public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack itemStack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource multiBufferSource, int i, float v, float v1, float v2, float v3, float v4, float v5) {
            EnderpackBlockItem enderpack = (EnderpackBlockItem) itemStack.getItem();
            Model model = BackpackRegistry.getBodyModel(enderpack, ((HumanoidModel<?>) renderLayerParent.getModel()).body);

            final boolean isEnderBag = enderpack == CampingItems.ENDERBAG;
            final boolean isEnderPack = enderpack == CampingItems.ENDERPACK;

            poseStack.pushPose();
            if (isEnderBag) poseStack.translate(-0.0625f * 5f, 0, 0.0625f * 2f);
            if (isEnderPack) poseStack.translate(-0.0625f * 5f, 0, 0.0625f * 2f);
            if (slotContext.entity().isCrouching()) poseStack.translate(0, -0.0625f - (0.0625f / 8f), (0.0625f) / 10.0f);

            model.renderToBuffer(poseStack, multiBufferSource.getBuffer(model.renderType(enderpack.getTexture())), i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}