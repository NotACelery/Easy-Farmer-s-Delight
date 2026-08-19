package dev.celerbi.easyfarmersdelightcompat.integration;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
public record FarmerHarvestInfo(ResourceLocation id, ItemStack input, Ingredient tool, List<ItemStack> outputs, Component description) {}
