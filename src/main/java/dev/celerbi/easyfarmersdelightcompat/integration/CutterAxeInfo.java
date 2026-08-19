package dev.celerbi.easyfarmersdelightcompat.integration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
public record CutterAxeInfo(ResourceLocation id, ItemStack input, Ingredient tool, ItemStack output, Component description) {}
