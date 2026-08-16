package dev.maicra.easyfarmersdelightcompat.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.maicra.easyfarmersdelightcompat.block.FarmerVariant;
import dev.maicra.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import java.util.Optional;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import com.mojang.math.Axis;

public final class CompatFarmerBlockEntityRenderer implements BlockEntityRenderer<CompatFarmerBlockEntity> {
    private static final ResourceLocation RICE_CROP_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rice");
    private static final ResourceLocation RICE_PANICLES_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rice_panicles");
    private static final ResourceLocation BUDDING_TOMATO_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "budding_tomatoes");
    private static final ResourceLocation TOMATO_CROP_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "tomatoes");
    private static final ResourceLocation ROPE_BLOCK_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rope");

    private final BlockRenderDispatcher blockRenderer;
    private final EntityRenderDispatcher entityRenderer;

    public CompatFarmerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.entityRenderer = context.getEntityRenderer();
    }

    @Override
    public void render(CompatFarmerBlockEntity farmer, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = farmer.getLevel();
        if (level == null) {
            return;
        }

        HolderLookup.Provider registries = level.registryAccess();
        renderCrop(farmer, registries, poseStack, bufferSource, packedLight, packedOverlay);
        renderVillager(farmer, registries, poseStack, bufferSource, packedLight, partialTick);
    }

    private void renderCrop(CompatFarmerBlockEntity farmer, HolderLookup.Provider registries, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState crop = farmer.easyVillagers().getCrop(registries);
        if (crop == null) {
            return;
        }

        FarmerVariant variant = farmer.variant();
        ResourceLocation cropId = BuiltInRegistries.BLOCK.getKey(crop.getBlock());

        if (variant.isAquatic() && RICE_CROP_ID.equals(cropId)) {
            renderRice(farmer, poseStack, bufferSource, packedLight, packedOverlay, crop);
            return;
        }

        if (isTomatoState(crop)) {
            renderTomato(farmer, poseStack, bufferSource, packedLight, packedOverlay, crop);
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.18D, 0.13D, 0.18D);
        poseStack.scale(0.64F, 0.64F, 0.64F);
        blockRenderer.renderSingleBlock(crop, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private void renderRice(CompatFarmerBlockEntity farmer, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, BlockState lowerState) {
        poseStack.pushPose();
        poseStack.translate(0.18D, 0.07D, 0.18D);
        poseStack.scale(0.64F, 0.64F, 0.64F);
        blockRenderer.renderSingleBlock(lowerState, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();

        if (farmer.paddyGrowth() >= 4) {
            Block paniclesBlock = BuiltInRegistries.BLOCK.get(RICE_PANICLES_ID);
            BlockState panicles = withAge(paniclesBlock.defaultBlockState(), farmer.paddyGrowth() - 4);
            poseStack.pushPose();
            poseStack.translate(0.18D, 0.47D, 0.18D);
            poseStack.scale(0.64F, 0.64F, 0.64F);
            blockRenderer.renderSingleBlock(panicles, poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }

    private void renderTomato(CompatFarmerBlockEntity farmer, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, BlockState baseCrop) {
        poseStack.pushPose();
        poseStack.translate(0.15D, 0.11D, 0.15D);
        poseStack.scale(0.70F, 0.70F, 0.70F);
        blockRenderer.renderSingleBlock(baseCrop, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();

        Block ropeBlock = BuiltInRegistries.BLOCK.get(ROPE_BLOCK_ID);
        Block tomatoBlock = BuiltInRegistries.BLOCK.get(TOMATO_CROP_ID);

        if (farmer.ropeCount() >= 1) {
            poseStack.pushPose();
            poseStack.translate(0.375D, 0.33D, 0.375D);
            poseStack.scale(0.25F, 0.25F, 0.25F);
            blockRenderer.renderSingleBlock(ropeBlock.defaultBlockState(), poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0.15D, 0.36D, 0.15D);
            poseStack.scale(0.70F, 0.70F, 0.70F);
            blockRenderer.renderSingleBlock(withAge(tomatoBlock.defaultBlockState(), farmer.ropeOneProgress()), poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
        }

        if (farmer.ropeCount() >= 2) {
            poseStack.pushPose();
            poseStack.translate(0.375D, 0.59D, 0.375D);
            poseStack.scale(0.25F, 0.25F, 0.25F);
            blockRenderer.renderSingleBlock(ropeBlock.defaultBlockState(), poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0.15D, 0.61D, 0.15D);
            poseStack.scale(0.70F, 0.70F, 0.70F);
            blockRenderer.renderSingleBlock(withAge(tomatoBlock.defaultBlockState(), farmer.ropeTwoProgress()), poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
        }
    }

    private void renderVillager(CompatFarmerBlockEntity farmer, HolderLookup.Provider registries, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick) {
        Villager villager = farmer.easyVillagers().getVillagerEntity(registries);
        if (villager == null) {
            return;
        }

        float scale = farmer.variant().isAquatic() ? 0.42F : 0.44F;
        double y = farmer.variant().isAquatic() ? 0.20D : 0.16D;

        poseStack.pushPose();
        poseStack.translate(0.5D, y, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.scale(scale, scale, scale);
        entityRenderer.render(villager, 0.0D, 0.0D, 0.0D, 0.0F, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    private static boolean isTomatoState(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return BUDDING_TOMATO_ID.equals(id) || TOMATO_CROP_ID.equals(id);
    }

    private static BlockState withAge(BlockState state, int age) {
        Optional<Property<?>> ageProperty = state.getProperties().stream()
                .filter(property -> property.getName().equals("age"))
                .findFirst();
        if (ageProperty.isEmpty() || !(ageProperty.get() instanceof IntegerProperty integerProperty)) {
            return state;
        }

        int max = integerProperty.getPossibleValues().stream().max(Integer::compareTo).orElse(0);
        int safeAge = Math.max(0, Math.min(max, age));
        return state.setValue(integerProperty, safeAge);
    }
}
