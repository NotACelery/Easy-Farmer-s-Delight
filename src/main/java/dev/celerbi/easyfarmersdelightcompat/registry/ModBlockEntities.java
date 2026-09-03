package dev.celerbi.easyfarmersdelightcompat.registry;

import dev.celerbi.easyfarmersdelightcompat.EasyFarmersDelightCompat;
import dev.celerbi.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import dev.celerbi.easyfarmersdelightcompat.blockentity.CutterBlockEntity;
import dev.celerbi.easyfarmersdelightcompat.blockentity.EasyMobFarmNoiseSwitchBlockEntity;
import dev.celerbi.easyfarmersdelightcompat.blockentity.IronFarmNoiseSwitchBlockEntity;
import dev.celerbi.easyfarmersdelightcompat.blockentity.GraftingSupportBlockEntity;
import dev.celerbi.easyfarmersdelightcompat.blockentity.VillagerNoiseSwitchBlockEntity;
import dev.celerbi.easyfarmersdelightcompat.compat.easymobfarm.EasyMobFarmCompat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            EasyFarmersDelightCompat.MOD_ID
    );

    public static final DeferredHolder<BlockEntityType<?>,
            BlockEntityType<CompatFarmerBlockEntity>> COMPAT_FARMER = BLOCK_ENTITIES.register(
            "compat_farmer",
            () -> BlockEntityType.Builder.of(
                    CompatFarmerBlockEntity::new,
                    ModBlocks.PADDY_FARMER.get(),
                    ModBlocks.RICH_FARMER.get(),
                    ModBlocks.RICH_PADDY_FARMER.get()
            ).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GraftingSupportBlockEntity>> GRAFTING_SUPPORT =
            BLOCK_ENTITIES.register(
                    "grafting_support",
                    () -> BlockEntityType.Builder.of(
                            GraftingSupportBlockEntity::new,
                            ModBlocks.GRAFTING_SUPPORT.get(),
                            ModBlocks.GRAFTING_CANOPY.get()
                    ).build(null)
            );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CutterBlockEntity>> CUTTER = BLOCK_ENTITIES
            .register(
            "cutter",
            () -> BlockEntityType.Builder.of(CutterBlockEntity::new, ModBlocks.CUTTER.get()).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>,
            BlockEntityType<VillagerNoiseSwitchBlockEntity>> VILLAGER_NOISE_SWITCH = BLOCK_ENTITIES.register(
            "villager_noise_switch",
            () -> BlockEntityType.Builder.of(
                    VillagerNoiseSwitchBlockEntity::new,
                    ModBlocks.VILLAGER_NOISE_SWITCH.get()
            ).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>,
            BlockEntityType<IronFarmNoiseSwitchBlockEntity>> IRON_FARM_NOISE_SWITCH = BLOCK_ENTITIES.register(
            "iron_farm_noise_switch",
            () -> BlockEntityType.Builder.of(
                    IronFarmNoiseSwitchBlockEntity::new,
                    ModBlocks.IRON_FARM_NOISE_SWITCH.get()
            ).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>,
            BlockEntityType<EasyMobFarmNoiseSwitchBlockEntity>> EASY_MOB_FARM_NOISE_SWITCH =
            EasyMobFarmCompat.isLoaded() ? BLOCK_ENTITIES.register(
                    "easy_mob_farm_noise_switch",
                    () -> BlockEntityType.Builder.of(
                            EasyMobFarmNoiseSwitchBlockEntity::new,
                            ModBlocks.EASY_MOB_FARM_NOISE_SWITCH.get()
                    ).build(null)
            ) : null;

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                COMPAT_FARMER.get(),
                (farmer, context) -> farmer.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CUTTER.get(),
                (cutter, direction) -> cutter.getAutomationHandler(direction)
        );
    }

    private ModBlockEntities() {
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
