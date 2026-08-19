package dev.celerbi.easyfarmersdelightcompat.blockentity;

import dev.celerbi.easyfarmersdelightcompat.integration.AxeActionResolver;
import dev.celerbi.easyfarmersdelightcompat.integration.CutterLogVariant;
import dev.celerbi.easyfarmersdelightcompat.integration.CutterVillagerAdapter;
import dev.celerbi.easyfarmersdelightcompat.integration.CuttingRecipeResolver;
import dev.celerbi.easyfarmersdelightcompat.integration.FarmerToolSupport;
import dev.celerbi.easyfarmersdelightcompat.integration.OutputSimulator;
import dev.celerbi.easyfarmersdelightcompat.registry.ModBlockEntities;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

/** Automated Farmer's Delight Cutting Board enclosure powered by one adult villager. */
public final class CutterBlockEntity extends BlockEntity {
    public static final int PROCESS_TICKS=10, INPUT_SLOTS=4, OUTPUT_SLOTS=4;
    private static final String KEY_VILLAGER="CutterVillager", KEY_TOOL="CutterTool", KEY_INPUT="CutterInput", KEY_OUTPUT="CutterOutput", KEY_PROGRESS="CutterProgress";
    private final CutterVillagerAdapter villagerAdapter=new CutterVillagerAdapter(this);
    private ItemStack villager=ItemStack.EMPTY;
    private Block logVariant=Blocks.OAK_LOG;
    private int progress;
    private boolean loadingState;

    private final ItemStackHandler tool=new ItemStackHandler(1){
        @Override public boolean isItemValid(int slot,ItemStack stack){return FarmerToolSupport.isProcessingTool(stack);}
        @Override public int getSlotLimit(int slot){return 1;}
        @Override protected void onContentsChanged(int slot){if(!loadingState)setChangedAndSync();}
    };
    private final ItemStackHandler input=new ItemStackHandler(INPUT_SLOTS){@Override protected void onContentsChanged(int slot){if(!loadingState)setChangedAndSync();}};
    private final ItemStackHandler output=new ItemStackHandler(OUTPUT_SLOTS){@Override protected void onContentsChanged(int slot){if(!loadingState)setChangedAndSync();}};
    private final IItemHandler topAutomation=new TopInsertHandler(tool,input);
    private final IItemHandler sideAutomation=new InputOnlyHandler(input);
    private final IItemHandler bottomAutomation=new OutputOnlyHandler(output);

    public CutterBlockEntity(BlockPos pos,BlockState state){super(ModBlockEntities.CUTTER.get(),pos,state);}

    public static void serverTick(ServerLevel level,BlockPos pos,BlockState state,CutterBlockEntity cutter){
        if(cutter.hasVillager()){
            cutter.villagerAdapter.advanceAge();
            if(level.getGameTime()%20L==0L){cutter.villagerAdapter.flushToOwner();cutter.syncBlock();}
        }
        if(!cutter.canWork()){cutter.setProgress(0);return;}
        cutter.setProgress(cutter.progress+1); if(cutter.progress<PROCESS_TICKS)return;
        cutter.setProgress(0); cutter.tryProcess(level);
    }
    private boolean canWork(){return villagerAdapter.hasAdultVillager()&&FarmerToolSupport.isProcessingTool(tool.getStackInSlot(0))&&hasAnyInput();}
    private boolean hasAnyInput(){for(int i=0;i<input.getSlots();i++)if(!input.getStackInSlot(i).isEmpty())return true;return false;}
    private void tryProcess(ServerLevel level){
        ItemStack equipped=tool.getStackInSlot(0); int fortune=fortuneLevel(level,equipped);
        for(int slot=0;slot<input.getSlots();slot++){
            ItemStack source=input.getStackInSlot(slot); if(source.isEmpty())continue;
            Optional<CuttingRecipeResolver.Result> cutting=CuttingRecipeResolver.resolve(level,source,equipped,fortune);
            if(cutting.isPresent()){
                var result=cutting.get(); if(OutputSimulator.canFitAll(output,result.outputs())){completeOperation(level,slot,result.outputs(),result.sound().orElse(SoundEvents.VILLAGER_WORK_BUTCHER));return;} continue;
            }
            Optional<AxeActionResolver.Result> axe=AxeActionResolver.resolve(source,equipped);
            if(axe.isPresent()){
                var result=axe.get(); List<ItemStack> results=List.of(result.output()); if(OutputSimulator.canFitAll(output,results)){completeOperation(level,slot,results,result.sound());return;}
            }
        }
    }
    private void completeOperation(ServerLevel level,int inputSlot,List<ItemStack> results,SoundEvent sound){
        if(!OutputSimulator.canFitAll(output,results))return;
        ItemStack source=input.getStackInSlot(inputSlot); if(source.isEmpty())return; source.shrink(1); input.setStackInSlot(inputSlot,source);
        if(!OutputSimulator.insertAll(output,results)){source.grow(1);input.setStackInSlot(inputSlot,source);return;}
        ItemStack equipped=tool.getStackInSlot(0);
        if(!equipped.isEmpty()&&equipped.isDamageableItem()){
            equipped.hurtAndBreak(1,level,null,broken->level.playSound(null,worldPosition,SoundEvents.ITEM_BREAK,SoundSource.BLOCKS,.8F,1F));
            tool.setStackInSlot(0,equipped);
        }
        level.playSound(null,worldPosition,sound,SoundSource.BLOCKS,.8F,1F); setChangedAndSync();
    }
    private static int fortuneLevel(ServerLevel level,ItemStack stack){
        if(stack.isEmpty())return 0; try{Holder<Enchantment> fortune=level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);return EnchantmentHelper.getItemEnchantmentLevel(fortune,stack);}catch(RuntimeException e){return 0;}
    }

    public boolean isVillagerItem(ItemStack stack){return villagerAdapter.isVillagerItem(stack);}
    public boolean hasVillager(){return !villager.isEmpty();}
    public boolean insertVillager(ItemStack stack){if(hasVillager()||!isVillagerItem(stack))return false;villager=stack.copyWithCount(1);villagerAdapter.reset();setChangedAndSync();return true;}
    public ItemStack removeVillager(){if(villager.isEmpty())return ItemStack.EMPTY;villagerAdapter.flushToOwner();ItemStack r=villager.copyWithCount(1);villager=ItemStack.EMPTY;villagerAdapter.reset();setChangedAndSync();return r;}
    public ItemStack getStoredVillager(){return villager;}
    public void updateVillagerFromAdapter(ItemStack stack){if(stack!=null&&!stack.isEmpty()){villager=stack.copyWithCount(1);setChanged();}}
    public CutterVillagerAdapter villagerAdapter(){return villagerAdapter;}
    public Block logVariant(){return logVariant;}
    public void setLogVariant(Block log){Block normalized=log==null?Blocks.OAK_LOG:log;if(logVariant!=normalized){logVariant=normalized;setChangedAndSync();}}
    public boolean hasStoredContents(){return !villager.isEmpty()||progress!=0||!handlerEmpty(tool)||!handlerEmpty(input)||!handlerEmpty(output);}
    public ItemStackHandler toolHandler(){return tool;} public ItemStackHandler inputHandler(){return input;} public ItemStackHandler outputHandler(){return output;}
    public IItemHandler getAutomationHandler(Direction direction){return direction==Direction.DOWN?bottomAutomation:direction==Direction.UP?topAutomation:sideAutomation;}
    public ItemStack displayInput(){if(!hasVillager()||!FarmerToolSupport.isProcessingTool(tool.getStackInSlot(0)))return ItemStack.EMPTY;for(int i=0;i<input.getSlots();i++){ItemStack s=input.getStackInSlot(i);if(!s.isEmpty())return s.copyWithCount(1);}return ItemStack.EMPTY;}
    public int progress(){return progress;}
    private void setProgress(int value){int v=Math.max(0,Math.min(PROCESS_TICKS,value));if(progress!=v){progress=v;setChanged();}}

    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){
        super.saveAdditional(tag,registries);villagerAdapter.flushToOwner();CutterLogVariant.write(tag,logVariant);
        if(!villager.isEmpty())tag.put(KEY_VILLAGER,villager.save(registries)); if(!handlerEmpty(tool))tag.put(KEY_TOOL,tool.serializeNBT(registries)); if(!handlerEmpty(input))tag.put(KEY_INPUT,input.serializeNBT(registries)); if(!handlerEmpty(output))tag.put(KEY_OUTPUT,output.serializeNBT(registries)); if(progress!=0)tag.putInt(KEY_PROGRESS,progress);
    }
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){
        super.loadAdditional(tag,registries);logVariant=CutterLogVariant.read(tag);villager=tag.contains(KEY_VILLAGER)?ItemStack.parseOptional(registries,tag.getCompound(KEY_VILLAGER)):ItemStack.EMPTY;
        loadingState=true;try{clear(tool);clear(input);clear(output);if(tag.contains(KEY_TOOL,Tag.TAG_COMPOUND))tool.deserializeNBT(registries,tag.getCompound(KEY_TOOL));if(tag.contains(KEY_INPUT,Tag.TAG_COMPOUND))input.deserializeNBT(registries,tag.getCompound(KEY_INPUT));if(tag.contains(KEY_OUTPUT,Tag.TAG_COMPOUND))output.deserializeNBT(registries,tag.getCompound(KEY_OUTPUT));}finally{loadingState=false;}
        progress=Math.max(0,Math.min(PROCESS_TICKS,tag.getInt(KEY_PROGRESS)));villagerAdapter.reset();
    }
    private static boolean handlerEmpty(ItemStackHandler h){for(int i=0;i<h.getSlots();i++)if(!h.getStackInSlot(i).isEmpty())return false;return true;}
    private static void clear(ItemStackHandler h){for(int i=0;i<h.getSlots();i++)h.setStackInSlot(i,ItemStack.EMPTY);}
    @Override public Packet<ClientGamePacketListener> getUpdatePacket(){return ClientboundBlockEntityDataPacket.create(this);}
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries){return saveCustomOnly(registries);}
    private void setChangedAndSync(){setChanged();syncBlock();}
    private void syncBlock(){Level level=getLevel();if(level!=null&&!level.isClientSide){BlockState state=getBlockState();level.sendBlockUpdated(worldPosition,state,state,3);}}

    private static final class InputOnlyHandler implements IItemHandler{
        private final IItemHandler d;InputOnlyHandler(IItemHandler d){this.d=d;}public int getSlots(){return d.getSlots();}public ItemStack getStackInSlot(int s){return d.getStackInSlot(s);}public ItemStack insertItem(int s,ItemStack stack,boolean sim){return FarmerToolSupport.isProcessingTool(stack)?stack:d.insertItem(s,stack,sim);}public ItemStack extractItem(int s,int a,boolean sim){return ItemStack.EMPTY;}public int getSlotLimit(int s){return d.getSlotLimit(s);}public boolean isItemValid(int s,ItemStack stack){return !FarmerToolSupport.isProcessingTool(stack)&&d.isItemValid(s,stack);}
    }
    private static final class TopInsertHandler implements IItemHandler{
        private final IItemHandler tool,input;TopInsertHandler(IItemHandler tool,IItemHandler input){this.tool=tool;this.input=input;}public int getSlots(){return 1+input.getSlots();}public ItemStack getStackInSlot(int s){return s==0?tool.getStackInSlot(0):input.getStackInSlot(s-1);}public ItemStack insertItem(int s,ItemStack stack,boolean sim){if(s==0)return tool.insertItem(0,stack,sim);if(s<1||s>=getSlots()||FarmerToolSupport.isProcessingTool(stack))return stack;return input.insertItem(s-1,stack,sim);}public ItemStack extractItem(int s,int a,boolean sim){return ItemStack.EMPTY;}public int getSlotLimit(int s){return s==0?tool.getSlotLimit(0):input.getSlotLimit(s-1);}public boolean isItemValid(int s,ItemStack stack){return s==0?tool.isItemValid(0,stack):s>=1&&s<getSlots()&&!FarmerToolSupport.isProcessingTool(stack)&&input.isItemValid(s-1,stack);}
    }
    private static final class OutputOnlyHandler implements IItemHandler{
        private final IItemHandler d;OutputOnlyHandler(IItemHandler d){this.d=d;}public int getSlots(){return d.getSlots();}public ItemStack getStackInSlot(int s){return d.getStackInSlot(s);}public ItemStack insertItem(int s,ItemStack stack,boolean sim){return stack;}public ItemStack extractItem(int s,int a,boolean sim){return d.extractItem(s,a,sim);}public int getSlotLimit(int s){return d.getSlotLimit(s);}public boolean isItemValid(int s,ItemStack stack){return false;}
    }
}
