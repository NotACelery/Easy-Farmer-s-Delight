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

        // Easy Villagers may attach a client/network cache to Farmer stacks. It
        // is not machine persistence and must never make our otherwise-empty
        // Farmer stacks compare as different items.
        removeEasyVillagersClientCache(stack);

        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (!(getBlock() instanceof CompatFarmerBlock farmerBlock)) {
            return;
        }

        if (data == null || data.isEmpty()) {
            // Legacy 1.2.0/1.2.1 stacks can carry an arbitrary component patch
            // inherited from the old upgrade recipe even after BLOCK_ENTITY_DATA
            // and MAX_STACK_SIZE are removed. Rebuild truly empty Farmers from
            // their current canonical item definition instead of trying to guess
            // every historical residual component one by one.
            canonicalizeEmptyStack(stack, entity, slot);
            return;
        }

        CompatFarmerBlockEntity probe = new CompatFarmerBlockEntity(BlockPos.ZERO, farmerBlock.defaultBlockState());
        probe.setLevel(level);
        data.loadInto(probe, level.registryAccess());
        if (probe.hasStoredContents(level.registryAccess())) {
            stack.set(DataComponents.MAX_STACK_SIZE, 1);
        } else {
            canonicalizeEmptyStack(stack, entity, slot);
        }
    }


    /**
     * Replaces an empty legacy Farmer with a fresh canonical stack. Only explicit
     * player-facing metadata is preserved; machine/cache/default components from
     * older builds are intentionally discarded. This is what makes old empty
     * Farmers compare identically to newly crafted ones after an update.
     */
    private static void canonicalizeEmptyStack(ItemStack stack, Entity entity, int slot) {
        if (!(entity instanceof Player player)) {
            // inventoryTick is normally invoked from a Player inventory. Keep a
            // conservative fallback for any custom carrier that invokes it too.
            stack.remove(DataComponents.BLOCK_ENTITY_DATA);
            stack.remove(DataComponents.MAX_STACK_SIZE);
            removeEasyVillagersClientCache(stack);
            return;
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

        // Newly crafted/current empty Farmers already are canonical. Do not
        // replace their ItemStack object every inventory tick.
        if (ItemStack.isSameItemSameComponents(stack, canonical)) {
            return;
        }

        player.getInventory().setItem(slot, canonical);
        player.getInventory().setChanged();
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
