package dev.celerbi.easyfarmersdelightcompat.integration.emi;

import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.stack.EmiStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Component-safe EMI transfer for the stateful Farmer upgrade recipes.
 *
 * <p>EMI 1.1.24's generic crafting transfer eventually looks up each concrete
 * requested stack with ItemStack.areItemsAndComponentsEqual. That is too strict
 * for an Ingredient such as easy_villagers:farmer: the recipe accepts the Farmer
 * by item, while the player's real stack may carry a villager/crop/machine
 * payload. This handler deliberately selects sources with Ingredient.test and
 * moves the exact source stack using normal inventory clicks.</p>
 */
public final class FarmerUpgradeEmiRecipeHandler implements EmiRecipeHandler<CraftingMenu> {
    private static final int CRAFT_START = 1;
    private static final int CRAFT_END_EXCLUSIVE = 10;
    private static final int INVENTORY_START = 10;
    private static final int INVENTORY_END_EXCLUSIVE = 46;

    @Override
    public EmiPlayerInventory getInventory(AbstractContainerScreen<CraftingMenu> screen) {
        CraftingMenu menu = screen.getMenu();
        List<EmiStack> stacks = new ArrayList<>();
        for (int slotId = CRAFT_START; slotId < INVENTORY_END_EXCLUSIVE; slotId++) {
            ItemStack stack = menu.getSlot(slotId).getItem();
            if (!stack.isEmpty()) {
                stacks.add(EmiStack.of(stack));
            }
        }
        return new EmiPlayerInventory(stacks);
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe instanceof FarmerUpgradeEmiRecipe;
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<CraftingMenu> context) {
        if (!(recipe instanceof FarmerUpgradeEmiRecipe upgrade)) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        CraftingMenu menu = context.getScreenHandler();
        if (player == null || minecraft.gameMode == null || !menu.getCarried().isEmpty()) {
            return false;
        }

        List<ItemStack> virtualInventory = snapshotPlayerInventory(menu);
        if (!virtuallyClearGrid(menu, virtualInventory)) {
            return false;
        }

        return virtuallyConsumeIngredients(upgrade.minecraftIngredients(), virtualInventory);
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<CraftingMenu> context) {
        if (!(recipe instanceof FarmerUpgradeEmiRecipe upgrade)) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        MultiPlayerGameMode gameMode = minecraft.gameMode;
        CraftingMenu menu = context.getScreenHandler();
        if (player == null || gameMode == null || !menu.getCarried().isEmpty()) {
            return false;
        }

        // Re-run the complete simulation immediately before mutating slots. This
        // avoids a partial transfer if the inventory changed while the recipe UI
        // was open.
        if (!canCraft(recipe, context)) {
            return false;
        }

        if (!clearGrid(menu, gameMode, player)) {
            return false;
        }

        List<Ingredient> ingredients = upgrade.minecraftIngredients();
        for (int recipeSlot = 0; recipeSlot < ingredients.size(); recipeSlot++) {
            Ingredient ingredient = ingredients.get(recipeSlot);
            if (ingredient.isEmpty()) {
                continue;
            }

            int sourceSlot = findSourceSlot(menu, ingredient);
            if (sourceSlot < 0) {
                return false;
            }

            int targetSlot = CRAFT_START + recipeSlot;
            if (!moveOne(menu, gameMode, player, sourceSlot, targetSlot, ingredient)) {
                return false;
            }
        }

        // The handler intentionally only fills the grid. EMI's optional
        // immediate-craft destinations are allowed to degrade to a normal fill.
        return true;
    }

    private static List<ItemStack> snapshotPlayerInventory(CraftingMenu menu) {
        List<ItemStack> virtual = new ArrayList<>(INVENTORY_END_EXCLUSIVE - INVENTORY_START);
        for (int slotId = INVENTORY_START; slotId < INVENTORY_END_EXCLUSIVE; slotId++) {
            virtual.add(menu.getSlot(slotId).getItem().copy());
        }
        return virtual;
    }

    /**
     * Simulates QUICK_MOVE of every existing crafting-grid stack into the player
     * inventory. No real slots are touched here.
     */
    private static boolean virtuallyClearGrid(CraftingMenu menu, List<ItemStack> virtualInventory) {
        for (int slotId = CRAFT_START; slotId < CRAFT_END_EXCLUSIVE; slotId++) {
            ItemStack gridStack = menu.getSlot(slotId).getItem();
            if (gridStack.isEmpty()) {
                continue;
            }
            ItemStack remaining = gridStack.copy();
            if (!insertIntoVirtualInventory(virtualInventory, remaining)) {
                return false;
            }
        }
        return true;
    }

    private static boolean insertIntoVirtualInventory(List<ItemStack> inventory, ItemStack remaining) {
        for (ItemStack target : inventory) {
            if (remaining.isEmpty()) {
                return true;
            }
            if (target.isEmpty() || !ItemStack.isSameItemSameComponents(target, remaining)) {
                continue;
            }
            int max = Math.min(target.getMaxStackSize(), remaining.getMaxStackSize());
            int room = max - target.getCount();
            if (room <= 0) {
                continue;
            }
            int moved = Math.min(room, remaining.getCount());
            target.grow(moved);
            remaining.shrink(moved);
        }

        for (int i = 0; i < inventory.size() && !remaining.isEmpty(); i++) {
            if (!inventory.get(i).isEmpty()) {
                continue;
            }
            int moved = Math.min(remaining.getCount(), remaining.getMaxStackSize());
            ItemStack placed = remaining.copy();
            placed.setCount(moved);
            inventory.set(i, placed);
            remaining.shrink(moved);
        }

        return remaining.isEmpty();
    }

    private static boolean virtuallyConsumeIngredients(List<Ingredient> ingredients, List<ItemStack> inventory) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.isEmpty()) {
                continue;
            }

            boolean found = false;
            for (ItemStack stack : inventory) {
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    stack.shrink(1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private static boolean clearGrid(CraftingMenu menu, MultiPlayerGameMode gameMode, Player player) {
        for (int slotId = CRAFT_START; slotId < CRAFT_END_EXCLUSIVE; slotId++) {
            Slot slot = menu.getSlot(slotId);
            if (slot.getItem().isEmpty()) {
                continue;
            }
            gameMode.handleInventoryMouseClick(menu.containerId, slotId, 0, ClickType.QUICK_MOVE, player);
            if (!menu.getSlot(slotId).getItem().isEmpty()) {
                return false;
            }
        }
        return menu.getCarried().isEmpty();
    }

    private static int findSourceSlot(CraftingMenu menu, Ingredient ingredient) {
        for (int slotId = INVENTORY_START; slotId < INVENTORY_END_EXCLUSIVE; slotId++) {
            ItemStack stack = menu.getSlot(slotId).getItem();
            if (!stack.isEmpty() && ingredient.test(stack)) {
                return slotId;
            }
        }
        return -1;
    }

    /** Moves one real item from source to target without reconstructing it. */
    private static boolean moveOne(
            CraftingMenu menu,
            MultiPlayerGameMode gameMode,
            Player player,
            int sourceSlot,
            int targetSlot,
            Ingredient expected
    ) {
        if (!menu.getCarried().isEmpty() || !menu.getSlot(targetSlot).getItem().isEmpty()) {
            return false;
        }

        gameMode.handleInventoryMouseClick(menu.containerId, sourceSlot, 0, ClickType.PICKUP, player);
        if (menu.getCarried().isEmpty()) {
            return false;
        }

        // Right-click places exactly one item, which is important for repeated
        // ingredients such as Glass Panes and Iron Blocks.
        gameMode.handleInventoryMouseClick(menu.containerId, targetSlot, 1, ClickType.PICKUP, player);

        // Return the rest of a larger source stack to its original slot.
        if (!menu.getCarried().isEmpty()) {
            gameMode.handleInventoryMouseClick(menu.containerId, sourceSlot, 0, ClickType.PICKUP, player);
        }

        ItemStack placed = menu.getSlot(targetSlot).getItem();
        return menu.getCarried().isEmpty() && !placed.isEmpty() && expected.test(placed);
    }
}
