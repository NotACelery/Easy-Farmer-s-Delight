package dev.celerbi.easyfarmersdelightcompat.integration;

import dev.celerbi.easyfarmersdelightcompat.EasyFarmersDelightCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Shared tool classification for Rich Farmers and the Cutter. */
public final class FarmerToolSupport {
    public static final TagKey<Item> KNIVES = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("c", "tools/knife")
    );

    public static final ResourceLocation EMPTY_KNIFE_SLOT = ResourceLocation.fromNamespaceAndPath(
            EasyFarmersDelightCompat.MOD_ID,
            "item/empty_knife_slot"
    );

    private FarmerToolSupport() {
    }

    public static boolean isKnife(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(KNIVES);
    }

    public static boolean isAxe(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(ItemTags.AXES);
    }

    public static boolean isProcessingTool(ItemStack stack) {
        return isKnife(stack) || isAxe(stack);
    }

    public static ItemStack normalizeKnife(ItemStack stack) {
        return isKnife(stack) ? stack.copyWithCount(1) : ItemStack.EMPTY;
    }
}
