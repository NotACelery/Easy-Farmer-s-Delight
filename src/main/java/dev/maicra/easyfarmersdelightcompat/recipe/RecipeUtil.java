package dev.maicra.easyfarmersdelightcompat.recipe;

import dev.maicra.easyfarmersdelightcompat.registry.ModBlockEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.component.CustomData;

final class RecipeUtil {
    private RecipeUtil() {
    }

    static boolean isExact3x3(CraftingInput input) {
        return input.width() == 3 && input.height() == 3;
    }

    static boolean isItem(ItemStack stack, String namespace, String path) {
        if (stack.isEmpty()) {
            return false;
        }
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return key != null && key.equals(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    static boolean isBlock(ItemStack stack, ItemLike expected) {
        return !stack.isEmpty() && stack.is(expected.asItem());
    }

    /**
     * Creates one of our farmer items while preserving every data component from the
     * source Farmer/Paddy Farmer. If the source carries BLOCK_ENTITY_DATA, its payload
     * is retained and only the block-entity type id is rewritten to our own type.
     */
    static ItemStack upgradeFarmer(ItemStack source, Block target) {
        ItemStack result = new ItemStack(target);
        result.applyComponents(source.getComponentsPatch());
        result.setCount(1);

        CustomData sourceData = source.get(DataComponents.BLOCK_ENTITY_DATA);
        if (sourceData != null) {
            BlockItem.setBlockEntityData(result, ModBlockEntities.COMPAT_FARMER.get(), sourceData.copyTag());
        }

        return result;
    }
}
