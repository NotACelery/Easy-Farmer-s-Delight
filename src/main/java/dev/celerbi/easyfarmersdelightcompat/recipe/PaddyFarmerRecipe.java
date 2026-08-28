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

public final class PaddyFarmerRecipe extends ShapedRecipe {
    private static final ResourceLocation EASY_FARMER = ResourceLocation.fromNamespaceAndPath("easy_villagers",
            "farmer");

    public PaddyFarmerRecipe(CraftingBookCategory category) {
        super("", category, pattern(), new ItemStack(ModBlocks.PADDY_FARMER_ITEM.get()), false);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return RecipeUtil.upgradeFarmer(input.getItem(4), ModBlocks.PADDY_FARMER.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.PADDY_FARMER.get();
    }

    private static ShapedRecipePattern pattern() {
        return ShapedRecipePattern.of(
                Map.of(
                        'G', Ingredient.of(Items.GLASS_PANE),
                        'F', Ingredient.of(item(EASY_FARMER)),
                        'I', Ingredient.of(Items.IRON_INGOT),
                        'W', Ingredient.of(Items.WATER_BUCKET)
                ),
                "GGG",
                "GFG",
                "IWI"
        );
    }

    private static Item item(ResourceLocation id) {
        return BuiltInRegistries.ITEM.get(id);
    }
}
