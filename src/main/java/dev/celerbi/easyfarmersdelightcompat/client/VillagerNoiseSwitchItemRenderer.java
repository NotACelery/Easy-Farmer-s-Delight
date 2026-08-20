package dev.celerbi.easyfarmersdelightcompat.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.celerbi.easyfarmersdelightcompat.block.VillagerNoiseSwitchBlock;
import dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.RedstoneSide;

/** Client-personal preview of the Villager Noise Switch item. */
public final class VillagerNoiseSwitchItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final float WORK_SCALE = 0.45F;
    private static final double FLOOR_Y = 1.0D / 16.0D + 0.002D;
    private static final double INNER_MIN = 1.0D / 16.0D;
    private static final double INNER_SIZE = 14.0D / 16.0D;
    private static final double DUST_CELL = INNER_SIZE / 3.0D;

    private final BlockRenderDispatcher blockRenderer;

    public VillagerNoiseSwitchItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void renderByItem(
            ItemStack stack,
            ItemDisplayContext context,
            PoseStack pose,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        Direction facing = Direction.SOUTH;
        boolean muted = ClientPreferences.villagersMuted();
        pose.pushPose();
        blockRenderer.renderSingleBlock(
                ModBlocks.VILLAGER_NOISE_SWITCH.get().defaultBlockState().setValue(VillagerNoiseSwitchBlock.FACING, facing),
                pose,
                buffer,
                light,
                overlay
        );
        renderDust(facing, muted, pose, buffer, light, overlay);
        renderSwitch(facing, muted, pose, buffer, light, overlay);
        pose.popPose();
    }

    private void renderDust(Direction facing, boolean muted, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        BlockState dust = Blocks.REDSTONE_WIRE.defaultBlockState()
                .setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)
                .setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE)
                .setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE)
                .setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE)
                .setValue(RedStoneWireBlock.POWER, muted ? 15 : 0);

        pose.pushPose();
        pose.translate(0.5D, 0.0D, 0.5D);
        pose.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        pose.translate(-0.5D, FLOOR_Y, -0.5D);
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                pose.pushPose();
                pose.translate(INNER_MIN + x * DUST_CELL, 0.0D, INNER_MIN + z * DUST_CELL);
                pose.scale((float) DUST_CELL, 1.0F, (float) DUST_CELL);
                blockRenderer.renderSingleBlock(dust, pose, buffer, light, overlay);
                pose.popPose();
            }
        }
        pose.popPose();
    }

    private void renderSwitch(Direction facing, boolean muted, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        pose.pushPose();
        applyWorkTransform(pose, facing);
        blockRenderer.renderSingleBlock(Blocks.IRON_BLOCK.defaultBlockState(), pose, buffer, light, overlay);
        BlockState lever = Blocks.LEVER.defaultBlockState()
                .setValue(FaceAttachedHorizontalDirectionalBlock.FACE, AttachFace.FLOOR)
                .setValue(LeverBlock.FACING, Direction.NORTH)
                .setValue(LeverBlock.POWERED, muted);
        pose.translate(0.0D, 1.0D, 0.0D);
        blockRenderer.renderSingleBlock(lever, pose, buffer, light, overlay);
        pose.popPose();
    }

    private static void applyWorkTransform(PoseStack pose, Direction facing) {
        pose.translate(0.5D, 1.0D / 16.0D, 0.5D);
        pose.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        pose.translate(0.0D, 0.0D, 2.0D / 16.0D);
        pose.translate(-0.5D, 0.0D, -0.5D);
        pose.scale(WORK_SCALE, WORK_SCALE, WORK_SCALE);
        pose.translate(0.5D / WORK_SCALE - 0.5D, 0.0D, 0.5D / WORK_SCALE - 0.5D);
    }
}
