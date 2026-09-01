package dev.celerbi.easyfarmersdelightcompat.registry;

import dev.celerbi.easyfarmersdelightcompat.EasyFarmersDelightCompat;
import dev.celerbi.easyfarmersdelightcompat.recipe.CutterRecipe;
import dev.celerbi.easyfarmersdelightcompat.recipe.PaddyFarmerRecipe;
import dev.celerbi.easyfarmersdelightcompat.recipe.RichFarmerRecipe;
import dev.celerbi.easyfarmersdelightcompat.recipe.RichPaddyFarmerRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(
            BuiltInRegistries.RECIPE_SERIALIZER,
            EasyFarmersDelightCompat.MOD_ID
    );

    public static final DeferredHolder<RecipeSerializer<?>,
            RecipeSerializer<PaddyFarmerRecipe>> PADDY_FARMER = SERIALIZERS.register(
            "paddy_farmer",
            () -> new SimpleCraftingRecipeSerializer<>(PaddyFarmerRecipe::new)
    );

    public static final DeferredHolder<RecipeSerializer<?>,
            RecipeSerializer<RichFarmerRecipe>> RICH_FARMER = SERIALIZERS.register(
            "rich_farmer",
            () -> new SimpleCraftingRecipeSerializer<>(RichFarmerRecipe::new)
    );

    public static final DeferredHolder<RecipeSerializer<?>,
            RecipeSerializer<RichPaddyFarmerRecipe>> RICH_PADDY_FARMER = SERIALIZERS.register(
            "rich_paddy_farmer",
            () -> new SimpleCraftingRecipeSerializer<>(RichPaddyFarmerRecipe::new)
    );

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CutterRecipe>> CUTTER = SERIALIZERS
            .register(
            "cutter", () -> new SimpleCraftingRecipeSerializer<>(CutterRecipe::new));

    private ModRecipeSerializers() {
    }

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}
