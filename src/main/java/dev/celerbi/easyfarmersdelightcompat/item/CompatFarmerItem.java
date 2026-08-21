package dev.celerbi.easyfarmersdelightcompat.item;

import dev.celerbi.easyfarmersdelightcompat.client.CompatFarmerItemRenderer;
import dev.celerbi.easyfarmersdelightcompat.block.CompatFarmerBlock;
import dev.celerbi.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/**
 * Farmer BlockItem with a state-aware inventory renderer. Empty Farmers remain
 * normal stackable items; stateful drops render their stored villager/crop.
 */
public final class CompatFarmerItem extends BlockItem {
    public CompatFarmerItem(Block block, Properties properties) {
        super(block, properties);
    }


    /**
     * One-time migration for 1.2.0 Farmer items. The old release made every Farmer
     * globally unstackable, so saved ItemStacks may not carry an explicit stack
     * limit. On first server inventory tick, real machine data is locked to one;
     * empty legacy BLOCK_ENTITY_DATA is removed so pristine Farmers can stack.
     */
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || stack.getMaxStackSize() == 1) {
            return;
        }

        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null || data.isEmpty() || !(getBlock() instanceof CompatFarmerBlock farmerBlock)) {
            return;
        }

        CompatFarmerBlockEntity probe = new CompatFarmerBlockEntity(BlockPos.ZERO, farmerBlock.defaultBlockState());
        probe.setLevel(level);
        data.loadInto(probe, level.registryAccess());
        if (probe.hasStoredContents(level.registryAccess())) {
            stack.set(DataComponents.MAX_STACK_SIZE, 1);
        } else {
            stack.remove(DataComponents.BLOCK_ENTITY_DATA);
            stack.remove(DataComponents.MAX_STACK_SIZE);
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new CompatFarmerItemRenderer();
                }
                return renderer;
            }
        });
    }
}
