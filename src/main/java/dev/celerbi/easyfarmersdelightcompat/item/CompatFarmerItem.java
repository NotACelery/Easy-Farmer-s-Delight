package dev.celerbi.easyfarmersdelightcompat.item;

import dev.celerbi.easyfarmersdelightcompat.block.CompatFarmerBlock;
import dev.celerbi.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import dev.celerbi.easyfarmersdelightcompat.client.CompatFarmerItemRenderer;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/**
 * Farmer BlockItem with state-aware stacking and inventory rendering.
 *
 * <p>The canonical empty item has no persistent machine payload and therefore
 * uses the normal item stack limit. As soon as real machine state is present the
 * stack is locked to one. This same normalizer also repairs legacy/test stacks
 * that still carry an old MAX_STACK_SIZE=1 override after being emptied.</p>
 */
public final class CompatFarmerItem extends BlockItem {
    private static final ResourceLocation EASY_VILLAGERS_BLOCK_ENTITY_COMPONENT =
            ResourceLocation.fromNamespaceAndPath("easy_villagers", "block_entity");

    public CompatFarmerItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide) {
            return;
        }

        ItemStack normalized = normalizeLoadedStack(stack, level);
        if (normalized == stack) {
            return;
        }
        if (entity instanceof Player player) {
            player.getInventory().setItem(slot, normalized);
            player.getInventory().setChanged();
        }
    }

    /**
     * Normalizes a Farmer stack loaded from any addon generation.
     *
     * <p>Stateful Farmers are never rebuilt: their machine payload is probed using
     * the real block entity and they remain max-stack 1. Only semantically empty
     * stacks are recreated from the current canonical item definition, which strips
     * arbitrary legacy component patches while retaining explicit name/lore metadata.</p>
     */
    public static ItemStack normalizeLoadedStack(ItemStack stack, Level level) {
        if (stack.isEmpty() || !(stack.getItem() instanceof CompatFarmerItem farmerItem)) {
            return stack;
        }
        if (!(farmerItem.getBlock() instanceof CompatFarmerBlock farmerBlock)) {
            return stack;
        }

        removeEasyVillagersClientCache(stack);
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data != null && !data.isEmpty()) {
            try {
                CompatFarmerBlockEntity probe = new CompatFarmerBlockEntity(
                        BlockPos.ZERO,
                        farmerBlock.defaultBlockState()
                );
                probe.setLevel(level);
                data.loadInto(probe, level.registryAccess());
                if (probe.hasStoredContents(level.registryAccess())) {
                    stack.set(DataComponents.MAX_STACK_SIZE, 1);
                    return stack;
                }
            } catch (RuntimeException malformedLegacyData) {
                // Migration must always prefer preserving an unknown historical stack
                // over accidentally rebuilding it and discarding machine state.
                stack.set(DataComponents.MAX_STACK_SIZE, 1);
                return stack;
            }
        }

        ItemStack canonical = new ItemStack(stack.getItem(), stack.getCount());
        var customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            canonical.set(DataComponents.CUSTOM_NAME, customName);
        }
        var lore = stack.get(DataComponents.LORE);
        if (lore != null) {
            canonical.set(DataComponents.LORE, lore);
        }

        return ItemStack.isSameItemSameComponents(stack, canonical) ? stack : canonical;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void removeEasyVillagersClientCache(ItemStack stack) {
        DataComponentType type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(EASY_VILLAGERS_BLOCK_ENTITY_COMPONENT);
        if (type != null) {
            stack.remove(type);
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
