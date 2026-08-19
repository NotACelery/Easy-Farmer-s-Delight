package dev.celerbi.easyfarmersdelightcompat.recipe;

import dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks;
import dev.celerbi.easyfarmersdelightcompat.registry.ModRecipeSerializers;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;

/** Display-only shaped recipes; authoritative custom recipes preserve Farmer BE data. */
public final class FarmerUpgradeDisplayRecipe extends ShapedRecipe {
    public enum Variant{PADDY,RICH,RICH_PADDY}
    private static final ResourceLocation EASY_FARMER=ResourceLocation.fromNamespaceAndPath("easy_villagers","farmer");
    private static final ResourceLocation RICH_SOIL=ResourceLocation.fromNamespaceAndPath("farmersdelight","rich_soil");
    private final Variant variant;
    public FarmerUpgradeDisplayRecipe(CraftingBookCategory category,Variant variant){super("efdc_farmer_upgrade_display",category,pattern(variant),result(variant),false);this.variant=variant;}
    @Override public boolean matches(CraftingInput input,Level level){return false;}
    @Override public ItemStack assemble(CraftingInput input,HolderLookup.Provider registries){return ItemStack.EMPTY;}
    @Override public RecipeSerializer<?> getSerializer(){return switch(variant){case PADDY->ModRecipeSerializers.PADDY_FARMER_DISPLAY.get();case RICH->ModRecipeSerializers.RICH_FARMER_DISPLAY.get();case RICH_PADDY->ModRecipeSerializers.RICH_PADDY_FARMER_DISPLAY.get();};}
    private static ShapedRecipePattern pattern(Variant v){
        Ingredient glass=Ingredient.of(Items.GLASS_PANE);
        if(v==Variant.PADDY)return ShapedRecipePattern.of(Map.of('G',glass,'F',Ingredient.of(item(EASY_FARMER)),'I',Ingredient.of(Items.IRON_INGOT),'W',Ingredient.of(Items.WATER_BUCKET)),"GGG","GFG","IWI");
        Ingredient center=v==Variant.RICH?Ingredient.of(item(EASY_FARMER)):Ingredient.of(ModBlocks.PADDY_FARMER_ITEM.get());
        return ShapedRecipePattern.of(Map.of('G',glass,'F',center,'I',Ingredient.of(Items.IRON_BLOCK),'R',Ingredient.of(item(RICH_SOIL))),"GGG","GFG","IRI");
    }
    private static ItemStack result(Variant v){return switch(v){case PADDY->new ItemStack(ModBlocks.PADDY_FARMER_ITEM.get());case RICH->new ItemStack(ModBlocks.RICH_FARMER_ITEM.get());case RICH_PADDY->new ItemStack(ModBlocks.RICH_PADDY_FARMER_ITEM.get());};}
    private static Item item(ResourceLocation id){return BuiltInRegistries.ITEM.get(id);}
}
