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
import net.minecraft.world.level.block.Block;

/**
 * G G G
 * G F G
 * B R B
 *
 * G = Glass Pane, F = Farmer or Paddy Farmer, B = Iron Block,
 * R = Farmer's Delight Rich Soil.
 */
public final class RichFarmerRecipe extends CustomRecipe {
    public RichFarmerRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (!RecipeUtil.isExact3x3(input)) {
            return false;
        }

        ItemStack center = input.getItem(4);
        boolean validFarmer = RecipeUtil.isItem(center, "easy_villagers", "farmer")
                || center.is(ModBlocks.PADDY_FARMER_ITEM.get());

        return validFarmer
                && RecipeUtil.isBlock(input.getItem(0), Items.GLASS_PANE)
                && RecipeUtil.isBlock(input.getItem(1), Items.GLASS_PANE)
                && RecipeUtil.isBlock(input.getItem(2), Items.GLASS_PANE)
                && RecipeUtil.isBlock(input.getItem(3), Items.GLASS_PANE)
                && RecipeUtil.isBlock(input.getItem(5), Items.GLASS_PANE)
                && RecipeUtil.isBlock(input.getItem(6), Items.IRON_BLOCK)
                && RecipeUtil.isItem(input.getItem(7), "farmersdelight", "rich_soil")
                && RecipeUtil.isBlock(input.getItem(8), Items.IRON_BLOCK);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack source = input.getItem(4);
        Block target = source.is(ModBlocks.PADDY_FARMER_ITEM.get())
                ? ModBlocks.RICH_PADDY_FARMER.get()
                : ModBlocks.RICH_FARMER.get();
        return RecipeUtil.upgradeFarmer(source, target);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.RICH_FARMER.get();
    }
}
