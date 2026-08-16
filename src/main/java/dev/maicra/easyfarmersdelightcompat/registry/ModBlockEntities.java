package dev.maicra.easyfarmersdelightcompat.registry;

import dev.maicra.easyfarmersdelightcompat.EasyFarmersDelightCompat;
import dev.maicra.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CompatFarmerBlockEntity>> COMPAT_FARMER = BLOCK_ENTITIES.register(
            "compat_farmer",
            () -> BlockEntityType.Builder.of(
                    CompatFarmerBlockEntity::new,
                    ModBlocks.PADDY_FARMER.get(),
                    ModBlocks.RICH_FARMER.get(),
                    ModBlocks.RICH_PADDY_FARMER.get()
            ).build(null)
    );

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                COMPAT_FARMER.get(),
                (farmer, context) -> farmer.getItemHandler()
        );
    }

    private ModBlockEntities() {
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
