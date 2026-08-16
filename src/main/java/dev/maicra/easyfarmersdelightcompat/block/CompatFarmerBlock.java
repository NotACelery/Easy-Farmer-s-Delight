package dev.maicra.easyfarmersdelightcompat.block;

import dev.maicra.easyfarmersdelightcompat.blockentity.CompatFarmerBlockEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Base block for every farmer owned by this addon.
 *
 * The geometry/assets are ours. Easy Villagers is only an external dependency and
 * interoperability source; this class does not extend or copy any Easy Villagers block.
 */
public final class CompatFarmerBlock extends Block implements EntityBlock {
    private final FarmerVariant variant;

    public CompatFarmerBlock(Properties properties, FarmerVariant variant) {
        super(properties);
        this.variant = variant;
    }

    public FarmerVariant variant() {
        return variant;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CompatFarmerBlockEntity(pos, state);
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
