package dev.celerbi.easyfarmersdelightcompat.registry;

import dev.celerbi.easyfarmersdelightcompat.EasyFarmersDelightCompat;
import dev.celerbi.easyfarmersdelightcompat.compat.easymobfarm.EasyMobFarmCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB,
            EasyFarmersDelightCompat.MOD_ID
    );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.easyfarmersdelightcompat"))
                    .icon(() -> new ItemStack(ModBlocks.PADDY_FARMER_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.PADDY_FARMER_ITEM.get());
                        output.accept(ModBlocks.RICH_FARMER_ITEM.get());
                        output.accept(ModBlocks.RICH_PADDY_FARMER_ITEM.get());
                        output.accept(ModBlocks.CUTTER_ITEM.get());
                        output.accept(ModBlocks.VILLAGER_NOISE_SWITCH_ITEM.get());
                        output.accept(ModBlocks.IRON_FARM_NOISE_SWITCH_ITEM.get());
                        if (EasyMobFarmCompat.isLoaded() && ModBlocks.EASY_MOB_FARM_NOISE_SWITCH_ITEM != null) {
                            output.accept(ModBlocks.EASY_MOB_FARM_NOISE_SWITCH_ITEM.get());
                        }
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
