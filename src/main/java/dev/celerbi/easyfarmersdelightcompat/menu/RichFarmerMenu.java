package dev.celerbi.easyfarmersdelightcompat.menu;

import dev.celerbi.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import dev.celerbi.easyfarmersdelightcompat.integration.FarmerToolSupport;
import dev.celerbi.easyfarmersdelightcompat.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Easy Villagers output-style menu plus a protected persistent Knife slot. */
public final class RichFarmerMenu extends AbstractContainerMenu {
    public static final int OUTPUT_SLOTS = 4;
    public static final int KNIFE_SLOT = 4;
    private static final int PLAYER_START = 5;
    private static final int PLAYER_END = 41;
    private final BlockPos blockPos;
    private final KnifeContainer knifeContainer;

    public static RichFarmerMenu fromNetwork(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        BlockEntity be = inventory.player.level().getBlockEntity(pos);
        return new RichFarmerMenu(id, inventory, pos, be instanceof CompatFarmerBlockEntity farmer ? farmer : null);
    }

    public RichFarmerMenu(int id, Inventory inventory, CompatFarmerBlockEntity farmer) {
        this(id, inventory, farmer.getBlockPos(), farmer);
    }

    private RichFarmerMenu(int id, Inventory inventory, BlockPos pos, CompatFarmerBlockEntity farmer) {
        super(ModMenus.RICH_FARMER, id);
        blockPos = pos;
        Container output = farmer == null ? new SimpleContainer(OUTPUT_SLOTS)
                : farmer.easyVillagers().getOutputInventory(inventory.player.level().registryAccess());
        if (output == null || output.getContainerSize() < OUTPUT_SLOTS) output = new SimpleContainer(OUTPUT_SLOTS);
        for (int i = 0; i < OUTPUT_SLOTS; i++) {
            addSlot(new Slot(output, i, 52 + i * 18, 20) {
                @Override public boolean mayPlace(ItemStack stack) { return false; }
            });
        }
        knifeContainer = new KnifeContainer(farmer);
        Slot knifeSlot = new Slot(knifeContainer, 0, 142, 20) {
            @Override public boolean mayPlace(ItemStack stack) { return FarmerToolSupport.isKnife(stack); }
            @Override public int getMaxStackSize() { return 1; }
        };
        knifeSlot.setBackground(InventoryMenu.BLOCK_ATLAS, FarmerToolSupport.EMPTY_KNIFE_SLOT);
        addSlot(knifeSlot);
        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 109));
    }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < PLAYER_START) {
            if (!moveItemStackTo(stack, PLAYER_START, PLAYER_END, true)) return ItemStack.EMPTY;
        } else if (FarmerToolSupport.isKnife(stack) && knifeContainer.getItem(0).isEmpty()) {
            knifeContainer.setItem(0, stack.copyWithCount(1));
            stack.shrink(1);
        } else return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        return original;
    }

    @Override public boolean stillValid(Player player) {
        if (player.distanceToSqr(blockPos.getX()+.5, blockPos.getY()+.5, blockPos.getZ()+.5) > 64) return false;
        BlockEntity be = player.level().getBlockEntity(blockPos);
        return be instanceof CompatFarmerBlockEntity farmer && farmer.variant().isRich();
    }

    private static final class KnifeContainer extends SimpleContainer {
        private final CompatFarmerBlockEntity farmer;
        private boolean initializing;
        KnifeContainer(CompatFarmerBlockEntity farmer) {
            super(1); this.farmer = farmer;
            if (farmer != null) { initializing = true; super.setItem(0, farmer.getKnife()); initializing = false; }
        }
        @Override public void setChanged() {
            super.setChanged();
            if (!initializing && farmer != null) farmer.setKnife(getItem(0));
        }
    }
}
