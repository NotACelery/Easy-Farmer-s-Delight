package dev.celerbi.easyfarmersdelightcompat.integration;

import dev.celerbi.easyfarmersdelightcompat.blockentity.VillagerNoiseSwitchBlockEntity;
import java.lang.reflect.Method;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Narrow Easy Villagers bridge for the Noise Switch.
 *
 * Unlike the Farmer integration, the switch does not need a synthetic
 * FarmerTileentity. Easy Villagers already exposes all stored-villager serialization
 * through its VillagerData component, so this adapter reflects only the two stable
 * static VillagerData methods needed to create/save the display entity. This avoids
 * coupling the switch to FarmerTileentity constructors or protected field names.
 */
public final class NoiseSwitchVillagerAdapter {
    private static final ResourceLocation VILLAGER_ITEM_ID = ResourceLocation.fromNamespaceAndPath(
            "easy_villagers",
            "villager"
    );
    private static final String VILLAGER_DATA_CLASS = "de.maxhenkel.easyvillagers.datacomponents.VillagerData";

    private final VillagerNoiseSwitchBlockEntity owner;
    private Villager cachedVillager;
    private Method createVillagerMethod;
    private Method applyToItemMethod;
    private boolean failed;

    public NoiseSwitchVillagerAdapter(VillagerNoiseSwitchBlockEntity owner) {
        this.owner = owner;
    }

    public void reset() {
        if (cachedVillager != null) cachedVillager.setTradingPlayer(null);
        cachedVillager = null;
        failed = false;
    }

    public boolean isVillagerItem(ItemStack stack) {
        return stack != null
                && !stack.isEmpty()
                && VILLAGER_ITEM_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    public Villager getVillagerEntity() {
        if (failed || owner.getStoredVillager().isEmpty()) return null;
        Level level = owner.getLevel();
        if (level == null) return null;

        if (cachedVillager != null && cachedVillager.level() == level) {
            return cachedVillager;
        }

        try {
            resolveMethods();
            ItemStack source = owner.getStoredVillager();
            Object created = createVillagerMethod.invoke(null, source, level);
            // VillagerData#getOrCreate may migrate legacy VillagerItem data on the
            // supplied stack. Persist that normalized copy back into our block entity
            // so old worlds/items do not require the conversion again every render.
            owner.updateVillagerFromAdapter(source);
            if (!(created instanceof Villager villager)) {
                throw new IllegalStateException("Easy Villagers VillagerData#createEasyVillager did not return a Villager");
            }
            cachedVillager = villager;
            return cachedVillager;
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            fail(e);
            return null;
        }
    }

    /** Mirrors Easy Villagers VillagerTileentity#advanceAge without needing a fake tile entity. */
    public boolean advanceAge() {
        Villager villager = getVillagerEntity();
        if (villager == null) return false;
        int previousAge = villager.getAge();
        int age = previousAge + 1;
        villager.setAge(age);
        return previousAge < 0 && age >= 0;
    }

    /** Writes the cached entity back into the stored Easy Villagers VillagerItem. */
    public void flushToOwner() {
        if (failed || cachedVillager == null || owner.getStoredVillager().isEmpty()) return;
        try {
            resolveMethods();
            ItemStack updated = owner.getStoredVillager();
            applyToItemMethod.invoke(null, updated, cachedVillager);
            owner.updateVillagerFromAdapter(updated);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            fail(e);
        }
    }

    private void resolveMethods() throws ReflectiveOperationException {
        if (createVillagerMethod != null && applyToItemMethod != null) return;
        Class<?> villagerData = Class.forName(VILLAGER_DATA_CLASS);
        createVillagerMethod = villagerData.getMethod("createEasyVillager", ItemStack.class, Level.class);
        applyToItemMethod = villagerData.getMethod("applyToItem", ItemStack.class, Villager.class);
    }

    private void fail(Throwable error) {
        if (!failed) {
            System.err.println("[Easy Farmer's Delight Compat] Easy Villagers VillagerData adapter failed for a Villager Noise Switch.");
            error.printStackTrace();
        }
        failed = true;
        if (cachedVillager != null) cachedVillager.setTradingPlayer(null);
        cachedVillager = null;
    }
}
