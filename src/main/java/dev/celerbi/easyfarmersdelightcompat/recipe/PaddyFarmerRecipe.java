package dev.celerbi.easyfarmersdelightcompat.recipe;

import dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks;
import dev.celerbi.easyfarmersdelightcompat.registry.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public final class PaddyFarmerRecipe extends ShapedRecipe {

    public PaddyFarmerRecipe(CraftingBookCategory category) {
        super("", category, FarmerUpgradeRecipeDefinitions.paddy().pattern(), new ItemStack(ModBlocks.PADDY_FARMER_ITEM.get()), false);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return RecipeUtil.upgradeFarmer(input.getItem(4), ModBlocks.PADDY_FARMER.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.PADDY_FARMER.get();
    }

}
