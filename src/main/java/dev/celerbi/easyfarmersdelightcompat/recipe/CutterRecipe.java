package dev.celerbi.easyfarmersdelightcompat.recipe;

import dev.celerbi.easyfarmersdelightcompat.integration.CutterLogVariant;
import dev.celerbi.easyfarmersdelightcompat.registry.ModRecipeSerializers;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public final class CutterRecipe extends ShapedRecipe {
    public CutterRecipe(CraftingBookCategory category) {
        super(
                "efdc_cutter",
                category,
                ShapedRecipePattern.of(
                        Map.of(
                                'G', Ingredient.of(Items.GLASS_PANE),
                                'C', Ingredient.of(cuttingBoard()),
                                'B', Ingredient.of(Items.BRICKS),
                                'L', Ingredient.of(ItemTags.LOGS)),
                        "GGG",
                        "GCG",
                        "BLB"),
                CutterLogVariant.createCutter(Blocks.OAK_LOG),
                false);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return input.size() >= 9
                && input.getItem(0).is(Items.GLASS_PANE)
                && input.getItem(1).is(Items.GLASS_PANE)
                && input.getItem(2).is(Items.GLASS_PANE)
                && input.getItem(3).is(Items.GLASS_PANE)
                && input.getItem(4).is(cuttingBoard())
                && input.getItem(5).is(Items.GLASS_PANE)
                && input.getItem(6).is(Items.BRICKS)
                && CutterLogVariant.isAllowed(input.getItem(7))
                && input.getItem(8).is(Items.BRICKS);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return input.size() < 8
                ? ItemStack.EMPTY
                : CutterLogVariant.createCutter(CutterLogVariant.fromIngredient(input.getItem(7)));
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return CutterLogVariant.createCutter(Blocks.OAK_LOG);
    }

    private static net.minecraft.world.item.Item cuttingBoard() {
        return BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath("farmersdelight", "cutting_board"));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CUTTER.get();
    }
}
