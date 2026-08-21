package dev.celerbi.easyfarmersdelightcompat.registry;

import dev.celerbi.easyfarmersdelightcompat.EasyFarmersDelightCompat;
import dev.celerbi.easyfarmersdelightcompat.block.CompatFarmerBlock;
import dev.celerbi.easyfarmersdelightcompat.block.FarmerVariant;
import dev.celerbi.easyfarmersdelightcompat.block.CutterBlock;
import dev.celerbi.easyfarmersdelightcompat.block.VillagerNoiseSwitchBlock;
import dev.celerbi.easyfarmersdelightcompat.item.CutterItem;
import dev.celerbi.easyfarmersdelightcompat.item.CompatFarmerItem;
import dev.celerbi.easyfarmersdelightcompat.item.VillagerNoiseSwitchItem;
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


    public static final DeferredBlock<CutterBlock> CUTTER = BLOCKS.registerBlock(
            "cutter",
            CutterBlock::new,
            farmerProperties()
    );

    public static final DeferredBlock<VillagerNoiseSwitchBlock> VILLAGER_NOISE_SWITCH = BLOCKS.registerBlock(
            "villager_noise_switch",
            VillagerNoiseSwitchBlock::new,
            farmerProperties()
    );

    public static final DeferredItem<CutterItem> CUTTER_ITEM = ITEMS.register(
            "cutter",
            () -> new CutterItem(CUTTER.get(), new Item.Properties())
    );

    public static final DeferredItem<VillagerNoiseSwitchItem> VILLAGER_NOISE_SWITCH_ITEM = ITEMS.register(
            "villager_noise_switch",
            () -> new VillagerNoiseSwitchItem(VILLAGER_NOISE_SWITCH.get(), new Item.Properties())
    );

    public static final DeferredItem<CompatFarmerItem> PADDY_FARMER_ITEM = ITEMS.register(
            "paddy_farmer",
            () -> new CompatFarmerItem(PADDY_FARMER.get(), new Item.Properties())
    );
    public static final DeferredItem<CompatFarmerItem> RICH_FARMER_ITEM = ITEMS.register(
            "rich_farmer",
            () -> new CompatFarmerItem(RICH_FARMER.get(), new Item.Properties())
    );
    public static final DeferredItem<CompatFarmerItem> RICH_PADDY_FARMER_ITEM = ITEMS.register(
            "rich_paddy_farmer",
            () -> new CompatFarmerItem(RICH_PADDY_FARMER.get(), new Item.Properties())
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
