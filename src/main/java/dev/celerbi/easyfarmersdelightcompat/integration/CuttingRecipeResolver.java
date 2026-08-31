package dev.celerbi.easyfarmersdelightcompat.integration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public final class CuttingRecipeResolver {
    private static final ResourceLocation CUTTING_TYPE = ResourceLocation.fromNamespaceAndPath("farmersdelight",
            "cutting");
    private static final String INPUT_CLASS = "vectorwing.farmersdelight.common.crafting.CuttingBoardRecipeInput";

    public record Result(ResourceLocation recipeId, List<ItemStack> outputs, Optional<SoundEvent> sound) {
        public Result {
            outputs = List.copyOf(outputs);
            sound = sound == null ? Optional.empty() : sound;
        }
    }

    private CuttingRecipeResolver() {
    }

    public static Optional<Result> resolve(Level level, ItemStack input, ItemStack tool, int fortuneLevel) {
        if (level == null || input == null || input.isEmpty() || tool == null || tool.isEmpty())
            return Optional.empty();
        try {
            Object recipeInput = createInput(input, tool);
            for (RecipeHolder<?> holder : level.getRecipeManager().getRecipes()) {
                Recipe<?> recipe = holder.value();
                if (!isCuttingRecipe(recipe))
                    continue;
                if (!matches(recipe, recipeInput, level))
                    continue;

                Method roll = findMethod(recipe.getClass(), "rollResults", 3);
                if (roll == null)
                    return Optional.empty();
                Object raw = roll.invoke(recipe, level.random, Math.max(0, fortuneLevel), null);
                if (!(raw instanceof List<?> rawList))
                    return Optional.empty();

                List<ItemStack> outputs = new ArrayList<>();
                for (Object value : rawList) {
                    if (value instanceof ItemStack stack && !stack.isEmpty())
                        outputs.add(stack.copy());
                }

                Optional<SoundEvent> sound = Optional.empty();
                Method getSound = findMethod(recipe.getClass(), "getSoundEvent", 0);
                if (getSound != null) {
                    Object value = getSound.invoke(recipe);
                    if (value instanceof Optional<?> opt && opt.orElse(null) instanceof SoundEvent event) {
                        sound = Optional.of(event);
                    }
                }
                return Optional.of(new Result(holder.id(), outputs, sound));
            }
        } catch (ReflectiveOperationException | LinkageError | RuntimeException ignored) {
        }
        return Optional.empty();
    }

    public static boolean hasMatchingRecipe(Level level, ItemStack input, Iterable<ItemStack> tools) {
        if (level == null || input == null || input.isEmpty() || tools == null)
            return false;
        try {
            List<Object> recipeInputs = new ArrayList<>();
            for (ItemStack tool : tools) {
                if (tool == null || tool.isEmpty())
                    continue;
                recipeInputs.add(createInput(input, tool));
            }
            if (recipeInputs.isEmpty())
                return false;

            for (RecipeHolder<?> holder : level.getRecipeManager().getRecipes()) {
                Recipe<?> recipe = holder.value();
                if (!isCuttingRecipe(recipe))
                    continue;
                for (Object recipeInput : recipeInputs) {
                    if (matches(recipe, recipeInput, level))
                        return true;
                }
            }
        } catch (ReflectiveOperationException | LinkageError | RuntimeException ignored) {
        }
        return false;
    }

    private static Object createInput(ItemStack input, ItemStack tool) throws ReflectiveOperationException {
        Class<?> inputClass = ReflectionCache.type(INPUT_CLASS);
        Constructor<?> ctor = ReflectionCache.constructor(inputClass, ItemStack.class, ItemStack.class);
        return ctor.newInstance(input.copyWithCount(1), tool.copyWithCount(1));
    }

    private static boolean isCuttingRecipe(Recipe<?> recipe) {
        return recipe != null && CUTTING_TYPE.equals(BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType()));
    }

    private static boolean matches(Recipe<?> recipe, Object recipeInput,
            Level level) throws ReflectiveOperationException {
        Method matches = findMethod(recipe.getClass(), "matches", 2);
        return matches != null && Boolean.TRUE.equals(matches.invoke(recipe, recipeInput, level));
    }

    private static Method findMethod(Class<?> type, String name, int count) {
        try {
            return ReflectionCache.publicMethodByArity(type, name, count);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }
}
