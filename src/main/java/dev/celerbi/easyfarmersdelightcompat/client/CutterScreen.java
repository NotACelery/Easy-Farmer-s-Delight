package dev.celerbi.easyfarmersdelightcompat.client;

import dev.celerbi.easyfarmersdelightcompat.blockentity.CutterBlockEntity;
import dev.celerbi.easyfarmersdelightcompat.menu.CutterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class CutterScreen extends AbstractContainerScreen<CutterMenu> {
    private static final ResourceLocation BACKGROUND=ResourceLocation.fromNamespaceAndPath("easy_villagers","textures/gui/container/input_output.png");
    private static final ResourceLocation EASY_OUTPUT=ResourceLocation.fromNamespaceAndPath("easy_villagers","textures/gui/container/output.png");
    private static final ResourceLocation EMPTY_TOOL=ResourceLocation.fromNamespaceAndPath("easyfarmersdelightcompat","textures/item/empty_knife_slot.png");
    public CutterScreen(CutterMenu menu,Inventory inv,Component title){super(menu,inv,title);imageWidth=176;imageHeight=164;inventoryLabelX=8;inventoryLabelY=71;}
    @Override public void render(GuiGraphics g,int mx,int my,float pt){super.render(g,mx,my,pt);renderTooltip(g,mx,my);}
    @Override protected void renderBg(GuiGraphics g,float pt,int mx,int my){
        int x=leftPos,y=topPos;g.blit(BACKGROUND,x,y,0,0,imageWidth,imageHeight);g.blit(EASY_OUTPUT,x+141,y+19,51,19,18,18);
        if(!menu.getSlot(CutterMenu.TOOL_SLOT).hasItem())g.blit(EMPTY_TOOL,x+142,y+20,0,0,16,16,16,16);
        int w=Math.round(16F*menu.progress()/CutterBlockEntity.PROCESS_TICKS);g.fill(x+142,y+42,x+142+w,y+44,0xFF6B8E23);
    }
    @Override protected void renderLabels(GuiGraphics g,int mx,int my){g.drawString(font,title,8,9,0x404040,false);center(g,Component.translatable("gui.easyfarmersdelightcompat.cutter.input"),88,9);center(g,Component.translatable("gui.easyfarmersdelightcompat.cutter.output"),88,40);g.drawString(font,playerInventoryTitle,inventoryLabelX,inventoryLabelY,0x404040,false);}
    private void center(GuiGraphics g,Component text,int x,int y){g.drawString(font,text,x-font.width(text)/2,y,0x404040,false);}
}
