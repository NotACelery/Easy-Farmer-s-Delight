package dev.celerbi.easyfarmersdelightcompat.registry;

import dev.celerbi.easyfarmersdelightcompat.EasyFarmersDelightCompat;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Dedicated creative tab so development blocks are always discoverable while the
 * addon is being built. Items emitted by a creative tab are also indexed by the
 * global Search Items tab.
 */
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
                    })
                    .build()
    );

    private ModCreativeTabs() {
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
