package dev.maicra.easyfarmersdelightcompat.block;

import dev.maicra.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import dev.maicra.easyfarmersdelightcompat.registry.ModBlockEntities;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;

/**
 * Base block for every farmer owned by this addon.
 *
 * The geometry/assets are ours. Easy Villagers is an external runtime dependency;
 * interaction with its Farmer data is isolated behind a small adapter.
 */
public final class CompatFarmerBlock extends Block implements EntityBlock {
    private static final ResourceLocation RICE_ITEM_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rice");
    private static final ResourceLocation TOMATO_SEEDS_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "tomato_seeds");
    private static final ResourceLocation ROPE_ITEM_ID = ResourceLocation.fromNamespaceAndPath("farmersdelight", "rope");

    private final FarmerVariant variant;

    public CompatFarmerBlock(Properties properties, FarmerVariant variant) {
        super(properties);
        this.variant = variant;
    }

    public FarmerVariant variant() {
        return variant;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CompatFarmerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != ModBlockEntities.COMPAT_FARMER.get()) {
            return null;
        }
        return (tickLevel, tickPos, tickState, blockEntity) -> {
            if (tickLevel instanceof ServerLevel serverLevel && blockEntity instanceof CompatFarmerBlockEntity farmer) {
                CompatFarmerBlockEntity.serverTick(serverLevel, tickPos, tickState, farmer);
            }
        };
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack heldItem,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CompatFarmerBlockEntity farmer)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        var registries = level.registryAccess();

        if (!farmer.easyVillagers().hasVillager(registries) && farmer.easyVillagers().isVillagerItem(heldItem)) {
            if (!level.isClientSide) {
                farmer.easyVillagers().insertVillager(heldItem, registries);
                consumeOne(heldItem, player);
                farmer.setChanged();
                level.playSound(null, pos, SoundEvents.VILLAGER_YES, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        // Tomato ropes are permanent infrastructure inside the Rich Farmer. Add them
        // with Rope; sneak-interaction removes the topmost rope before the crop itself.
        if (!variant.isAquatic() && variant.isRich() && farmer.hasTomatoCrop(registries)) {
            if (player.isShiftKeyDown() && farmer.ropeCount() > 0) {
                if (!level.isClientSide) {
                    ItemStack removedRope = farmer.removeTopRope();
                    if (!removedRope.isEmpty()) {
                        ItemHandlerHelper.giveItemToPlayer(player, removedRope);
                    }
                    level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 0.8F, 1.0F);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }

            if (isRope(heldItem) && farmer.ropeCount() < 2) {
                if (!level.isClientSide && farmer.addRope()) {
                    consumeOne(heldItem, player);
                    level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.8F, 1.0F);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        if (farmer.easyVillagers().getCrop(registries) == null) {
            boolean validCrop = variant.isAquatic()
                    ? isRice(heldItem)
                    : (variant.isRich() && isTomatoSeeds(heldItem))
                            || farmer.easyVillagers().isValidSeed(heldItem, registries);

            if (validCrop) {
                if (!level.isClientSide) {
                    boolean selected;
                    if (variant.isAquatic()) {
                        farmer.selectRice(registries);
                        selected = true;
                    } else if (variant.isRich() && isTomatoSeeds(heldItem)) {
                        farmer.selectTomato(registries);
                        selected = true;
                    } else {
                        selected = farmer.easyVillagers().setCropFromSeed(heldItem, registries);
                    }
                    if (selected) {
                        consumeOne(heldItem, player);
                        farmer.setChanged();
                        level.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        if (player.isShiftKeyDown() && farmer.easyVillagers().getCrop(registries) != null) {
            if (!level.isClientSide) {
                ItemStack removed = farmer.removeSelectedCrop(registries);
                if (!removed.isEmpty()) {
                    ItemHandlerHelper.giveItemToPlayer(player, removed);
                }
                level.playSound(null, pos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (player.isShiftKeyDown() && farmer.easyVillagers().hasVillager(registries)) {
            if (!level.isClientSide) {
                ItemStack villager = farmer.easyVillagers().removeVillager(registries);
                if (!villager.isEmpty()) {
                    ItemHandlerHelper.giveItemToPlayer(player, villager);
                }
                farmer.setChanged();
                level.playSound(null, pos, SoundEvents.VILLAGER_CELEBRATE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        openOutput(level, pos, player, farmer, state);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CompatFarmerBlockEntity farmer)) {
            return InteractionResult.PASS;
        }

        var registries = level.registryAccess();
        if (player.isShiftKeyDown() && !variant.isAquatic() && variant.isRich()
                && farmer.hasTomatoCrop(registries) && farmer.ropeCount() > 0) {
            if (!level.isClientSide) {
                ItemStack removedRope = farmer.removeTopRope();
                if (!removedRope.isEmpty()) {
                    ItemHandlerHelper.giveItemToPlayer(player, removedRope);
                }
                level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 0.8F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (player.isShiftKeyDown() && farmer.easyVillagers().getCrop(registries) != null) {
            if (!level.isClientSide) {
                ItemStack removed = farmer.removeSelectedCrop(registries);
                if (!removed.isEmpty()) {
                    ItemHandlerHelper.giveItemToPlayer(player, removed);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (player.isShiftKeyDown() && farmer.easyVillagers().hasVillager(registries)) {
            if (!level.isClientSide) {
                ItemStack villager = farmer.easyVillagers().removeVillager(registries);
                if (!villager.isEmpty()) {
                    ItemHandlerHelper.giveItemToPlayer(player, villager);
                }
                farmer.setChanged();
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        openOutput(level, pos, player, farmer, state);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void openOutput(Level level, BlockPos pos, Player player, CompatFarmerBlockEntity farmer, BlockState state) {
        if (level.isClientSide) {
            return;
        }
        player.openMenu(new SimpleMenuProvider(
                (id, inventory, menuPlayer) -> farmer.easyVillagers().createOutputMenu(
                        id,
                        inventory,
                        level,
                        this,
                        level.registryAccess()
                ),
                Component.translatable(state.getBlock().getDescriptionId())
        ));
    }

    private static boolean isRice(ItemStack stack) {
        return RICE_ITEM_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    private static boolean isTomatoSeeds(ItemStack stack) {
        return TOMATO_SEEDS_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    private static boolean isRope(ItemStack stack) {
        return ROPE_ITEM_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    private static void consumeOne(ItemStack stack, Player player) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ItemStack stack = new ItemStack(this);
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof CompatFarmerBlockEntity compatFarmer) {
            compatFarmer.saveToItem(stack, params.getLevel().registryAccess());
        }
        return List.of(stack);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(this);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CompatFarmerBlockEntity compatFarmer) {
            compatFarmer.saveToItem(stack, level.registryAccess());
        }
        return stack;
    }
}
