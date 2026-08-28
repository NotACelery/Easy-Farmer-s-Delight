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

    static ItemStack upgradeFarmer(ItemStack source, Block target) {

        ItemStack result = new ItemStack(target);
        result.setCount(1);

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

        stripEmptyContainers(tag);

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

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void removeEasyVillagersClientCache(ItemStack stack) {
        DataComponentType type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(EASY_VILLAGERS_BLOCK_ENTITY_COMPONENT);
        if (type != null) {
            stack.remove(type);
        }
    }
}
