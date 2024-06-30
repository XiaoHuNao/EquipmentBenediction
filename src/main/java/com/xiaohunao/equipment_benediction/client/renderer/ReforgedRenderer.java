package com.xiaohunao.equipment_benediction.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.xiaohunao.equipment_benediction.api.IQuality;
import com.xiaohunao.equipment_benediction.common.block.entity.ReforgedBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class ReforgedRenderer implements BlockEntityRenderer<ReforgedBlockEntity> {
    private final Random random = new Random();

    public ReforgedRenderer(BlockEntityRendererProvider.Context context) {
    }


    @Override
    public void render(ReforgedBlockEntity reforgedBlockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        long posLong = reforgedBlockEntity.getBlockPos().asLong();
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        ItemStack equippedItem = reforgedBlockEntity.getEquippedItem();
        ItemStack reforgedItem = reforgedBlockEntity.getReforgedItem();

        if (reforgedBlockEntity.getLevel() != null){
            poseStack.pushPose();
            RenderSystem.enableDepthTest();
            minecraft.getBlockRenderer().renderSingleBlock(reforgedBlockEntity.getBlockState(), poseStack, bufferSource, combinedLight, combinedOverlay, reforgedBlockEntity.getModelData(), RenderType.cutout());
            poseStack.translate(0.5D, 1, 0.5D);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(30.0F));
            poseStack.scale(0.5F, 0.5F, 0.5F);

            if (!equippedItem.isEmpty()) {
//                itemRenderer.renderStatic(equippedItem, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, reforgedBlockEntity.getLevel(), (int) posLong);
                itemRenderer.renderStatic(equippedItem, ItemTransforms.TransformType.FIXED,combinedLight, combinedOverlay,poseStack, bufferSource,(int) posLong);
            }

            poseStack.pushPose();
            if (!reforgedItem.isEmpty()){
                int itemRenderCount = this.getModelCount(reforgedItem);
                int seed = reforgedItem.isEmpty() ? 187 : Item.getId(reforgedItem.getItem()) * 2;
                this.random.setSeed(seed);
                for (int i = 0; i < itemRenderCount; i++) {
                    float xOffset = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F * 0.5F;
                    float zOffset = (this.random.nextFloat() * 2.0F - 1.0F) * 0.3F * 0.5F;
                    if (xOffset >0.0F) {
                        xOffset = -xOffset;
                    }
                    if (zOffset > 0.0F) {
                        zOffset = -zOffset;
                    }
                    poseStack.translate(xOffset, 0.01 * (i + 1), zOffset);
                    itemRenderer.renderStatic(reforgedItem, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, bufferSource, (int) posLong);
                }
            }
            float renderOffset = reforgedBlockEntity.getRenderOffset();
            renderQualityTip(reforgedBlockEntity, equippedItem,poseStack, bufferSource, combinedLight, partialTicks,renderOffset);
            poseStack.popPose();
            poseStack.popPose();
        }
    }

    private static void renderQualityTip(ReforgedBlockEntity reforgedBlockEntity, ItemStack equippedItem,
                                         PoseStack poseStack, MultiBufferSource bufferSource,
                                         int combinedLight, float partialTicks,float renderOffset) {
        poseStack.pushPose();
        Minecraft minecraft = Minecraft.getInstance();
        BlockEntityRenderDispatcher blockEntityRenderDispatcher = minecraft.getBlockEntityRenderDispatcher();

        Font font = minecraft.font;
        if (reforgedBlockEntity.isRenderQualityTip()){
            double distanceToSqr = blockEntityRenderDispatcher.camera.getPosition().distanceToSqr(Vec3.atCenterOf(reforgedBlockEntity.getBlockPos().offset(0.5, 0.5, 0.5)));
            if (distanceToSqr < 16 && renderOffset <= 2.0F){
                IQuality renderQuality = reforgedBlockEntity.getRenderQuality();
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(30));
                poseStack.mulPose(blockEntityRenderDispatcher.camera.rotation());
                poseStack.translate(0.0, Math.sin(renderOffset), 0.0);
                poseStack.scale(-0.05F, -0.05F, 0.05F);

                float textWidth = font.width(renderQuality.getDisplayName());
//                Minecraft.getInstance().font.drawInBatch(renderQuality.getDisplayName(), -textWidth / 2.0F, 0.0F, renderQuality.getColor().getValue(), false, poseStack.last().pose(), bufferSource, Font.DisplayMode.POLYGON_OFFSET, 0, combinedLight);
                Minecraft.getInstance().font.drawInBatch(renderQuality.getDisplayName(), -textWidth / 2.0F, 0.0F, renderQuality.getColor().getValue(), false, poseStack.last().pose(), bufferSource, false, 0, combinedLight);
                reforgedBlockEntity.setRenderOffset(renderOffset + 0.01F);
            }else {
                reforgedBlockEntity.setRenderOffset(0.0F);
                reforgedBlockEntity.setRenderQualityTip(false);
            }
        }
        poseStack.popPose();
    }

    protected int getModelCount(ItemStack stack) {
        if (stack.getCount() > 48) {
            return 5;
        } else if (stack.getCount() > 32) {
            return 4;
        } else if (stack.getCount() > 16) {
            return 3;
        } else if (stack.getCount() > 8) {
            return 2;
        }
        return 1;
    }
}


