package dev.celerbi.easyfarmersdelightcompat.integration;

import dev.celerbi.easyfarmersdelightcompat.EasyFarmersDelightCompat;
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

/** Single source of viewer-only Farmer/Cutter documentation. */
public final class RecipeViewerData {
    public static final List<FarmerHarvestInfo> FARMER_HARVESTS=List.of(
            new FarmerHarvestInfo(id("farmer_harvest/rice_with_knife"),stack("farmersdelight","rice_panicle"),Ingredient.of(FarmerToolSupport.KNIVES),List.of(stack("farmersdelight","rice")),Component.translatable("easyfarmersdelightcompat.viewer.farmer_harvest.rice")),
            new FarmerHarvestInfo(id("farmer_harvest/brown_mushroom_colony"),stack("farmersdelight","brown_mushroom_colony"),Ingredient.of(FarmerToolSupport.KNIVES),List.of(new ItemStack(Items.BROWN_MUSHROOM,3)),Component.translatable("easyfarmersdelightcompat.viewer.farmer_harvest.mushroom")),
            new FarmerHarvestInfo(id("farmer_harvest/red_mushroom_colony"),stack("farmersdelight","red_mushroom_colony"),Ingredient.of(FarmerToolSupport.KNIVES),List.of(new ItemStack(Items.RED_MUSHROOM,3)),Component.translatable("easyfarmersdelightcompat.viewer.farmer_harvest.mushroom"))
    );
    private RecipeViewerData(){}
    public static List<CutterAxeInfo> cutterAxeActions(){
        List<CutterAxeInfo> out=new ArrayList<>();ItemStack axe=new ItemStack(Items.IRON_AXE);Ingredient axes=Ingredient.of(ItemTags.AXES);
        for(Item item:BuiltInRegistries.ITEM){ItemStack input=item.getDefaultInstance();if(input.isEmpty())continue;AxeActionResolver.resolve(input,axe).ifPresent(r->{ResourceLocation iid=BuiltInRegistries.ITEM.getKey(item);String action=r.action().name().toLowerCase(Locale.ROOT);out.add(new CutterAxeInfo(id("cutter_axe/"+action+"/"+iid.getNamespace()+"/"+iid.getPath()),input.copyWithCount(1),axes,r.output().copy(),Component.translatable("easyfarmersdelightcompat.viewer.cutter_axe."+action)));});}
        return List.copyOf(out);
    }
    private static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath(EasyFarmersDelightCompat.MOD_ID,path);}
    private static ItemStack stack(String ns,String path){return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ns,path)));}
}
