package dev.celerbi.easyfarmersdelightcompat.integration.jei;

import dev.celerbi.easyfarmersdelightcompat.integration.FarmerHarvestInfo;
import dev.celerbi.easyfarmersdelightcompat.integration.ToolUse;
import dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** JEI rendering adapter for the shared FarmerHarvestInfo dataset. */
public final class FarmerHarvestJeiCategory implements IRecipeCategory<FarmerHarvestInfo> {
    private static final int WIDTH = 180;
    private static final int HEIGHT = 106;
    private final IDrawable icon;

    public FarmerHarvestJeiCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.RICH_FARMER_ITEM.get()));
    }

    @Override
    public RecipeType<FarmerHarvestInfo> getRecipeType() {
        return EasyFdJeiPlugin.FARMER_HARVEST;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.easyfarmersdelightcompat.farmer_harvest");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FarmerHarvestInfo recipe, IFocusGroup focuses) {
        builder.addInputSlot(8, 8)
                .addIngredients(recipe.input())
                .setStandardSlotBackground();

        if (recipe.hasTool()) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 48, 8)
                    .addIngredients(recipe.tool())
                    .setStandardSlotBackground()
                    .addRichTooltipCallback((slotView, tooltip) -> {
                        tooltip.add(Component.translatable(recipe.toolUse() == ToolUse.REQUIRED
                                ? "easyfarmersdelightcompat.viewer.tool.required"
                                : "easyfarmersdelightcompat.viewer.tool.optional"));
                        tooltip.add(Component.translatable(recipe.toolDamaged()
                                ? "easyfarmersdelightcompat.viewer.tool.damaged"
                                : "easyfarmersdelightcompat.viewer.tool.not_damaged"));
                    });
        }

        for (int i = 0; i < Math.min(4, recipe.outputs().size()); i++) {
            builder.addOutputSlot(104 + i * 18, 8)
                    .addItemStack(recipe.outputs().get(i))
                    .setStandardSlotBackground();
        }
    }

    @Override
    public void draw(FarmerHarvestInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        if (recipe.hasTool()) {
            graphics.drawString(font, "+", 35, 13, 0x404040, false);
        }
        graphics.drawString(font, "→", 82, 13, 0x404040, false);

        List<Component> text = new ArrayList<>();
        text.add(recipe.description());
        if (recipe.hasTool()) {
            text.add(Component.translatable(recipe.toolUse() == ToolUse.REQUIRED
                    ? "easyfarmersdelightcompat.viewer.tool.required"
                    : "easyfarmersdelightcompat.viewer.tool.optional"));
            text.add(Component.translatable(recipe.toolDamaged()
                    ? "easyfarmersdelightcompat.viewer.tool.damaged"
                    : "easyfarmersdelightcompat.viewer.tool.not_damaged"));
        }
        if (recipe.realLoot()) {
            text.add(Component.translatable("easyfarmersdelightcompat.viewer.real_loot"));
            text.add(Component.translatable("easyfarmersdelightcompat.viewer.outputs_illustrative"));
        }

        int y = 36;
        for (Component paragraph : text) {
            for (var line : font.split(paragraph, 170)) {
                if (y > HEIGHT - 9) return;
                graphics.drawString(font, line, 5, y, 0x555555, false);
                y += 9;
            }
        }
    }
}
