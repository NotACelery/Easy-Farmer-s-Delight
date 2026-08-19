package dev.celerbi.easyfarmersdelightcompat.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.celerbi.easyfarmersdelightcompat.block.CutterBlock;
import dev.celerbi.easyfarmersdelightcompat.integration.CutterLogVariant;
import dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class CutterItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final BlockRenderDispatcher blockRenderer;
    public CutterItemRenderer(){super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),Minecraft.getInstance().getEntityModels());blockRenderer=Minecraft.getInstance().getBlockRenderer();}
    @Override public void renderByItem(ItemStack stack,ItemDisplayContext ctx,PoseStack pose,MultiBufferSource buffer,int light,int overlay){
        pose.pushPose();blockRenderer.renderSingleBlock(ModBlocks.CUTTER.get().defaultBlockState().setValue(CutterBlock.FACING,Direction.SOUTH),pose,buffer,light,overlay);
        Block variant=CutterLogVariant.fromStack(stack);pose.pushPose();applyWorkTransform(pose,Direction.SOUTH);blockRenderer.renderSingleBlock(variant.defaultBlockState(),pose,buffer,light,overlay);pose.popPose();pose.popPose();
    }
    private static void applyWorkTransform(PoseStack pose,Direction facing){final float scale=.45F;pose.translate(.5D,1D/16D,.5D);pose.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));pose.translate(0D,0D,2D/16D);pose.translate(-.5D,0D,-.5D);pose.scale(scale,scale,scale);pose.translate(.5D/scale-.5D,0D,.5D/scale-.5D);}
}
