package dev.celerbi.easyfarmersdelightcompat.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.celerbi.easyfarmersdelightcompat.block.CompatFarmerBlock;
import dev.celerbi.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * Renders the contents of our farmer blocks using the same spatial rules as
 * Easy Villagers' 1.21.1 FarmerRenderer: villager behind the crop, both rotated
 * by the block FACING value and both kept inside the one-block enclosure.
 */
public final class CompatFarmerBlockEntityRenderer implements BlockEntityRenderer<CompatFarmerBlockEntity> {
    private static final ResourceLocation RICE_CROP_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rice");
    private static final ResourceLocation RICE_PANICLES_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rice_panicles");
    private static final ResourceLocation BUDDING_TOMATO_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "budding_tomatoes");
    private static final ResourceLocation TOMATO_CROP_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "tomatoes");
    private static final ResourceLocation TOMATO_ON_ROPE_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "tomatoes_on_rope");

    private static final float FARM_SCALE = 0.45F;
    private static final float TOMATO_STACK_SCALE = 0.28F;

    private final Minecraft minecraft;
    private final BlockRenderDispatcher blockRenderer;
    private final VillagerRenderer villagerRenderer;

    public CompatFarmerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.minecraft = Minecraft.getInstance();
        this.blockRenderer = context.getBlockRenderDispatcher();
        EntityRendererProvider.Context entityContext = new EntityRendererProvider.Context(
                minecraft.getEntityRenderDispatcher(),
                minecraft.getItemRenderer(),
                minecraft.getBlockRenderer(),
                minecraft.gameRenderer.itemInHandRenderer,
                minecraft.getResourceManager(),
                minecraft.getEntityModels(),
                minecraft.font
        );
        this.villagerRenderer = new VillagerRenderer(entityContext);
    }

    @Override
    public void render(
            CompatFarmerBlockEntity farmer,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int combinedLight,
            int combinedOverlay
    ) {
        Level level = farmer.getLevel();
        if (level == null) {
            return;
        }

        Direction direction = farmer.getBlockState().hasProperty(CompatFarmerBlock.FACING)
                ? farmer.getBlockState().getValue(CompatFarmerBlock.FACING)
                : Direction.SOUTH;
        HolderLookup.Provider registries = level.registryAccess();

        renderVillager(farmer, registries, direction, poseStack, buffer, combinedLight);
        renderCrop(farmer, registries, direction, poseStack, buffer, combinedLight, combinedOverlay);
    }

    /** Mirrors Easy Villagers FarmerRenderer villager transform exactly. */
    private void renderVillager(
            CompatFarmerBlockEntity farmer,
            HolderLookup.Provider registries,
            Direction direction,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int combinedLight
    ) {
        Villager villager = farmer.easyVillagers().getVillagerEntity(registries);
        if (villager == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 1D / 16D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-direction.toYRot()));
        poseStack.translate(0D, 0D, -4D / 16D);
        poseStack.scale(FARM_SCALE, FARM_SCALE, FARM_SCALE);
        villagerRenderer.render(villager, 0F, 1F, poseStack, buffer, combinedLight);
        poseStack.popPose();
    }

    private void renderCrop(
            CompatFarmerBlockEntity farmer,
            HolderLookup.Provider registries,
            Direction direction,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int combinedLight,
            int combinedOverlay
    ) {
        BlockState crop = farmer.easyVillagers().getCrop(registries);
        if (crop == null) {
            return;
        }

        ResourceLocation cropId = BuiltInRegistries.BLOCK.getKey(crop.getBlock());
        if (farmer.variant().isAquatic() && RICE_CROP_ID.equals(cropId)) {
            renderRice(farmer, direction, poseStack, buffer, combinedLight, combinedOverlay, crop);
        } else if (isTomatoState(crop)) {
            renderTomato(farmer, direction, poseStack, buffer, combinedLight, combinedOverlay, crop);
        } else {
            poseStack.pushPose();
            applyCropTransform(poseStack, direction, FARM_SCALE);
            renderBlockState(crop, poseStack, buffer, combinedLight, combinedOverlay);
            poseStack.popPose();
        }
    }

    /**
     * Rice has two real Farmer's Delight blocks. Both are rendered inside the same
     * 0.45-scaled crop space, so even the fully mature two-stage plant stays below
     * the glass roof instead of extending into the block above.
     */
    private void renderRice(
            CompatFarmerBlockEntity farmer,
            Direction direction,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int combinedLight,
            int combinedOverlay,
            BlockState lowerState
    ) {
        poseStack.pushPose();
        applyCropTransform(poseStack, direction, FARM_SCALE);
        renderBlockState(lowerState, poseStack, buffer, combinedLight, combinedOverlay);

        if (farmer.paddyGrowth() >= 4) {
            Block paniclesBlock = BuiltInRegistries.BLOCK.get(RICE_PANICLES_ID);
            if (paniclesBlock != Blocks.AIR) {
                poseStack.pushPose();
                poseStack.translate(0D, 1D, 0D);
                BlockState panicles = withAge(paniclesBlock.defaultBlockState(), farmer.paddyGrowth() - 4);
                renderBlockState(panicles, poseStack, buffer, combinedLight, combinedOverlay);
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

    /**
     * Tomato can contain base + Rope 1 + Rope 2. Three full 0.45 blocks would be
     * taller than the enclosure, so the complete trellis is rendered as one
     * 0.28-scale stack (3 * 0.28 = 0.84 blocks maximum height).
     */
    private void renderTomato(
            CompatFarmerBlockEntity farmer,
            Direction direction,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int combinedLight,
            int combinedOverlay,
            BlockState baseCrop
    ) {
        poseStack.pushPose();
        applyCropTransform(poseStack, direction, TOMATO_STACK_SCALE);
        renderBlockState(baseCrop, poseStack, buffer, combinedLight, combinedOverlay);

        Block tomatoOnRope = BuiltInRegistries.BLOCK.get(TOMATO_ON_ROPE_ID);

        if (farmer.ropeCount() >= 1) {
            renderTomatoSection(1, farmer.ropeOneProgress(), tomatoOnRope, poseStack, buffer, combinedLight, combinedOverlay);
        }
        if (farmer.ropeCount() >= 2) {
            renderTomatoSection(2, farmer.ropeTwoProgress(), tomatoOnRope, poseStack, buffer, combinedLight, combinedOverlay);
        }
        poseStack.popPose();
    }

    private void renderTomatoSection(
            int section,
            int progress,
            Block tomatoOnRope,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int combinedLight,
            int combinedOverlay
    ) {
        poseStack.pushPose();
        poseStack.translate(0D, section, 0D);
        if (tomatoOnRope != Blocks.AIR) {
            // Farmer's Delight has a dedicated hanging tomato block whose model already
            // includes the rope/vine relationship. Rendering Rope + Tomatoes on the
            // exact same plane caused z-fighting and black geometry in the old renderer.
            renderBlockState(withAge(tomatoOnRope.defaultBlockState(), progress), poseStack, buffer, combinedLight, combinedOverlay);
        }
        poseStack.popPose();
    }

    /** Mirrors Easy Villagers FarmerRenderer crop positioning. */
    private static void applyCropTransform(PoseStack poseStack, Direction direction, float scale) {
        poseStack.translate(0.5D, 1D / 16D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-direction.toYRot()));
        poseStack.translate(0D, 0D, 2D / 16D);
        poseStack.translate(-0.5D, 0D, -0.5D);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0.5D / scale - 0.5D, 0D, 0.5D / scale - 0.5D);
    }

    /**
     * Uses the same block-model render pipeline as Easy Villagers instead of
     * renderSingleBlock(). This preserves the crop's own RenderType and tint and
     * avoids the black/corrupted geometry seen in the previous build.
     */
    private void renderBlockState(
            BlockState state,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int combinedLight,
            int combinedOverlay
    ) {
        int color = minecraft.getBlockColors().getColor(state, null, null, 0);
        float red = ((color >> 16) & 0xFF) / 255F;
        float green = ((color >> 8) & 0xFF) / 255F;
        float blue = (color & 0xFF) / 255F;
        RenderType renderType = ItemBlockRenderTypes.getRenderType(state, false);
        blockRenderer.getModelRenderer().renderModel(
                poseStack.last(),
                buffer.getBuffer(renderType),
                state,
                blockRenderer.getBlockModel(state),
                red,
                green,
                blue,
                combinedLight,
                combinedOverlay,
                ModelData.EMPTY,
                renderType
        );
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
