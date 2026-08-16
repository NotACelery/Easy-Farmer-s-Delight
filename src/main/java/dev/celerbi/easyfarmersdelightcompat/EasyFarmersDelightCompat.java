package dev.celerbi.easyfarmersdelightcompat;

import dev.celerbi.easyfarmersdelightcompat.registry.ModBlockEntities;
import dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks;
import dev.celerbi.easyfarmersdelightcompat.registry.ModRecipeSerializers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(EasyFarmersDelightCompat.MOD_ID)
public final class EasyFarmersDelightCompat {
    public static final String MOD_ID = "easyfarmersdelightcompat";

    public EasyFarmersDelightCompat(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        modEventBus.addListener(ModBlockEntities::onRegisterCapabilities);
        ModRecipeSerializers.register(modEventBus);
    }
}
