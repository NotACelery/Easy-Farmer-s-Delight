package dev.celerbi.easyfarmersdelightcompat.recipe;

import dev.celerbi.easyfarmersdelightcompat.registry.ModBlockEntities;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.component.CustomData;

final class RecipeUtil {
    private static final ResourceLocation EASY_VILLAGERS_BLOCK_ENTITY_COMPONENT =
            ResourceLocation.fromNamespaceAndPath("easy_villagers", "block_entity");

    private RecipeUtil() {
    }

    static boolean isExact3x3(CraftingInput input) {
        return input.width() == 3 && input.height() == 3;
    }

    static boolean isItem(ItemStack stack, String namespace, String path) {
        if (stack.isEmpty()) {
            return false;
        }
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return key != null && key.equals(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    static boolean isBlock(ItemStack stack, ItemLike expected) {
        return !stack.isEmpty() && stack.is(expected.asItem());
    }

    /**
     * Creates one of our Farmer items while preserving real machine state.
     * Empty source Farmers deliberately produce a clean stackable result; a
     * meaningful block-entity payload is rewritten to our type and locked to one.
     */
    static ItemStack upgradeFarmer(ItemStack source, Block target) {
        // Start from the target's canonical item instead of cloning the complete
        // source component patch. Easy Villagers can attach transient/cache
        // components to its Farmer item; copying those made clean upgrade outputs
        // compare as different stacks even though no machine state was present.
        ItemStack result = new ItemStack(target);
        result.setCount(1);

        // Preserve player-facing metadata that is safe to carry across an upgrade.
        var customName = source.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            result.set(DataComponents.CUSTOM_NAME, customName);
        }
        var lore = source.get(DataComponents.LORE);
        if (lore != null) {
            result.set(DataComponents.LORE, lore);
        }

        CustomData sourceData = source.get(DataComponents.BLOCK_ENTITY_DATA);
        if (hasMeaningfulBlockEntityData(sourceData)) {
            BlockItem.setBlockEntityData(result, ModBlockEntities.COMPAT_FARMER.get(), sourceData.copyTag());
            result.set(DataComponents.MAX_STACK_SIZE, 1);
        }

        // Defensive: never let Easy Villagers' render/network cache survive onto
        // an Easy FD Farmer even if another component-copy path is introduced later.
        removeEasyVillagersClientCache(result);
        return result;
    }

    private static boolean hasMeaningfulBlockEntityData(CustomData data) {
        if (data == null || data.isEmpty()) {
            return false;
        }

        CompoundTag tag = data.copyTag();
        tag.remove("id");
        tag.remove("x");
        tag.remove("y");
        tag.remove("z");

        // Easy Villagers can leave structurally present but semantically empty
        // Farmer payload behind (most notably an empty Items list). Treating that
        // as real machine state makes a freshly crafted Easy FD Farmer receive a
        // MAX_STACK_SIZE=1 component before it ever reaches the inventory.
        stripEmptyContainers(tag);

        // Old/current Easy FD payloads may also contain only schema/default fields.
        // Those describe an empty machine and must not poison upgrade stacking.
        tag.remove("EfdcSchema");
        removeZeroInt(tag, "EfdcPaddyGrowth");
        removeZeroInt(tag, "EfdcBaseProgress");
        removeZeroInt(tag, "EfdcRopeOneProgress");
        removeZeroInt(tag, "EfdcRopeTwoProgress");
        removeZeroInt(tag, "EfdcRopeCount");
        removeZeroInt(tag, "EfdcSugarCaneHeight");
        removeZeroInt(tag, "EfdcSugarCaneAge");
        removeFalseBoolean(tag, "EfdcFruitReady");
        removeFalseBoolean(tag, "EfdcPaddySand");
        stripEmptyContainers(tag);

        return !tag.isEmpty();
    }

    private static void stripEmptyContainers(CompoundTag tag) {
        for (String key : java.util.Set.copyOf(tag.getAllKeys())) {
            Tag value = tag.get(key);
            if (value instanceof CompoundTag compound && compound.isEmpty()) {
                tag.remove(key);
            } else if (value instanceof ListTag list && list.isEmpty()) {
                tag.remove(key);
            }
        }
    }

    private static void removeZeroInt(CompoundTag tag, String key) {
        if (tag.contains(key, Tag.TAG_ANY_NUMERIC) && tag.getInt(key) == 0) {
            tag.remove(key);
        }
    }

    private static void removeFalseBoolean(CompoundTag tag, String key) {
        if (tag.contains(key, Tag.TAG_ANY_NUMERIC) && !tag.getBoolean(key)) {
            tag.remove(key);
        }
    }

    /**
     * Easy Villagers attaches a network-only block-entity cache component while
     * rendering/inspecting Farmer items. It is not the persisted machine payload
     * and copying it to our item makes otherwise-empty Farmers compare as different
     * stacks. The real state is preserved through vanilla BLOCK_ENTITY_DATA below.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void removeEasyVillagersClientCache(ItemStack stack) {
        DataComponentType type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(EASY_VILLAGERS_BLOCK_ENTITY_COMPONENT);
        if (type != null) {
            stack.remove(type);
        }
    }
}
