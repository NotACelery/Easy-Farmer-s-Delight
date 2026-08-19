package dev.celerbi.easyfarmersdelightcompat.registry;

import dev.celerbi.easyfarmersdelightcompat.EasyFarmersDelightCompat;
import dev.celerbi.easyfarmersdelightcompat.menu.CutterMenu;
import dev.celerbi.easyfarmersdelightcompat.menu.PaddyFarmerMenu;
import dev.celerbi.easyfarmersdelightcompat.menu.RichFarmerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = EasyFarmersDelightCompat.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModMenus {
    public static MenuType<RichFarmerMenu> RICH_FARMER;
    public static MenuType<PaddyFarmerMenu> PADDY_FARMER;
    public static MenuType<CutterMenu> CUTTER;

    private ModMenus() {}

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.MENU)) return;
        RICH_FARMER = IMenuTypeExtension.create(RichFarmerMenu::fromNetwork);
        PADDY_FARMER = IMenuTypeExtension.create(PaddyFarmerMenu::fromNetwork);
        CUTTER = IMenuTypeExtension.create(CutterMenu::fromNetwork);
        event.register(Registries.MENU, ResourceLocation.fromNamespaceAndPath(EasyFarmersDelightCompat.MOD_ID, "rich_farmer_output"), () -> RICH_FARMER);
        event.register(Registries.MENU, ResourceLocation.fromNamespaceAndPath(EasyFarmersDelightCompat.MOD_ID, "paddy_farmer_output"), () -> PADDY_FARMER);
        event.register(Registries.MENU, ResourceLocation.fromNamespaceAndPath(EasyFarmersDelightCompat.MOD_ID, "cutter"), () -> CUTTER);
    }
}
