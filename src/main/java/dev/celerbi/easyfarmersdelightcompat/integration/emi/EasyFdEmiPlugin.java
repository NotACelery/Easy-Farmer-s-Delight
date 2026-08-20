package dev.celerbi.easyfarmersdelightcompat.integration.emi;

import dev.celerbi.easyfarmersdelightcompat.EasyFarmersDelightCompat;
import dev.celerbi.easyfarmersdelightcompat.integration.RecipeViewerData;
import dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;

@EmiEntrypoint
public final class EasyFdEmiPlugin implements EmiPlugin {
    public static final EmiRecipeCategory FARMER_HARVEST = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(EasyFarmersDelightCompat.MOD_ID, "farmer_harvest"),
            EmiStack.of(ModBlocks.RICH_FARMER_ITEM.get())
    );
    public static final EmiRecipeCategory CUTTER_AXE = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(EasyFarmersDelightCompat.MOD_ID, "cutter_axe"),
            EmiStack.of(ModBlocks.CUTTER_ITEM.get())
    );
    public static final EmiRecipeCategory BLOCK_GUIDE = new EmiRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(EasyFarmersDelightCompat.MOD_ID, "block_guide"),
            EmiStack.of(ModBlocks.RICH_FARMER_ITEM.get())
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(FARMER_HARVEST);
        registry.addCategory(CUTTER_AXE);
        registry.addCategory(BLOCK_GUIDE);

        registry.addWorkstation(FARMER_HARVEST, EmiStack.of(ModBlocks.PADDY_FARMER_ITEM.get()));
        registry.addWorkstation(FARMER_HARVEST, EmiStack.of(ModBlocks.RICH_FARMER_ITEM.get()));
        registry.addWorkstation(FARMER_HARVEST, EmiStack.of(ModBlocks.RICH_PADDY_FARMER_ITEM.get()));

        registry.addWorkstation(CUTTER_AXE, EmiStack.of(ModBlocks.CUTTER_ITEM.get()));


        registerCutting(registry);

        for (var info : RecipeViewerData.FARMER_HARVESTS) {
            registry.addRecipe(new FarmerHarvestEmiRecipe(info));
        }
        for (var info : RecipeViewerData.cutterAxeActions()) {
            registry.addRecipe(new CutterAxeEmiRecipe(info));
        }
        for (var info : RecipeViewerData.BLOCK_GUIDES) {
            registry.addRecipe(new BlockGuideEmiRecipe(info));
        }
    }

    private static void registerCutting(EmiRegistry registry) {
        Class<?> categories = load("vectorwing.farmersdelight.integration.emi.FDRecipeCategories");
        if (categories == null) return;

        try {
            var field = categories.getDeclaredField("CUTTING");
            field.trySetAccessible();
            Object value = field.get(null);
            if (value instanceof EmiRecipeCategory category) {
                registry.addWorkstation(category, EmiStack.of(ModBlocks.CUTTER_ITEM.get()));
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
        }
    }

    private static Class<?> load(String name) {
        ClassLoader context = Thread.currentThread().getContextClassLoader();
        if (context != null) {
            try {
                return Class.forName(name, true, context);
            } catch (ClassNotFoundException | LinkageError ignored) {
            }
        }
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException | LinkageError ignored) {
            return null;
        }
    }
}
