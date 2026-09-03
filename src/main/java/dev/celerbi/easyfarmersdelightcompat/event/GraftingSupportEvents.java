package dev.celerbi.easyfarmersdelightcompat.event;

import dev.celerbi.easyfarmersdelightcompat.block.GraftingSupportBlock;
import dev.celerbi.easyfarmersdelightcompat.blockentity.GraftingSupportBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public final class GraftingSupportEvents {
    private GraftingSupportEvents() {
    }

    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getLevel().isClientSide
                || event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START) {
            return;
        }

        BlockPos supportPos = GraftingSupportBlock.resolveSupportPos(event.getLevel(), event.getPos());
        if (supportPos == null
                || !(event.getLevel().getBlockEntity(supportPos) instanceof GraftingSupportBlockEntity support)
                || !support.hasCanopy()) {
            return;
        }

        ItemStack canopy = support.removeCanopy();
        if (!canopy.isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(event.getEntity(), canopy);
            event.getLevel().playSound(null, supportPos, SoundEvents.GRASS_BREAK, SoundSource.BLOCKS, 0.8F, 1.0F);
        }

        // Cancel only on the server. The client-side START event must remain uncancelled so
        // vanilla still sends the action packet that reaches this handler.
        event.setCanceled(true);
    }
}
