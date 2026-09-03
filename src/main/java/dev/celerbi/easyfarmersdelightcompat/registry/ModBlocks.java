package dev.celerbi.easyfarmersdelightcompat.registry;

import dev.celerbi.easyfarmersdelightcompat.EasyFarmersDelightCompat;
import dev.celerbi.easyfarmersdelightcompat.block.CompatFarmerBlock;
import dev.celerbi.easyfarmersdelightcompat.block.CutterBlock;
import dev.celerbi.easyfarmersdelightcompat.block.EasyMobFarmNoiseSwitchBlock;
import dev.celerbi.easyfarmersdelightcompat.block.FarmerVariant;
import dev.celerbi.easyfarmersdelightcompat.block.IronFarmNoiseSwitchBlock;
import dev.celerbi.easyfarmersdelightcompat.block.GraftingCanopyBlock;
import dev.celerbi.easyfarmersdelightcompat.block.GraftingSupportBlock;
import dev.celerbi.easyfarmersdelightcompat.block.VillagerNoiseSwitchBlock;
import dev.celerbi.easyfarmersdelightcompat.compat.easymobfarm.EasyMobFarmCompat;
import dev.celerbi.easyfarmersdelightcompat.item.CompatFarmerItem;
import dev.celerbi.easyfarmersdelightcompat.item.CutterItem;
import dev.celerbi.easyfarmersdelightcompat.item.EasyMobFarmNoiseSwitchItem;
import dev.celerbi.easyfarmersdelightcompat.item.IronFarmNoiseSwitchItem;
import dev.celerbi.easyfarmersdelightcompat.item.VillagerNoiseSwitchItem;
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


    public static final DeferredBlock<GraftingSupportBlock> GRAFTING_SUPPORT = BLOCKS.registerBlock(
            "grafting_support",
            GraftingSupportBlock::new,
            graftingSupportProperties()
    );

    public static final DeferredBlock<GraftingCanopyBlock> GRAFTING_CANOPY = BLOCKS.registerBlock(
            "grafting_canopy",
            GraftingCanopyBlock::new,
            graftingCanopyProperties()
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

    public static final DeferredBlock<IronFarmNoiseSwitchBlock> IRON_FARM_NOISE_SWITCH = BLOCKS.registerBlock(
            "iron_farm_noise_switch",
            IronFarmNoiseSwitchBlock::new,
            farmerProperties()
    );

    public static final DeferredBlock<EasyMobFarmNoiseSwitchBlock> EASY_MOB_FARM_NOISE_SWITCH =
            EasyMobFarmCompat.isLoaded() ? BLOCKS.registerBlock(
                    "easy_mob_farm_noise_switch",
                    EasyMobFarmNoiseSwitchBlock::new,
                    farmerProperties()
            ) : null;

    public static final DeferredItem<CutterItem> CUTTER_ITEM = ITEMS.register(
            "cutter",
            () -> new CutterItem(CUTTER.get(), new Item.Properties())
    );

    public static final DeferredItem<VillagerNoiseSwitchItem> VILLAGER_NOISE_SWITCH_ITEM = ITEMS.register(
            "villager_noise_switch",
            () -> new VillagerNoiseSwitchItem(VILLAGER_NOISE_SWITCH.get(), new Item.Properties().stacksTo(1))
    );

    public static final DeferredItem<IronFarmNoiseSwitchItem> IRON_FARM_NOISE_SWITCH_ITEM = ITEMS.register(
            "iron_farm_noise_switch",
            () -> new IronFarmNoiseSwitchItem(IRON_FARM_NOISE_SWITCH.get(), new Item.Properties().stacksTo(1))
    );

    public static final DeferredItem<EasyMobFarmNoiseSwitchItem> EASY_MOB_FARM_NOISE_SWITCH_ITEM =
            EasyMobFarmCompat.isLoaded() ? ITEMS.register(
                    "easy_mob_farm_noise_switch",
                    () -> new EasyMobFarmNoiseSwitchItem(
                            EASY_MOB_FARM_NOISE_SWITCH.get(), new Item.Properties().stacksTo(1))
            ) : null;

    public static final DeferredItem<BlockItem> GRAFTING_SUPPORT_ITEM = ITEMS.register(
            "grafting_support",
            () -> new BlockItem(GRAFTING_SUPPORT.get(), new Item.Properties().stacksTo(64))
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

    private static BlockBehaviour.Properties graftingSupportProperties() {
        return BlockBehaviour.Properties.of()
                .strength(1.5F)
                .sound(SoundType.WOOD)
                .noOcclusion()
                .randomTicks();
    }

    private static BlockBehaviour.Properties graftingCanopyProperties() {
        return BlockBehaviour.Properties.of()
                .strength(1.5F)
                .sound(SoundType.GRASS)
                .noOcclusion()
                .noTerrainParticles()
                .noLootTable();
    }

    private ModBlocks() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ModCreativeTabs.register(bus);
    }
}
