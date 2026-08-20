package dev.celerbi.easyfarmersdelightcompat.integration;

import dev.celerbi.easyfarmersdelightcompat.EasyFarmersDelightCompat;
import dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Single source of viewer-only Farmer/Cutter/Block Guide documentation.
 *
 * <p>Gameplay does not read this class. JEI and EMI both do, which keeps the two
 * viewers informationally identical without allowing viewer code to become a
 * second gameplay ruleset.</p>
 */
public final class RecipeViewerData {
    public static final List<FarmerHarvestInfo> FARMER_HARVESTS = List.of(
            new FarmerHarvestInfo(
                    id("farmer_harvest/rice_with_knife"),
                    ingredient("farmersdelight", "rice"),
                    Ingredient.of(FarmerToolSupport.KNIVES),
                    ToolUse.OPTIONAL,
                    false,
                    true,
                    List.of(stack("farmersdelight", "rice")),
                    Component.translatable("easyfarmersdelightcompat.viewer.farmer_harvest.rice")
            ),
            new FarmerHarvestInfo(
                    id("farmer_harvest/brown_mushroom_colony"),
                    ingredient("farmersdelight", "brown_mushroom_colony"),
                    Ingredient.of(FarmerToolSupport.KNIVES),
                    ToolUse.REQUIRED,
                    false,
                    false,
                    List.of(new ItemStack(Items.BROWN_MUSHROOM, 3)),
                    Component.translatable("easyfarmersdelightcompat.viewer.farmer_harvest.mushroom")
            ),
            new FarmerHarvestInfo(
                    id("farmer_harvest/red_mushroom_colony"),
                    ingredient("farmersdelight", "red_mushroom_colony"),
                    Ingredient.of(FarmerToolSupport.KNIVES),
                    ToolUse.REQUIRED,
                    false,
                    false,
                    List.of(new ItemStack(Items.RED_MUSHROOM, 3)),
                    Component.translatable("easyfarmersdelightcompat.viewer.farmer_harvest.mushroom")
            ),
            new FarmerHarvestInfo(
                    id("farmer_harvest/normal_crop_with_hoe"),
                    Ingredient.of(Items.CARROT, Items.POTATO, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS),
                    Ingredient.of(ItemTags.HOES),
                    ToolUse.OPTIONAL,
                    false,
                    true,
                    List.of(new ItemStack(Items.CARROT)),
                    Component.translatable("easyfarmersdelightcompat.viewer.farmer_harvest.hoe")
            ),
            new FarmerHarvestInfo(
                    id("farmer_harvest/tomato_with_hoe"),
                    ingredient("farmersdelight", "tomato_seeds"),
                    Ingredient.of(ItemTags.HOES),
                    ToolUse.OPTIONAL,
                    false,
                    true,
                    List.of(stack("farmersdelight", "tomato")),
                    Component.translatable("easyfarmersdelightcompat.viewer.farmer_harvest.tomato")
            ),
            new FarmerHarvestInfo(
                    id("farmer_harvest/melon_with_axe"),
                    Ingredient.of(Items.MELON_SEEDS),
                    Ingredient.of(ItemTags.AXES),
                    ToolUse.REQUIRED,
                    true,
                    true,
                    List.of(new ItemStack(Items.MELON_SLICE)),
                    Component.translatable("easyfarmersdelightcompat.viewer.farmer_harvest.melon")
            ),
            new FarmerHarvestInfo(
                    id("farmer_harvest/pumpkin_with_axe"),
                    Ingredient.of(Items.PUMPKIN_SEEDS),
                    Ingredient.of(ItemTags.AXES),
                    ToolUse.REQUIRED,
                    true,
                    true,
                    List.of(new ItemStack(Items.PUMPKIN)),
                    Component.translatable("easyfarmersdelightcompat.viewer.farmer_harvest.pumpkin")
            )
    );

    public static final List<BlockGuideInfo> BLOCK_GUIDES = List.of(
            new BlockGuideInfo(
                    id("block_guide/paddy_rice"),
                    Component.translatable("easyfarmersdelightcompat.viewer.guide.paddy_rice.title"),
                    List.of(
                            catalyst(
                                    Ingredient.of(ModBlocks.PADDY_FARMER_ITEM.get(), ModBlocks.RICH_PADDY_FARMER_ITEM.get()),
                                    "easyfarmersdelightcompat.viewer.label.machine"
                            ),
                            input(ingredient("farmersdelight", "rice"), "easyfarmersdelightcompat.viewer.label.rice"),
                            output(ingredient("farmersdelight", "rice_panicle"), Component.translatable("easyfarmersdelightcompat.viewer.label.rice"))
                    ),
                    lines("easyfarmersdelightcompat.viewer.guide.paddy_rice", 4)
            ),
            new BlockGuideInfo(
                    id("block_guide/paddy_sugar_cane"),
                    Component.translatable("easyfarmersdelightcompat.viewer.guide.paddy_sugar_cane.title"),
                    List.of(
                            catalyst(
                                    Ingredient.of(ModBlocks.PADDY_FARMER_ITEM.get(), ModBlocks.RICH_PADDY_FARMER_ITEM.get()),
                                    "easyfarmersdelightcompat.viewer.label.machine"
                            ),
                            input(Ingredient.of(Items.SAND), "easyfarmersdelightcompat.viewer.label.sand"),
                            input(Ingredient.of(Items.SUGAR_CANE), "easyfarmersdelightcompat.viewer.label.sugar_cane"),
                            output(Ingredient.of(new ItemStack(Items.SUGAR_CANE, 2)), Component.translatable("easyfarmersdelightcompat.viewer.label.sugar_cane"))
                    ),
                    lines("easyfarmersdelightcompat.viewer.guide.paddy_sugar_cane", 6)
            ),
            new BlockGuideInfo(
                    id("block_guide/harvest_tools"),
                    Component.translatable("easyfarmersdelightcompat.viewer.guide.harvest_tools.title"),
                    List.of(
                            catalyst(
                                    Ingredient.of(ModBlocks.RICH_FARMER_ITEM.get(), ModBlocks.RICH_PADDY_FARMER_ITEM.get()),
                                    "easyfarmersdelightcompat.viewer.label.machine"
                            ),
                            tool(Ingredient.of(FarmerToolSupport.KNIVES), "easyfarmersdelightcompat.viewer.label.knife"),
                            tool(Ingredient.of(ItemTags.HOES), "easyfarmersdelightcompat.viewer.label.hoe"),
                            tool(Ingredient.of(ItemTags.AXES), "easyfarmersdelightcompat.viewer.label.axe")
                    ),
                    lines("easyfarmersdelightcompat.viewer.guide.harvest_tools", 5)
            ),
            new BlockGuideInfo(
                    id("block_guide/rich_normal_crops"),
                    Component.translatable("easyfarmersdelightcompat.viewer.guide.rich_normal_crops.title"),
                    List.of(
                            catalyst(Ingredient.of(ModBlocks.RICH_FARMER_ITEM.get()), "easyfarmersdelightcompat.viewer.label.machine"),
                            input(
                                    Ingredient.of(Items.CARROT, Items.POTATO, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS),
                                    "easyfarmersdelightcompat.viewer.label.normal_crop"
                            ),
                            tool(Ingredient.of(ItemTags.HOES), "easyfarmersdelightcompat.viewer.label.hoe")
                    ),
                    lines("easyfarmersdelightcompat.viewer.guide.rich_normal_crops", 4)
            ),
            new BlockGuideInfo(
                    id("block_guide/rich_tomatoes_rope"),
                    Component.translatable("easyfarmersdelightcompat.viewer.guide.rich_tomatoes_rope.title"),
                    List.of(
                            catalyst(Ingredient.of(ModBlocks.RICH_FARMER_ITEM.get()), "easyfarmersdelightcompat.viewer.label.machine"),
                            input(ingredient("farmersdelight", "tomato_seeds"), "easyfarmersdelightcompat.viewer.label.tomato_seeds"),
                            input(ingredient("farmersdelight", "rope"), "easyfarmersdelightcompat.viewer.label.rope"),
                            tool(Ingredient.of(ItemTags.HOES), "easyfarmersdelightcompat.viewer.label.hoe"),
                            output(ingredient("farmersdelight", "tomato"), stack("farmersdelight", "tomato").getHoverName())
                    ),
                    lines("easyfarmersdelightcompat.viewer.guide.rich_tomatoes_rope", 5)
            ),
            new BlockGuideInfo(
                    id("block_guide/rich_mushroom_colonies"),
                    Component.translatable("easyfarmersdelightcompat.viewer.guide.rich_mushroom_colonies.title"),
                    List.of(
                            catalyst(Ingredient.of(ModBlocks.RICH_FARMER_ITEM.get()), "easyfarmersdelightcompat.viewer.label.machine"),
                            input(Ingredient.of(Items.RED_MUSHROOM, Items.BROWN_MUSHROOM), "easyfarmersdelightcompat.viewer.label.mushroom"),
                            tool(Ingredient.of(FarmerToolSupport.KNIVES), "easyfarmersdelightcompat.viewer.label.knife"),
                            output(
                                    Ingredient.of(new ItemStack(Items.RED_MUSHROOM, 3), new ItemStack(Items.BROWN_MUSHROOM, 3)),
                                    Component.translatable("easyfarmersdelightcompat.viewer.label.mushroom")
                            )
                    ),
                    lines("easyfarmersdelightcompat.viewer.guide.rich_mushroom_colonies", 5)
            ),
            new BlockGuideInfo(
                    id("block_guide/rich_melon"),
                    Component.translatable("easyfarmersdelightcompat.viewer.guide.rich_melon.title"),
                    List.of(
                            catalyst(Ingredient.of(ModBlocks.RICH_FARMER_ITEM.get()), "easyfarmersdelightcompat.viewer.label.machine"),
                            input(Ingredient.of(Items.MELON_SEEDS), "easyfarmersdelightcompat.viewer.label.melon_seeds"),
                            tool(Ingredient.of(ItemTags.AXES), "easyfarmersdelightcompat.viewer.label.axe"),
                            output(Ingredient.of(Items.MELON_SLICE, Items.MELON), new ItemStack(Items.MELON).getHoverName())
                    ),
                    lines("easyfarmersdelightcompat.viewer.guide.rich_melon", 7)
            ),
            new BlockGuideInfo(
                    id("block_guide/rich_pumpkin"),
                    Component.translatable("easyfarmersdelightcompat.viewer.guide.rich_pumpkin.title"),
                    List.of(
                            catalyst(Ingredient.of(ModBlocks.RICH_FARMER_ITEM.get()), "easyfarmersdelightcompat.viewer.label.machine"),
                            input(Ingredient.of(Items.PUMPKIN_SEEDS), "easyfarmersdelightcompat.viewer.label.pumpkin_seeds"),
                            tool(Ingredient.of(ItemTags.AXES), "easyfarmersdelightcompat.viewer.label.axe"),
                            output(Ingredient.of(Items.PUMPKIN), new ItemStack(Items.PUMPKIN).getHoverName())
                    ),
                    lines("easyfarmersdelightcompat.viewer.guide.rich_pumpkin", 7)
            ),
            new BlockGuideInfo(
                    id("block_guide/cutter"),
                    Component.translatable("easyfarmersdelightcompat.viewer.guide.cutter.title"),
                    List.of(
                            catalyst(Ingredient.of(ModBlocks.CUTTER_ITEM.get()), "easyfarmersdelightcompat.viewer.label.machine"),
                            input(ingredient("easy_villagers", "villager"), "easyfarmersdelightcompat.viewer.label.villager"),
                            input(Ingredient.of(Items.SPRUCE_LOG), "easyfarmersdelightcompat.viewer.label.cutter_input"),
                            tool(Ingredient.of(FarmerToolSupport.KNIVES), "easyfarmersdelightcompat.viewer.label.knife"),
                            tool(Ingredient.of(ItemTags.AXES), "easyfarmersdelightcompat.viewer.label.axe"),
                            output(Ingredient.of(Items.STRIPPED_SPRUCE_LOG), new ItemStack(Items.STRIPPED_SPRUCE_LOG).getHoverName())
                    ),
                    lines("easyfarmersdelightcompat.viewer.guide.cutter", 7)
            ),
            new BlockGuideInfo(
                    id("block_guide/noise_switch"),
                    Component.translatable("easyfarmersdelightcompat.viewer.guide.noise_switch.title"),
                    List.of(
                            catalyst(Ingredient.of(ModBlocks.VILLAGER_NOISE_SWITCH_ITEM.get()), "easyfarmersdelightcompat.viewer.label.machine"),
                            input(ingredient("easy_villagers", "villager"), "easyfarmersdelightcompat.viewer.label.villager")
                    ),
                    lines("easyfarmersdelightcompat.viewer.guide.noise_switch", 7)
            )
    );

    private RecipeViewerData() {
    }

    public static List<CutterAxeInfo> cutterAxeActions() {
        List<CutterAxeInfo> out = new ArrayList<>();
        ItemStack axe = new ItemStack(Items.IRON_AXE);
        Ingredient axes = Ingredient.of(ItemTags.AXES);
        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack input = item.getDefaultInstance();
            if (input.isEmpty()) continue;
            AxeActionResolver.resolve(input, axe).ifPresent(result -> {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
                String action = result.action().name().toLowerCase(Locale.ROOT);
                out.add(new CutterAxeInfo(
                        id("cutter_axe/" + action + "/" + itemId.getNamespace() + "/" + itemId.getPath()),
                        input.copyWithCount(1),
                        axes,
                        result.output().copy(),
                        Component.translatable("easyfarmersdelightcompat.viewer.cutter_axe." + action)
                ));
            });
        }
        return List.copyOf(out);
    }

    private static GuideIngredient input(Ingredient ingredient, String labelKey) {
        return input(ingredient, Component.translatable(labelKey));
    }

    private static GuideIngredient input(Ingredient ingredient, Component label) {
        return new GuideIngredient(ingredient, GuideIngredient.Role.INPUT, label);
    }

    private static GuideIngredient output(Ingredient ingredient, Component label) {
        return new GuideIngredient(ingredient, GuideIngredient.Role.OUTPUT, label);
    }

    private static GuideIngredient tool(Ingredient ingredient, String labelKey) {
        return new GuideIngredient(
                ingredient,
                GuideIngredient.Role.TOOL,
                Component.translatable(labelKey)
        );
    }

    private static GuideIngredient catalyst(Ingredient ingredient, String labelKey) {
        return new GuideIngredient(
                ingredient,
                GuideIngredient.Role.CATALYST,
                Component.translatable(labelKey)
        );
    }

    private static List<Component> lines(String baseKey, int count) {
        List<Component> lines = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            lines.add(Component.translatable(baseKey + ".line" + i));
        }
        return List.copyOf(lines);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(EasyFarmersDelightCompat.MOD_ID, path);
    }

    private static Ingredient ingredient(String namespace, String path) {
        ItemStack stack = stack(namespace, path);
        return stack.isEmpty() ? Ingredient.EMPTY : Ingredient.of(stack);
    }

    private static ItemStack stack(String namespace, String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item == null || item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item);
    }
}
