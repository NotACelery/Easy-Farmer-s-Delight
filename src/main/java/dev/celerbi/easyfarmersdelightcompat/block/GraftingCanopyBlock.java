package dev.celerbi.easyfarmersdelightcompat.block;

import dev.celerbi.easyfarmersdelightcompat.blockentity.GraftingSupportBlockEntity;
import dev.celerbi.easyfarmersdelightcompat.integration.FarmerToolSupport;
import dev.celerbi.easyfarmersdelightcompat.registry.ModBlocks;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Invisible upper half reserved by a placed Grafting Support.
 *
 * <p>The actual canopy is rendered by the lower support block entity. This
 * marker supplies the real selectable/collidable leaf volume and is itself
 * breakable. Breaking it clears only the canopy; the support below remains.</p>
 */
public final class GraftingCanopyBlock extends Block {
    // Standalone renderer: the raised 0.68-block canopy extends about 7 px
    // into the upper reserved block. X/Z match the same scaled leaf cube, so
    // outline, interaction and collision follow the visible leaf mass.
    private static final VoxelShape CANOPY_SHAPE = Block.box(2.5D, 0.0D, 2.5D, 13.5D, 7.0D, 13.5D);

    public GraftingCanopyBlock(Properties properties) {
        super(properties);
    }

    private static VoxelShape canopyShape(BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos.below()) instanceof GraftingSupportBlockEntity support && support.hasCanopy()) {
            return CANOPY_SHAPE;
        }
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return canopyShape(level, pos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return canopyShape(level, pos);
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return canopyShape(level, pos);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return canopyShape(level, pos);
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return canopyShape(level, pos);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
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
        return GraftingSupportBlock.interactAt(level, pos.below(), player, hand, heldItem);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide
                && level.getBlockEntity(pos.below()) instanceof GraftingSupportBlockEntity support
                && support.hasCanopy()) {
            ItemStack canopy = support.removeCanopy();
            ItemStack tool = player.getMainHandItem();
            if (!player.getAbilities().instabuild && !canopy.isEmpty() && canRecoverCanopy(level, tool)) {
                Block.popResource(level, pos, canopy);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    public static boolean canRecoverCanopy(Level level, ItemStack tool) {
        if (FarmerToolSupport.isShears(tool)) {
            return true;
        }
        if (tool == null || tool.isEmpty()) {
            return false;
        }
        return level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(Enchantments.SILK_TOUCH)
                .map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, tool) > 0)
                .orElse(false);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (direction == Direction.DOWN && !neighborState.is(ModBlocks.GRAFTING_SUPPORT.get())) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos.below()) instanceof GraftingSupportBlockEntity support && support.hasCanopy()) {
            return support.canopyStack();
        }
        return new ItemStack(ModBlocks.GRAFTING_SUPPORT_ITEM.get());
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        // Drops are handled from playerWillDestroy because the real leaf stack
        // lives in the lower support block entity, not in this marker block.
        return List.of();
    }
}
