package dev.celerbi.easyfarmersdelightcompat.recipe;

import dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks;
import dev.celerbi.easyfarmersdelightcompat.registry.ModRecipeSerializers;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

/** Easy Villagers Farmer -> Rich Farmer. */
public final class RichFarmerRecipe extends ShapedRecipe {
    private static final ResourceLocation EASY_FARMER = ResourceLocation.fromNamespaceAndPath("easy_villagers", "farmer");
    private static final ResourceLocation RICH_SOIL = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rich_soil");

    public RichFarmerRecipe(CraftingBookCategory category) {
        super("", category, pattern(), new ItemStack(ModBlocks.RICH_FARMER_ITEM.get()), false);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return RecipeUtil.upgradeFarmer(input.getItem(4), ModBlocks.RICH_FARMER.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.RICH_FARMER.get();
    }

    private static ShapedRecipePattern pattern() {
        return ShapedRecipePattern.of(
                Map.of(
                        'G', Ingredient.of(Items.GLASS_PANE),
                        'F', Ingredient.of(item(EASY_FARMER)),
                        'I', Ingredient.of(Items.IRON_BLOCK),
                        'R', Ingredient.of(item(RICH_SOIL))
                ),
                "GGG",
                "GFG",
                "IRI"
        );
    }

    private static Item item(ResourceLocation id) {
        return BuiltInRegistries.ITEM.get(id);
    }
}
