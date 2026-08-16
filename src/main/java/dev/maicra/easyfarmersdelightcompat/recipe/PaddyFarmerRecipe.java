package dev.maicra.easyfarmersdelightcompat.recipe;

import dev.maicra.easyfarmersdelightcompat.registry.ModBlocks;
import dev.maicra.easyfarmersdelightcompat.registry.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * G G G
 * G F G
 * I W I
 *
 * G = Glass Pane, F = Easy Villagers Farmer, I = Iron Ingot, W = Water Bucket.
 * The vanilla crafting remainder automatically returns the empty bucket.
 */
public final class PaddyFarmerRecipe extends CustomRecipe {
    public PaddyFarmerRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (!RecipeUtil.isExact3x3(input)) {
            return false;
        }

        return RecipeUtil.isBlock(input.getItem(0, 0), Items.GLASS_PANE)
                && RecipeUtil.isBlock(input.getItem(0, 1), Items.GLASS_PANE)
                && RecipeUtil.isBlock(input.getItem(0, 2), Items.GLASS_PANE)
                && RecipeUtil.isBlock(input.getItem(1, 0), Items.GLASS_PANE)
                && RecipeUtil.isItem(input.getItem(1, 1), "easy_villagers", "farmer")
                && RecipeUtil.isBlock(input.getItem(1, 2), Items.GLASS_PANE)
                && RecipeUtil.isBlock(input.getItem(2, 0), Items.IRON_INGOT)
                && RecipeUtil.isBlock(input.getItem(2, 1), Items.WATER_BUCKET)
                && RecipeUtil.isBlock(input.getItem(2, 2), Items.IRON_INGOT);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return RecipeUtil.upgradeFarmer(input.getItem(1, 1), ModBlocks.PADDY_FARMER.get());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.PADDY_FARMER.get();
    }
}
