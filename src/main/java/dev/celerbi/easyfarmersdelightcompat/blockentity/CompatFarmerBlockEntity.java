package dev.celerbi.easyfarmersdelightcompat.blockentity;

import dev.celerbi.easyfarmersdelightcompat.block.CompatFarmerBlock;
import dev.celerbi.easyfarmersdelightcompat.block.FarmerVariant;
import dev.celerbi.easyfarmersdelightcompat.integration.EasyVillagersFarmerAdapter;
import dev.celerbi.easyfarmersdelightcompat.integration.FarmersDelightAdapter;
import dev.celerbi.easyfarmersdelightcompat.integration.FarmerToolSupport;
import dev.celerbi.easyfarmersdelightcompat.registry.ModBlockEntities;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
    private static final String KEY_KNIFE = "EfdcKnife";
    private static final String LEGACY_ERURUU_KNIFE = "EruruuKnife";

    private static final ResourceLocation RICE_ITEM_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rice");
    private static final ResourceLocation RICE_CROP_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rice");
    private static final ResourceLocation RICE_PANICLES_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rice_panicles");
    private static final ResourceLocation TOMATO_SEEDS_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "tomato_seeds");
    private static final ResourceLocation BUDDING_TOMATO_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "budding_tomatoes");
    private static final ResourceLocation TOMATO_CROP_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "tomatoes");
    private static final ResourceLocation TOMATO_ITEM_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "tomato");
    private static final ResourceLocation ROTTEN_TOMATO_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rotten_tomato");
    private static final ResourceLocation ROPE_ITEM_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rope");
    private static final ResourceLocation RED_MUSHROOM_ITEM_ID = ResourceLocation.withDefaultNamespace("red_mushroom");
    private static final ResourceLocation BROWN_MUSHROOM_ITEM_ID = ResourceLocation.withDefaultNamespace("brown_mushroom");
    private static final ResourceLocation RED_MUSHROOM_COLONY_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "red_mushroom_colony");
    private static final ResourceLocation BROWN_MUSHROOM_COLONY_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "brown_mushroom_colony");
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
    private ItemStack knife = ItemStack.EMPTY;

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

    public ItemStack getKnife() {
        return knife.copy();
    }

    public void setKnife(ItemStack stack) {
        ItemStack normalized = variant().isRich() ? FarmerToolSupport.normalizeKnife(stack) : ItemStack.EMPTY;
        if (!ItemStack.isSameItemSameComponents(knife, normalized) || knife.getCount() != normalized.getCount()) {
            knife = normalized;
            setChanged();
        }
    }

    private ItemStack harvestTool() {
        return variant().isRich() && FarmerToolSupport.isKnife(knife) ? knife : ItemStack.EMPTY;
    }

    public boolean hasTomatoCrop(HolderLookup.Provider registries) {
        return isTomatoState(easyVillagers.getCrop(registries));
    }

    public boolean hasMushroomColony(HolderLookup.Provider registries) {
        return isMushroomColonyState(easyVillagers.getCrop(registries));
    }

    public boolean addRope() {
        if (variant() != FarmerVariant.RICH || ropeCount >= 2) {
            return false;
        }
        ropeCount++;
        if (ropeCount == 1) {
            ropeOneProgress = 0;
        } else {
            ropeTwoProgress = 0;
        }
        setChanged();
        return true;
    }

    public ItemStack removeTopRope() {
        if (ropeCount <= 0) {
            return ItemStack.EMPTY;
        }

        if (ropeCount == 2) {
            ropeTwoProgress = 0;
        } else {
            ropeOneProgress = 0;
        }
        ropeCount--;
        setChanged();

        Item rope = BuiltInRegistries.ITEM.get(ROPE_ITEM_ID);
        return new ItemStack(rope);
    }

    public void selectRice(HolderLookup.Provider registries) {
        easyVillagers.setRiceCrop(registries);
        paddyGrowth = 0;
        syncRiceCropState(registries);
        setChanged();
    }

    public void selectTomato(HolderLookup.Provider registries) {
        Block buddingTomato = BuiltInRegistries.BLOCK.get(BUDDING_TOMATO_ID);
        easyVillagers.setCropState(withAge(buddingTomato.defaultBlockState(), 0), registries);
        baseProgress = 0;
        ropeOneProgress = 0;
        ropeTwoProgress = 0;
        setChanged();
    }

    public boolean selectMushroom(ItemStack mushroomStack, HolderLookup.Provider registries) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(mushroomStack.getItem());
        ResourceLocation colonyId;
        if (RED_MUSHROOM_ITEM_ID.equals(itemId)) {
            colonyId = RED_MUSHROOM_COLONY_ID;
        } else if (BROWN_MUSHROOM_ITEM_ID.equals(itemId)) {
            colonyId = BROWN_MUSHROOM_COLONY_ID;
        } else {
            return false;
        }

        Block colony = BuiltInRegistries.BLOCK.get(colonyId);
        if (BuiltInRegistries.BLOCK.getKey(colony).equals(ResourceLocation.withDefaultNamespace("air"))) {
            return false;
        }

        easyVillagers.setCropState(withAge(colony.defaultBlockState(), 0), registries);
        baseProgress = 0;
        ropeOneProgress = 0;
        ropeTwoProgress = 0;
        ropeCount = 0;
        setChanged();
        return true;
    }

    public ItemStack removeSelectedCrop(HolderLookup.Provider registries) {
        BlockState selected = easyVillagers.getCrop(registries);
        boolean rice = selected != null && RICE_CROP_ID.equals(BuiltInRegistries.BLOCK.getKey(selected.getBlock()));
        boolean tomato = isTomatoState(selected);
        ResourceLocation mushroomItemId = mushroomItemForColony(selected);
        ItemStack removed = easyVillagers.removeCrop(registries);
        paddyGrowth = 0;
        baseProgress = 0;
        ropeOneProgress = 0;
        ropeTwoProgress = 0;
        setChanged();

        if (rice) {
            Item riceItem = BuiltInRegistries.ITEM.get(RICE_ITEM_ID);
            return new ItemStack(riceItem);
        }
        if (tomato) {
            Item tomatoSeeds = BuiltInRegistries.ITEM.get(TOMATO_SEEDS_ID);
            return new ItemStack(tomatoSeeds);
        }
        if (mushroomItemId != null) {
            Item mushroom = BuiltInRegistries.ITEM.get(mushroomItemId);
            return new ItemStack(mushroom);
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

        // Keep Easy Villagers' stored-villager aging behaviour for every variant.
        if (farmer.easyVillagers.hasVillager(registries)) {
            farmer.easyVillagers.advanceVillagerAge(registries);
            farmer.setChanged();
        }

        // Every farmer family uses Easy Villagers' one-second work cadence. The
        // configured farmSpeed remains the common speed control for base growth,
        // rope sections and virtual Rich Soil opportunities.
        if (level.getGameTime() % 20L != 0L) {
            return;
        }

        int farmSpeed = farmer.easyVillagers.farmSpeed();

        if (farmer.variant().isAquatic()) {
            if (!farmer.easyVillagers.hasRiceCrop(registries)) {
                return;
            }

            Villager villager = farmer.easyVillagers.getVillagerEntity(registries);
            boolean canHarvest = villager != null
                    && !villager.isBaby()
                    && villager.getVillagerData().getProfession() == VillagerProfession.FARMER;

            // Once the panicles are fully mature, harvesting happens on the next
            // one-second Farmer cadence without requiring a second farmSpeed RNG roll.
            // Growth itself still uses Easy Villagers' configured RNG. This prevents
            // fully-grown Rice from visually stalling for several extra work rolls.
            if (farmer.paddyGrowth >= MAX_PADDY_GROWTH && canHarvest) {
                if (farmer.harvestMatureRice(level, registries)) {
                    // Farmer's Delight keeps the mature submerged plant after the
                    // panicles are harvested, so only the upper half regrows.
                    farmer.paddyGrowth = 3;
                    farmer.syncRiceCropState(registries);
                    farmer.setChanged();
                    level.playSound(null, pos, SoundEvents.VILLAGER_WORK_FARMER, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                // A mature Paddy waits here while output is full; it must not
                // consume/reset its panicles until the complete harvest can fit.
                return;
            }

            // Normal Paddy growth roll.
            if (farmer.paddyGrowth < MAX_PADDY_GROWTH
                    && level.random.nextInt(farmSpeed) == 0) {
                farmer.paddyGrowth++;
                farmer.syncRiceCropState(registries);
                farmer.setChanged();
            }

            // Rich Paddy receives a separate opportunity at the same cadence. This
            // models Rich Soil as part of the machine instead of requiring Minecraft
            // to randomly select an imaginary Rich Soil block from a chunk section.
            if (farmer.variant().isRich()
                    && level.random.nextInt(farmSpeed) == 0) {
                farmer.tryRichPaddyBoost(level, registries);
            }
            return;
        }

        BlockState crop = farmer.easyVillagers.getCrop(registries);
        if (crop == null) {
            return;
        }

        // Base crop gets its own Easy Villagers work roll.
        if (level.random.nextInt(farmSpeed) == 0) {
            boolean changed;
            if (isTomatoState(crop)) {
                changed = farmer.ageTomato(level, registries);
            } else if (isMushroomColonyState(crop)) {
                changed = farmer.ageMushroomColony(level, registries);
            } else {
                changed = farmer.ageNormalCropSafely(level, registries);
            }
            if (changed) {
                farmer.setChanged();
            }
        }

        // Tomato rope sections are intentionally independent work rolls. Two ropes
        // installed on the same tick can therefore diverge naturally instead of
        // remaining synchronized forever.
        BlockState afterBase = farmer.easyVillagers.getCrop(registries);
        if (afterBase != null && TOMATO_CROP_ID.equals(BuiltInRegistries.BLOCK.getKey(afterBase.getBlock()))) {
            if (farmer.ropeCount >= 1 && level.random.nextInt(farmSpeed) == 0) {
                farmer.ageTomatoRopeSection(level, registries, 1);
            }
            if (farmer.ropeCount >= 2 && level.random.nextInt(farmSpeed) == 0) {
                farmer.ageTomatoRopeSection(level, registries, 2);
            }
        }

        // Rich Farmer gets a separate, farmSpeed-scaled Rich Soil opportunity. Once
        // that opportunity occurs, Farmer's Delight's own richSoilBoostChance decides
        // whether the equivalent of Bone Meal is applied.
        if (farmer.variant().isRich()
                && level.random.nextInt(farmSpeed) == 0) {
            farmer.tryRichSoilBoost(level, registries);
        }
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

        double boostChance = farmersDelight.richSoilBoostChance();
        if (boostChance <= 0.0D || level.random.nextDouble() > boostChance) {
            return;
        }

        applyRichSoilBoneMeal(level, registries);
    }

    private void tryRichPaddyBoost(ServerLevel level, HolderLookup.Provider registries) {
        if (paddyGrowth >= MAX_PADDY_GROWTH) {
            return;
        }

        double boostChance = farmersDelight.richSoilBoostChance();
        if (boostChance <= 0.0D || level.random.nextDouble() > boostChance) {
            return;
        }

        Block rice = BuiltInRegistries.BLOCK.get(RICE_CROP_ID);
        int increment = getBoneMealAgeIncrease(rice, level);
        if (increment <= 0) {
            return;
        }

        paddyGrowth = Math.min(MAX_PADDY_GROWTH, paddyGrowth + increment);
        syncRiceCropState(registries);
        setChanged();
    }

    private boolean applyRichSoilBoneMeal(Level level, HolderLookup.Provider registries) {
        BlockState crop = easyVillagers.getCrop(registries);
        if (crop == null || crop.is(UNAFFECTED_BY_RICH_SOIL)) {
            return false;
        }

        if (BUDDING_TOMATO_ID.equals(BuiltInRegistries.BLOCK.getKey(crop.getBlock()))) {
            int currentAge = getAge(crop);
            int ageGrowth = Math.min(currentAge + 1 + level.random.nextInt(4), 7);
            if (ageGrowth <= 3) {
                easyVillagers.setCropState(withAge(crop, ageGrowth), registries);
                baseProgress = ageGrowth;
            } else {
                Block tomato = BuiltInRegistries.BLOCK.get(TOMATO_CROP_ID);
                int remainingGrowth = ageGrowth - 4;
                easyVillagers.setCropState(withAge(tomato.defaultBlockState(), remainingGrowth), registries);
                baseProgress = remainingGrowth;
            }
            setChanged();
            return true;
        }

        if (TOMATO_CROP_ID.equals(BuiltInRegistries.BLOCK.getKey(crop.getBlock()))) {
            int increment = getBoneMealAgeIncrease(crop.getBlock(), level);
            if (increment <= 0) {
                return false;
            }

            int baseAge = getAge(crop);
            if (baseAge < 3) {
                int nextAge = Math.min(3, baseAge + increment);
                easyVillagers.setCropState(withAge(crop, nextAge), registries);
                baseProgress = nextAge;
                setChanged();
                return true;
            }

            if (ropeCount >= 1 && ropeOneProgress < 3) {
                ropeOneProgress = Math.min(3, ropeOneProgress + increment);
                setChanged();
                return true;
            }
            if (ropeCount >= 2 && ropeTwoProgress < 3) {
                ropeTwoProgress = Math.min(3, ropeTwoProgress + increment);
                setChanged();
                return true;
            }
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
     * Compatible crops expose their bone-meal increment as a protected method.
     * Reflection lets us respect each implementation (CropBlock, RiceBlock,
     * BuddingTomatoBlock, etc.) instead of hardcoding one arbitrary increment.
     */
    private static int getBoneMealAgeIncrease(Block block, Level level) {
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

    /**
     * Generic Easy Villagers-style crop lifecycle with lossless output handling.
     * Growth is unchanged, but a mature crop only resets after its exact generated
     * loot can fit completely in the four-slot output inventory.
     */
    private boolean ageNormalCropSafely(ServerLevel level, HolderLookup.Provider registries) {
        BlockState crop = easyVillagers.getCrop(registries);
        if (crop == null) {
            return false;
        }

        Optional<Property<?>> ageProperty = crop.getProperties().stream()
                .filter(property -> property.getName().equals("age"))
                .findFirst();
        if (ageProperty.isEmpty() || !(ageProperty.get() instanceof IntegerProperty integerProperty)) {
            return false;
        }

        int age = crop.getValue(integerProperty);
        int maxAge = integerProperty.getPossibleValues().stream().max(Integer::compareTo).orElse(age);
        if (age < maxAge) {
            easyVillagers.setCropState(crop.setValue(integerProperty, age + 1), registries);
            return true;
        }

        Villager villager = easyVillagers.getVillagerEntity(registries);
        if (villager == null || villager.isBaby() || villager.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return false;
        }

        Container output = easyVillagers.getOutputInventory(registries);
        if (output == null) {
            return false;
        }

        LootParams.Builder context = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .withParameter(LootContextParams.BLOCK_STATE, crop)
                .withParameter(LootContextParams.TOOL, harvestTool());
        List<ItemStack> drops = crop.getDrops(context);
        if (!canFitAll(output, drops)) {
            return false;
        }

        for (ItemStack drop : drops) {
            insertIntoOutput(output, drop.copy());
        }
        output.setChanged();
        easyVillagers.setCropState(crop.setValue(integerProperty, 0), registries);
        level.playSound(null, worldPosition, SoundEvents.VILLAGER_WORK_FARMER, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }

    /**
     * Farmer's Delight Mushroom Colony lifecycle used by the Rich Farmer. The
     * colony itself is persistent: ages 0..3 grow progressively, and a mature
     * colony is harvested like a full knife harvest (3 mushrooms at age 3)
     * before returning to age 0 without consuming another mushroom item.
     */
    private boolean ageMushroomColony(ServerLevel level, HolderLookup.Provider registries) {
        BlockState crop = easyVillagers.getCrop(registries);
        if (!isMushroomColonyState(crop)) {
            return false;
        }

        int age = getAge(crop);
        int maxAge = maxAge(crop);
        if (age < maxAge) {
            easyVillagers.setCropState(withAge(crop, age + 1), registries);
            return true;
        }

        Villager villager = easyVillagers.getVillagerEntity(registries);
        if (villager == null || villager.isBaby() || villager.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return false;
        }

        // Farmer's Delight colonies require a Knife for their mature harvest.
        // Growth is allowed to finish normally; a mature colony simply waits until
        // the Rich Farmer is equipped, matching the validated sandbox behaviour.
        if (variant().isRich() && !FarmerToolSupport.isKnife(knife)) {
            return false;
        }

        ResourceLocation mushroomItemId = mushroomItemForColony(crop);
        if (mushroomItemId == null) {
            return false;
        }

        Container output = easyVillagers.getOutputInventory(registries);
        if (output == null) {
            return false;
        }

        // MushroomColonyBlock's full knife harvest returns colonyAge mushrooms
        // and resets the persistent colony to age 0. At max age (3), that is 3.
        Item mushroom = BuiltInRegistries.ITEM.get(mushroomItemId);
        ItemStack harvest = new ItemStack(mushroom, maxAge);
        if (!canFitAll(output, List.of(harvest))) {
            return false;
        }
        insertIntoOutput(output, harvest.copy());
        output.setChanged();
        easyVillagers.setCropState(withAge(crop, 0), registries);
        level.playSound(null, worldPosition, crop.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.8F, 1.0F);
        return true;
    }

    private boolean ageTomato(ServerLevel level, HolderLookup.Provider registries) {
        BlockState crop = easyVillagers.getCrop(registries);
        if (!isTomatoState(crop)) {
            return false;
        }

        ResourceLocation cropId = BuiltInRegistries.BLOCK.getKey(crop.getBlock());
        int age = getAge(crop);

        // Farmer's Delight starts tomatoes as budding_tomatoes (age 0..3), then
        // transitions into the persistent tomatoes vine at age 0.
        if (BUDDING_TOMATO_ID.equals(cropId)) {
            if (age < 3) {
                easyVillagers.setCropState(withAge(crop, age + 1), registries);
                baseProgress = age + 1;
            } else {
                Block tomato = BuiltInRegistries.BLOCK.get(TOMATO_CROP_ID);
                easyVillagers.setCropState(withAge(tomato.defaultBlockState(), 0), registries);
                baseProgress = 0;
            }
            return true;
        }

        if (!TOMATO_CROP_ID.equals(cropId)) {
            return false;
        }

        if (age < 3) {
            easyVillagers.setCropState(withAge(crop, age + 1), registries);
            baseProgress = age + 1;
            return true;
        }

        Villager villager = easyVillagers.getVillagerEntity(registries);
        if (villager == null || villager.isBaby() || villager.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return false;
        }

        if (!harvestTomatoSection(level, registries)) {
            return false;
        }
        easyVillagers.setCropState(withAge(crop, 0), registries);
        baseProgress = 0;
        level.playSound(null, worldPosition, SoundEvents.VILLAGER_WORK_FARMER, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }

    private boolean ageTomatoRopeSection(ServerLevel level, HolderLookup.Provider registries, int ropeIndex) {
        if (ropeIndex < 1 || ropeIndex > ropeCount) {
            return false;
        }

        Villager villager = easyVillagers.getVillagerEntity(registries);
        boolean canHarvest = villager != null
                && !villager.isBaby()
                && villager.getVillagerData().getProfession() == VillagerProfession.FARMER;

        int progress = ropeIndex == 1 ? ropeOneProgress : ropeTwoProgress;
        if (progress < 3) {
            if (ropeIndex == 1) {
                ropeOneProgress++;
            } else {
                ropeTwoProgress++;
            }
            setChanged();
            return true;
        }

        if (!canHarvest) {
            return false;
        }

        if (!harvestTomatoSection(level, registries)) {
            return false;
        }
        if (ropeIndex == 1) {
            ropeOneProgress = 0;
        } else {
            ropeTwoProgress = 0;
        }
        setChanged();
        return true;
    }

    private boolean harvestTomatoSection(ServerLevel level, HolderLookup.Provider registries) {
        Container output = easyVillagers.getOutputInventory(registries);
        if (output == null) {
            return false;
        }

        List<ItemStack> drops = new ArrayList<>(2);
        Item tomato = BuiltInRegistries.ITEM.get(TOMATO_ITEM_ID);
        drops.add(new ItemStack(tomato, 1 + level.random.nextInt(2)));

        // Mirrors TomatoBlock#useWithoutItem: 5% chance for one Rotten Tomato.
        if (level.random.nextFloat() < 0.05F) {
            Item rottenTomato = BuiltInRegistries.ITEM.get(ROTTEN_TOMATO_ID);
            drops.add(new ItemStack(rottenTomato));
        }

        if (!canFitAll(output, drops)) {
            return false;
        }
        for (ItemStack drop : drops) {
            insertIntoOutput(output, drop.copy());
        }
        output.setChanged();
        return true;
    }

    private static boolean isMushroomColonyState(BlockState state) {
        return mushroomItemForColony(state) != null;
    }

    private static ResourceLocation mushroomItemForColony(BlockState state) {
        if (state == null) {
            return null;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (RED_MUSHROOM_COLONY_ID.equals(id)) {
            return RED_MUSHROOM_ITEM_ID;
        }
        if (BROWN_MUSHROOM_COLONY_ID.equals(id)) {
            return BROWN_MUSHROOM_ITEM_ID;
        }
        return null;
    }

    private static int maxAge(BlockState state) {
        return state.getProperties().stream()
                .filter(property -> property.getName().equals("age"))
                .filter(IntegerProperty.class::isInstance)
                .map(IntegerProperty.class::cast)
                .findFirst()
                .map(property -> property.getPossibleValues().stream().max(Integer::compareTo).orElse(0))
                .orElse(0);
    }

    private static boolean isTomatoState(BlockState state) {
        if (state == null) {
            return false;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return BUDDING_TOMATO_ID.equals(id) || TOMATO_CROP_ID.equals(id);
    }

    private static int getAge(BlockState state) {
        return state.getProperties().stream()
                .filter(property -> property.getName().equals("age"))
                .filter(IntegerProperty.class::isInstance)
                .map(IntegerProperty.class::cast)
                .findFirst()
                .map(state::getValue)
                .orElse(0);
    }

    private boolean harvestMatureRice(ServerLevel level, HolderLookup.Provider registries) {
        Container output = easyVillagers.getOutputInventory(registries);
        if (output == null) {
            return false;
        }

        Block panicles = BuiltInRegistries.BLOCK.get(RICE_PANICLES_ID);
        BlockState mature = withAge(panicles.defaultBlockState(), 3);

        LootParams.Builder context = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .withParameter(LootContextParams.BLOCK_STATE, mature)
                .withParameter(LootContextParams.TOOL, harvestTool());

        List<ItemStack> drops = mature.getDrops(context);
        if (!canFitAll(output, drops)) {
            return false;
        }
        for (ItemStack drop : drops) {
            insertIntoOutput(output, drop.copy());
        }
        output.setChanged();
        return true;
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

    /**
     * Simulates the same stacking rules used by insertIntoOutput without mutating
     * the real inventory. Harvesting is only committed when every generated drop
     * can be stored, so no crop output can disappear due to a full container.
     */
    private static boolean canFitAll(Container output, List<ItemStack> stacks) {
        ItemStack[] simulated = new ItemStack[output.getContainerSize()];
        for (int slot = 0; slot < simulated.length; slot++) {
            simulated[slot] = output.getItem(slot).copy();
        }

        for (ItemStack source : stacks) {
            ItemStack remaining = source.copy();
            for (int slot = 0; slot < simulated.length && !remaining.isEmpty(); slot++) {
                ItemStack existing = simulated[slot];
                if (existing.isEmpty()) {
                    int move = Math.min(
                            remaining.getCount(),
                            Math.min(remaining.getMaxStackSize(), output.getMaxStackSize(remaining))
                    );
                    simulated[slot] = remaining.copyWithCount(move);
                    remaining.shrink(move);
                    continue;
                }

                if (!ItemStack.isSameItemSameComponents(existing, remaining)) {
                    continue;
                }

                int max = Math.min(existing.getMaxStackSize(), output.getMaxStackSize(existing));
                int room = max - existing.getCount();
                if (room <= 0) {
                    continue;
                }

                int move = Math.min(room, remaining.getCount());
                existing.grow(move);
                remaining.shrink(move);
            }

            if (!remaining.isEmpty()) {
                return false;
            }
        }
        return true;
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
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
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

        CompoundTag knifeTag = null;
        if (tag.contains(KEY_KNIFE, net.minecraft.nbt.Tag.TAG_COMPOUND)) {
            knifeTag = tag.getCompound(KEY_KNIFE);
        } else if (tag.contains(LEGACY_ERURUU_KNIFE, net.minecraft.nbt.Tag.TAG_COMPOUND)) {
            // One-way migration from Eruruu's sandbox storage.
            knifeTag = tag.getCompound(LEGACY_ERURUU_KNIFE);
        }
        knife = variant().isRich() && knifeTag != null
                ? FarmerToolSupport.normalizeKnife(ItemStack.parseOptional(registries, knifeTag))
                : ItemStack.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        passthroughData = easyVillagers.snapshot(passthroughData, registries);
        CompoundTag preserved = passthroughData.copy();
        stripMetadata(preserved);
        stripAddonKeys(preserved);
        tag.merge(preserved);

        tag.putInt(KEY_SCHEMA, 3);
        tag.putInt(KEY_PADDY_GROWTH, paddyGrowth);
        tag.putInt(KEY_BASE_PROGRESS, baseProgress);
        tag.putInt(KEY_ROPE_ONE_PROGRESS, ropeOneProgress);
        tag.putInt(KEY_ROPE_TWO_PROGRESS, ropeTwoProgress);
        tag.putInt(KEY_ROPE_COUNT, ropeCount);
        tag.remove(LEGACY_ERURUU_KNIFE);
        if (variant().isRich() && FarmerToolSupport.isKnife(knife)) {
            tag.put(KEY_KNIFE, knife.save(registries));
        } else {
            tag.remove(KEY_KNIFE);
        }
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
        tag.remove(KEY_KNIFE);
        tag.remove(LEGACY_ERURUU_KNIFE);
    }

    private static void stripMetadata(CompoundTag tag) {
        tag.remove("id");
        tag.remove("x");
        tag.remove("y");
        tag.remove("z");
    }
}
