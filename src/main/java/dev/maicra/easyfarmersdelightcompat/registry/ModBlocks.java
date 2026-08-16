package dev.maicra.easyfarmersdelightcompat.registry;

import dev.maicra.easyfarmersdelightcompat.EasyFarmersDelightCompat;
import dev.maicra.easyfarmersdelightcompat.block.CompatFarmerBlock;
import dev.maicra.easyfarmersdelightcompat.block.FarmerVariant;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EasyFarmersDelightCompat.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EasyFarmersDelightCompat.MOD_ID);

    public static final DeferredBlock<CompatFarmerBlock> PADDY_FARMER = BLOCKS.registerBlock(
            "paddy_farmer",
            properties -> new CompatFarmerBlock(properties, FarmerVariant.PADDY),
            farmerProperties()
    );

    public static final DeferredBlock<CompatFarmerBlock> RICH_FARMER = BLOCKS.registerBlock(
            "rich_farmer",
            properties -> new CompatFarmerBlock(properties, FarmerVariant.RICH),
            farmerProperties()
    );

    public static final DeferredBlock<CompatFarmerBlock> RICH_PADDY_FARMER = BLOCKS.registerBlock(
            "rich_paddy_farmer",
            properties -> new CompatFarmerBlock(properties, FarmerVariant.RICH_PADDY),
            farmerProperties()
    );

    public static final DeferredItem<BlockItem> PADDY_FARMER_ITEM = ITEMS.registerSimpleBlockItem(
            PADDY_FARMER,
            new Item.Properties().stacksTo(1)
    );
    public static final DeferredItem<BlockItem> RICH_FARMER_ITEM = ITEMS.registerSimpleBlockItem(
            RICH_FARMER,
            new Item.Properties().stacksTo(1)
    );
    public static final DeferredItem<BlockItem> RICH_PADDY_FARMER_ITEM = ITEMS.registerSimpleBlockItem(
            RICH_PADDY_FARMER,
            new Item.Properties().stacksTo(1)
    );


    private static BlockBehaviour.Properties farmerProperties() {
        return BlockBehaviour.Properties.of()
                .strength(2.5F)
                .sound(SoundType.METAL)
                .noOcclusion();
    }

    private ModBlocks() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ModCreativeTabs.register(bus);
    }
}
