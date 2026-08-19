package dev.celerbi.easyfarmersdelightcompat.compat.jade;

import dev.celerbi.easyfarmersdelightcompat.EasyFarmersDelightCompat;
import dev.celerbi.easyfarmersdelightcompat.block.CompatFarmerBlock;
import dev.celerbi.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import dev.celerbi.easyfarmersdelightcompat.block.CutterBlock;
import dev.celerbi.easyfarmersdelightcompat.blockentity.CutterBlockEntity;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

/**
 * Optional Jade integration. Jade's @WailaPlugin("jade") gate prevents this class
 * from being loaded when Jade is absent, so the addon remains fully standalone.
 */
@WailaPlugin("jade")
public final class EfdcJadePlugin implements IWailaPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(
            EasyFarmersDelightCompat.MOD_ID,
            "farmer_status"
    );

    private static final String ROOT = "EfdcFarmerStatus";
    private static final String CROP = "Crop";
    private static final String AGE = "Age";
    private static final String MAX_AGE = "MaxAge";
    private static final String PADDY_GROWTH = "PaddyGrowth";
    private static final String BASE_PROGRESS = "BaseProgress";
    private static final String ROPE_ONE_PROGRESS = "RopeOneProgress";
    private static final String ROPE_TWO_PROGRESS = "RopeTwoProgress";
    private static final String ROPE_COUNT = "RopeCount";
    private static final String AQUATIC = "Aquatic";
    private static final String RICH = "Rich";

    private static final ResourceLocation RICE = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rice");
    private static final ResourceLocation BUDDING_TOMATOES = ResourceLocation.fromNamespaceAndPath("farmersdelight", "budding_tomatoes");
    private static final ResourceLocation TOMATOES = ResourceLocation.fromNamespaceAndPath("farmersdelight", "tomatoes");
    private static final ResourceLocation RED_MUSHROOM_COLONY = ResourceLocation.fromNamespaceAndPath("farmersdelight", "red_mushroom_colony");
    private static final ResourceLocation BROWN_MUSHROOM_COLONY = ResourceLocation.fromNamespaceAndPath("farmersdelight", "brown_mushroom_colony");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(ServerDataProvider.INSTANCE, CompatFarmerBlockEntity.class);
        registration.registerBlockDataProvider(FarmerKnifeJadeProvider.INSTANCE, CompatFarmerBlockEntity.class);
        registration.registerBlockDataProvider(CutterJadeProvider.INSTANCE, CutterBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(ClientProvider.INSTANCE, CompatFarmerBlock.class);
        registration.registerBlockComponent(FarmerKnifeJadeProvider.INSTANCE, CompatFarmerBlock.class);
        registration.registerBlockComponent(CutterJadeProvider.INSTANCE, CutterBlock.class);
    }

    private enum ServerDataProvider implements IServerDataProvider<BlockAccessor> {
        INSTANCE;

        @Override
        public void appendServerData(CompoundTag data, BlockAccessor accessor) {
            if (!(accessor.getBlockEntity() instanceof CompatFarmerBlockEntity farmer)) {
                return;
            }

            data.put(ROOT, buildStatus(farmer, accessor.getLevel()));
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }

    private enum ClientProvider implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            CompoundTag serverData = accessor.getServerData();
            CompoundTag status;
            if (serverData.contains(ROOT)) {
                status = serverData.getCompound(ROOT);
            } else if (accessor.getBlockEntity() instanceof CompatFarmerBlockEntity farmer) {
                // Jade can run client-only. Our BlockEntity already syncs its state for the
                // in-world renderer, so use that synchronized copy when the server has no Jade.
                status = buildStatus(farmer, accessor.getLevel());
            } else {
                return;
            }
            String cropIdString = status.getString(CROP);

            if (cropIdString.isEmpty()) {
                tooltip.add(Component.translatable("jade.easyfarmersdelightcompat.crop.none")
                        .withStyle(ChatFormatting.GRAY));
            } else {
                ResourceLocation cropId = ResourceLocation.tryParse(cropIdString);
                tooltip.add(Component.translatable(
                                "jade.easyfarmersdelightcompat.crop",
                                cropName(cropId)
                        )
                        .withStyle(ChatFormatting.WHITE));

                if (cropId != null && isTomato(cropId)) {
                    appendTomatoGrowth(tooltip, status);
                } else if (status.getBoolean(AQUATIC)) {
                    tooltip.add(Component.translatable(
                                    "jade.easyfarmersdelightcompat.growth",
                                    percent(status.getInt(PADDY_GROWTH), 7)
                            )
                            .withStyle(ChatFormatting.GRAY));
                } else {
                    tooltip.add(Component.translatable(
                                    "jade.easyfarmersdelightcompat.growth",
                                    percent(status.getInt(AGE), status.getInt(MAX_AGE))
                            )
                            .withStyle(ChatFormatting.GRAY));
                }
            }

            if (status.getBoolean(RICH)) {
                tooltip.add(Component.translatable("jade.easyfarmersdelightcompat.rich_soil.active")
                        .withStyle(ChatFormatting.GREEN));
            }
        }

        @Override
        public ResourceLocation getUid() {
            return UID;
        }
    }

    private static CompoundTag buildStatus(CompatFarmerBlockEntity farmer, net.minecraft.world.level.Level level) {
        CompoundTag status = new CompoundTag();
        status.putBoolean(AQUATIC, farmer.variant().isAquatic());
        status.putBoolean(RICH, farmer.variant().isRich());
        status.putInt(PADDY_GROWTH, farmer.paddyGrowth());
        status.putInt(BASE_PROGRESS, farmer.baseProgress());
        status.putInt(ROPE_ONE_PROGRESS, farmer.ropeOneProgress());
        status.putInt(ROPE_TWO_PROGRESS, farmer.ropeTwoProgress());
        status.putInt(ROPE_COUNT, farmer.ropeCount());

        BlockState crop = farmer.easyVillagers().getCrop(level.registryAccess());
        if (crop != null) {
            ResourceLocation cropId = BuiltInRegistries.BLOCK.getKey(crop.getBlock());
            status.putString(CROP, cropId.toString());

            AgeInfo ageInfo = ageInfo(crop);
            status.putInt(AGE, ageInfo.age());
            status.putInt(MAX_AGE, ageInfo.maxAge());
        }
        return status;
    }

    private static void appendTomatoGrowth(ITooltip tooltip, CompoundTag status) {
        int base = percent(status.getInt(BASE_PROGRESS), 3);
        int ropes = Math.max(0, Math.min(2, status.getInt(ROPE_COUNT)));

        MutableComponent line;
        if (ropes >= 2) {
            line = Component.translatable(
                    "jade.easyfarmersdelightcompat.growth.tomato.two_ropes",
                    base,
                    percent(status.getInt(ROPE_ONE_PROGRESS), 3),
                    percent(status.getInt(ROPE_TWO_PROGRESS), 3)
            );
        } else if (ropes == 1) {
            line = Component.translatable(
                    "jade.easyfarmersdelightcompat.growth.tomato.one_rope",
                    base,
                    percent(status.getInt(ROPE_ONE_PROGRESS), 3)
            );
        } else {
            line = Component.translatable(
                    "jade.easyfarmersdelightcompat.growth.tomato.base",
                    base
            );
        }

        tooltip.add(line.withStyle(ChatFormatting.GRAY));
    }

    private static Component cropName(ResourceLocation cropId) {
        if (cropId == null) {
            return Component.translatable("jade.easyfarmersdelightcompat.unknown");
        }
        if (RICE.equals(cropId)) {
            return Component.translatable("item.farmersdelight.rice");
        }
        if (isTomato(cropId)) {
            return Component.translatable("item.farmersdelight.tomato");
        }
        if (RED_MUSHROOM_COLONY.equals(cropId)) {
            return Component.translatable("block.minecraft.red_mushroom");
        }
        if (BROWN_MUSHROOM_COLONY.equals(cropId)) {
            return Component.translatable("block.minecraft.brown_mushroom");
        }

        Block crop = BuiltInRegistries.BLOCK.get(cropId);
        return crop.getName();
    }

    private static boolean isTomato(ResourceLocation cropId) {
        return BUDDING_TOMATOES.equals(cropId) || TOMATOES.equals(cropId);
    }

    private static int percent(int progress, int maximum) {
        if (maximum <= 0) {
            return 0;
        }
        int safeProgress = Math.max(0, Math.min(maximum, progress));
        return Math.round((safeProgress * 100.0F) / maximum);
    }

    private static AgeInfo ageInfo(BlockState state) {
        Optional<Property<?>> ageProperty = state.getProperties().stream()
                .filter(property -> property.getName().equals("age"))
                .findFirst();
        if (ageProperty.isEmpty() || !(ageProperty.get() instanceof IntegerProperty integerProperty)) {
            return new AgeInfo(0, 0);
        }

        int age = state.getValue(integerProperty);
        int maxAge = integerProperty.getPossibleValues().stream().max(Integer::compareTo).orElse(age);
        return new AgeInfo(age, maxAge);
    }

    private record AgeInfo(int age, int maxAge) {
    }
}
