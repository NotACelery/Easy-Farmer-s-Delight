package dev.maicra.easyfarmersdelightcompat.blockentity;

import dev.maicra.easyfarmersdelightcompat.block.CompatFarmerBlock;
import dev.maicra.easyfarmersdelightcompat.block.FarmerVariant;
import dev.maicra.easyfarmersdelightcompat.integration.EasyVillagersFarmerAdapter;
import dev.maicra.easyfarmersdelightcompat.integration.FarmersDelightAdapter;
import dev.maicra.easyfarmersdelightcompat.registry.ModBlockEntities;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * Compatibility-safe storage for our farmer family.
 *
 * Unknown NBT is deliberately preserved. Easy Villagers' Farmer payload is kept
 * opaque and is only accessed through the narrow runtime adapter when a feature
 * actually needs it.
 */
public final class CompatFarmerBlockEntity extends BlockEntity {
    private static final String KEY_SCHEMA = "EfdcSchema";
    private static final String KEY_PADDY_GROWTH = "EfdcPaddyGrowth";
    private static final String KEY_BASE_PROGRESS = "EfdcBaseProgress";
    private static final String KEY_ROPE_ONE_PROGRESS = "EfdcRopeOneProgress";
    private static final String KEY_ROPE_TWO_PROGRESS = "EfdcRopeTwoProgress";
    private static final String KEY_ROPE_COUNT = "EfdcRopeCount";

    private static final ResourceLocation RICE_ITEM_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rice");
    private static final ResourceLocation RICE_CROP_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rice_crop");
    private static final ResourceLocation RICE_PANICLES_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rice_crop_panicles");
    private static final TagKey<Block> UNAFFECTED_BY_RICH_SOIL = TagKey.create(
            Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "unaffected_by_rich_soil")
    );

    /**
     * Virtual Farmer's Delight rice lifecycle:
     * 0..3 = submerged lower rice ages 0..3
     * 4..7 = upper panicles ages 0..3 (lower rice remains age 3)
     * next successful work cycle = harvest; the submerged lower plant stays mature at stage 3
     */
    private static final int MAX_PADDY_GROWTH = 7;

    private final EasyVillagersFarmerAdapter easyVillagers = new EasyVillagersFarmerAdapter(this);
    private final FarmersDelightAdapter farmersDelight = new FarmersDelightAdapter();

    private CompoundTag passthroughData = new CompoundTag();
    private int paddyGrowth;
    private int baseProgress;
    private int ropeOneProgress;
    private int ropeTwoProgress;
    private int ropeCount;

    public CompatFarmerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMPAT_FARMER.get(), pos, state);
    }

    public FarmerVariant variant() {
        if (getBlockState().getBlock() instanceof CompatFarmerBlock block) {
            return block.variant();
        }
        return FarmerVariant.PADDY;
    }

    public EasyVillagersFarmerAdapter easyVillagers() {
        return easyVillagers;
    }

    public CompoundTag passthroughDataCopy() {
        return passthroughData.copy();
    }

    public IItemHandler getItemHandler() {
        if (level == null) {
            return null;
        }
        return easyVillagers.getItemHandler(level.registryAccess());
    }

    public int paddyGrowth() {
        return paddyGrowth;
    }

    public int baseProgress() {
        return baseProgress;
    }

    public int ropeOneProgress() {
        return ropeOneProgress;
    }

    public int ropeTwoProgress() {
        return ropeTwoProgress;
    }

    public int ropeCount() {
        return ropeCount;
    }

    public void selectRice(HolderLookup.Provider registries) {
        easyVillagers.setRiceCrop(registries);
        paddyGrowth = 0;
        syncRiceCropState(registries);
        setChanged();
    }

    public ItemStack removeSelectedCrop(HolderLookup.Provider registries) {
        boolean rice = easyVillagers.hasRiceCrop(registries);
        ItemStack removed = easyVillagers.removeCrop(registries);
        paddyGrowth = 0;
        setChanged();

        if (rice) {
            Item riceItem = BuiltInRegistries.ITEM.get(RICE_ITEM_ID);
            return new ItemStack(riceItem);
        }
        return removed;
    }

    public void setBaseProgress(int value) {
        baseProgress = Math.max(0, value);
        setChanged();
    }

    public void setRopeProgress(int ropeIndex, int value) {
        int safe = Math.max(0, value);
        if (ropeIndex == 1) {
            ropeOneProgress = safe;
        } else if (ropeIndex == 2) {
            ropeTwoProgress = safe;
        } else {
            throw new IllegalArgumentException("Rope index must be 1 or 2");
        }
        setChanged();
    }

    public void setRopeCount(int value) {
        ropeCount = Math.max(0, Math.min(2, value));
        setChanged();
    }

    public static void serverTick(ServerLevel level, BlockPos pos, BlockState state, CompatFarmerBlockEntity farmer) {
        HolderLookup.Provider registries = level.registryAccess();

        if (!farmer.variant().isAquatic()) {
            // The Rich Farmer drives the same normal crop operation as Easy Villagers,
            // but without letting the unplaced delegate send its own block-entity sync
            // packets. Seed validation, blacklist, farmSpeed, loot and output remain EV's.
            if (farmer.easyVillagers.hasVillager(registries)) {
                farmer.easyVillagers.advanceVillagerAge(registries);
                farmer.setChanged();
            }

            if (level.getGameTime() % 20L == 0L
                    && level.random.nextInt(farmer.easyVillagers.farmSpeed()) == 0
                    && farmer.easyVillagers.ageCrop(registries)) {
                farmer.setChanged();
            }

            if (farmer.variant().isRich()) {
                farmer.tryRichSoilBoost(level, registries);
            }
            return;
        }

        // Easy Villagers advances a stored baby villager while it lives in the block.
        if (farmer.easyVillagers.hasVillager(registries)) {
            farmer.easyVillagers.advanceVillagerAge(registries);
            // The reflected villager entity lives inside the delegate, so the owner
            // must be marked dirty or baby-age progress could be lost on chunk reload.
            farmer.setChanged();
        }

        if (level.getGameTime() % 20L != 0L || !farmer.easyVillagers.hasRiceCrop(registries)) {
            return;
        }

        Villager villager = farmer.easyVillagers.getVillagerEntity(registries);
        if (villager == null || villager.isBaby() || villager.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return;
        }

        int farmSpeed = farmer.easyVillagers.farmSpeed();
        if (level.random.nextInt(farmSpeed) != 0) {
            return;
        }

        if (farmer.paddyGrowth < MAX_PADDY_GROWTH) {
            farmer.paddyGrowth++;
            farmer.syncRiceCropState(registries);
            farmer.setChanged();
            return;
        }

        farmer.harvestMatureRice(level, registries);
        // Farmer's Delight harvests the upper rice panicles without uprooting the
        // mature submerged plant. Keep the lower crop at age 3 so subsequent
        // harvests regrow only the panicles instead of restarting from a seed.
        farmer.paddyGrowth = 3;
        farmer.syncRiceCropState(registries);
        farmer.setChanged();
        level.playSound(null, pos, SoundEvents.VILLAGER_WORK_FARMER, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    /**
     * Emulates the virtual Rich Soil below the Rich Farmer.
     *
     * A real Rich Soil block is randomly ticked by Minecraft and, when selected,
     * Farmer's Delight rolls richSoilBoostChance before applying bone meal. Our crop
     * exists only as a stored BlockState, so we reproduce those two rolls and then
     * apply the crop's own bone-meal age increment without placing it in the world.
     */
    private void tryRichSoilBoost(ServerLevel level, HolderLookup.Provider registries) {
        BlockState crop = easyVillagers.getCrop(registries);
        if (crop == null || crop.is(UNAFFECTED_BY_RICH_SOIL)) {
            return;
        }

        int randomTickSpeed = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        if (randomTickSpeed <= 0) {
            return;
        }

        double boostChance = farmersDelight.richSoilBoostChance();
        if (boostChance <= 0.0D) {
            return;
        }

        // Minecraft performs randomTickSpeed draws per ticking chunk section. A real
        // Rich Soil block can therefore be selected more than once in the same game
        // tick. Replaying every draw preserves that behavior instead of collapsing it
        // into a single approximate probability.
        for (int draw = 0; draw < randomTickSpeed; draw++) {
            if (level.random.nextInt(4096) != 0 || level.random.nextDouble() > boostChance) {
                continue;
            }
            if (!applyRichSoilBoneMeal(level, registries)) {
                return;
            }
        }
    }


    private boolean applyRichSoilBoneMeal(Level level, HolderLookup.Provider registries) {
        BlockState crop = easyVillagers.getCrop(registries);
        if (crop == null || crop.is(UNAFFECTED_BY_RICH_SOIL)) {
            return false;
        }

        Optional<Property<?>> ageProperty = crop.getProperties().stream()
                .filter(property -> property.getName().equals("age"))
                .findFirst();
        if (ageProperty.isEmpty() || !(ageProperty.get() instanceof IntegerProperty integerProperty)) {
            return false;
        }

        int currentAge = crop.getValue(integerProperty);
        int maxAge = integerProperty.getPossibleValues().stream().max(Integer::compareTo).orElse(currentAge);
        if (currentAge >= maxAge) {
            return false;
        }

        int increment = getBoneMealAgeIncrease(crop.getBlock(), level);
        if (increment <= 0) {
            return false;
        }

        easyVillagers.setCropState(crop.setValue(integerProperty, Math.min(maxAge, currentAge + increment)), registries);
        setChanged();
        return true;
    }

    /**
     * CropBlock exposes its bone-meal increment as a protected method. Reflection lets
     * us respect overrides from compatible CropBlock subclasses instead of hardcoding
     * vanilla's 2..5 increment. Unsupported crop types simply receive no virtual boost.
     */
    private static int getBoneMealAgeIncrease(Block block, Level level) {
        if (!(block instanceof CropBlock)) {
            return 0;
        }

        Class<?> type = block.getClass();
        while (type != null) {
            try {
                Method method = type.getDeclaredMethod("getBonemealAgeIncrease", Level.class);
                method.setAccessible(true);
                Object result = method.invoke(block, level);
                return result instanceof Number number ? Math.max(0, number.intValue()) : 0;
            } catch (NoSuchMethodException ignored) {
                type = type.getSuperclass();
            } catch (ReflectiveOperationException | RuntimeException e) {
                return 0;
            }
        }
        return 0;
    }

    private void harvestMatureRice(ServerLevel level, HolderLookup.Provider registries) {
        Container output = easyVillagers.getOutputInventory(registries);
        if (output == null) {
            return;
        }

        Block panicles = BuiltInRegistries.BLOCK.get(RICE_PANICLES_ID);
        BlockState mature = withAge(panicles.defaultBlockState(), 3);

        LootParams.Builder context = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .withParameter(LootContextParams.BLOCK_STATE, mature)
                .withParameter(LootContextParams.TOOL, ItemStack.EMPTY);

        List<ItemStack> drops = mature.getDrops(context);
        for (ItemStack drop : drops) {
            insertIntoOutput(output, drop.copy());
        }
        output.setChanged();
    }

    private void syncRiceCropState(HolderLookup.Provider registries) {
        if (!easyVillagers.hasRiceCrop(registries)) {
            return;
        }
        Block riceCrop = BuiltInRegistries.BLOCK.get(RICE_CROP_ID);
        BlockState lower = withAge(riceCrop.defaultBlockState(), Math.min(3, paddyGrowth));
        easyVillagers.setRiceCropState(lower, registries);
    }

    private static BlockState withAge(BlockState state, int age) {
        Optional<Property<?>> ageProperty = state.getProperties().stream()
                .filter(property -> property.getName().equals("age"))
                .findFirst();
        if (ageProperty.isEmpty() || !(ageProperty.get() instanceof IntegerProperty integerProperty)) {
            return state;
        }

        int max = integerProperty.getPossibleValues().stream().max(Integer::compareTo).orElse(0);
        int safeAge = Math.max(0, Math.min(max, age));
        return state.setValue(integerProperty, safeAge);
    }

    private static void insertIntoOutput(Container output, ItemStack stack) {
        for (int slot = 0; slot < output.getContainerSize() && !stack.isEmpty(); slot++) {
            ItemStack existing = output.getItem(slot);
            if (existing.isEmpty()) {
                int move = Math.min(stack.getCount(), Math.min(stack.getMaxStackSize(), output.getMaxStackSize(stack)));
                output.setItem(slot, stack.copyWithCount(move));
                stack.shrink(move);
                continue;
            }

            if (!ItemStack.isSameItemSameComponents(existing, stack)) {
                continue;
            }

            int max = Math.min(existing.getMaxStackSize(), output.getMaxStackSize(existing));
            int room = max - existing.getCount();
            if (room <= 0) {
                continue;
            }

            int move = Math.min(room, stack.getCount());
            existing.grow(move);
            stack.shrink(move);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        passthroughData = tag.copy();
        stripMetadata(passthroughData);
        easyVillagers.reset();

        if (tag.contains(KEY_PADDY_GROWTH)) {
            paddyGrowth = Math.max(0, Math.min(MAX_PADDY_GROWTH, tag.getInt(KEY_PADDY_GROWTH)));
        } else {
            paddyGrowth = inferLegacyRiceGrowth(tag);
        }

        baseProgress = Math.max(0, tag.getInt(KEY_BASE_PROGRESS));
        ropeOneProgress = Math.max(0, tag.getInt(KEY_ROPE_ONE_PROGRESS));
        ropeTwoProgress = Math.max(0, tag.getInt(KEY_ROPE_TWO_PROGRESS));
        ropeCount = Math.max(0, Math.min(2, tag.getInt(KEY_ROPE_COUNT)));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        passthroughData = easyVillagers.snapshot(passthroughData, registries);
        CompoundTag preserved = passthroughData.copy();
        stripMetadata(preserved);
        stripAddonKeys(preserved);
        tag.merge(preserved);

        tag.putInt(KEY_SCHEMA, 2);
        tag.putInt(KEY_PADDY_GROWTH, paddyGrowth);
        tag.putInt(KEY_BASE_PROGRESS, baseProgress);
        tag.putInt(KEY_ROPE_ONE_PROGRESS, ropeOneProgress);
        tag.putInt(KEY_ROPE_TWO_PROGRESS, ropeTwoProgress);
        tag.putInt(KEY_ROPE_COUNT, ropeCount);
    }

    private static int inferLegacyRiceGrowth(CompoundTag tag) {
        if (!tag.contains("Crop")) {
            return 0;
        }
        try {
            BlockState crop = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("Crop"));
            if (!RICE_CROP_ID.equals(BuiltInRegistries.BLOCK.getKey(crop.getBlock()))) {
                return 0;
            }
            return crop.getProperties().stream()
                    .filter(property -> property.getName().equals("age"))
                    .filter(IntegerProperty.class::isInstance)
                    .map(IntegerProperty.class::cast)
                    .findFirst()
                    .map(crop::getValue)
                    .orElse(0);
        } catch (RuntimeException ignored) {
            return 0;
        }
    }

    private static void stripAddonKeys(CompoundTag tag) {
        tag.remove(KEY_SCHEMA);
        tag.remove(KEY_PADDY_GROWTH);
        tag.remove(KEY_BASE_PROGRESS);
        tag.remove(KEY_ROPE_ONE_PROGRESS);
        tag.remove(KEY_ROPE_TWO_PROGRESS);
        tag.remove(KEY_ROPE_COUNT);
    }

    private static void stripMetadata(CompoundTag tag) {
        tag.remove("id");
        tag.remove("x");
        tag.remove("y");
        tag.remove("z");
    }
}
